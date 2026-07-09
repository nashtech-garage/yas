#!/bin/bash
# Khong dung 'set -e' vi script co cac lenh du kien that bai (grep, wget 403...)

NS="dev"

echo "====================================================================="
echo "  KIEM TRA SERVICE MESH (ISTIO) TREN MOI TRUONG $NS"
echo "====================================================================="

function write_step {
    echo -e "\n\e[1;36m=====================================================================\e[0m"
    echo -e "\e[1;36m$1\e[0m"
    echo -e "\e[1;36m=====================================================================\e[0m"
}

function write_result {
    if [ "$2" == "true" ]; then
        echo -e "  [PASS] $1 : $3"
    else
        echo -e "  [FAIL] $1 : $3"
    fi
}

# =====================================================================
# KIEM TRA TRANG THAI PODS
# =====================================================================
write_step "KIEM TRA TRANG THAI: Pods 2/2 (co Envoy sidecar)"
echo -e "\e[33m  Pods trong namespace $NS :\e[0m"
kubectl get pods -n $NS -o wide
echo ""
echo -e "\e[33m  Neu pod nao khong phai 2/2 thi Istio injection chua hoat dong\e[0m"
echo -e "\e[90m  Fix: kubectl rollout restart deployment/<service> -n $NS\e[0m"

sleep 3

# =====================================================================
# TEST 1: mTLS STRICT - Pod ngoai mesh bi chan
# =====================================================================
write_step "TEST 1: mTLS STRICT - Pod ngoai mesh bi chan"
echo -e "\e[33m  Tao pod o namespace default (NGOAI mesh, khong co Envoy)...\e[0m"
echo -e "\e[33m  Goi product service, phai bi Connection Reset...\e[0m"
echo ""

kubectl delete pod mtls-test --namespace=default --ignore-not-found=true 2>/dev/null || true
sleep 2

kubectl run mtls-test --image=curlimages/curl --namespace=default --restart=Never -- curl -sv --max-time 5 "http://product.$NS.svc.cluster.local/product/storefront/products/featured" 2>&1 > /dev/null || true

waited=0
podPhase=""
while [[ "$podPhase" != "Succeeded" && "$podPhase" != "Failed" && $waited -lt 15 ]]; do
    sleep 2
    waited=$((waited + 2))
    podPhase=$(kubectl get pod mtls-test -n default -o jsonpath='{.status.phase}' 2>/dev/null || true)
done

mtlsResult=$(kubectl logs mtls-test --namespace=default 2>&1 || true)
kubectl delete pod mtls-test --namespace=default --ignore-not-found=true 2>/dev/null || true

if echo "$mtlsResult" | grep -qE "reset|refused|Connection reset|timed out|timeout|connection reset by peer"; then
    write_result "mTLS chan traffic tu ngoai mesh" "true" "Connection bi reset/refused (mTLS STRICT dang hoat dong)"
else
    write_result "mTLS chan traffic tu ngoai mesh" "false" "Traffic KHONG bi chan: $mtlsResult"
fi

sleep 3

# =====================================================================
# TEST 2: AuthorizationPolicy - Service khong duoc phep bi 403
# =====================================================================
write_step "TEST 2: AuthorizationPolicy - Service khong duoc phep bi 403"
echo -e "\e[33m  cart goi customer, phai bi 403 (cart khong nam trong allowedCallers cua customer)\e[0m"
echo ""

denyResult=$(kubectl exec -n $NS deployment/cart -c cart -- wget -S -O /dev/null --timeout=5 "http://customer.$NS.svc.cluster.local/customer/storefront/customers/profile" 2>&1 || true)

if echo "$denyResult" | grep -qE "403|RBAC|denied|forbidden"; then
    write_result "AuthzPolicy chan service khong duoc phep" "true" "HTTP 403 - RBAC: access denied"
else
    write_result "AuthzPolicy chan service khong duoc phep" "false" "Response: $denyResult"
fi

sleep 3

# =====================================================================
# TEST 3: AuthorizationPolicy - Service DUOC phep tra ve 200
# =====================================================================
write_step "TEST 3: AuthorizationPolicy - Service DUOC phep tra ve 200"
echo -e "\e[33m  storefront-bff goi product, phai duoc 200 (co trong allowedCallers)\e[0m"
echo ""

allowResult=$(kubectl exec -n $NS deployment/storefront-bff -c storefront-bff -- wget -S -O - --timeout=15 "http://product.$NS.svc.cluster.local/product/storefront/products/featured" 2>&1 || true)

if echo "$allowResult" | grep -qE "200 OK|productList|totalPage|pageNumber"; then
    write_result "AuthzPolicy cho phep service hop le" "true" "HTTP 200 - Du lieu JSON tra ve thanh cong"
else
    write_result "AuthzPolicy cho phep service hop le" "false" "Response: $allowResult"
fi

sleep 3

# =====================================================================
# TEST 4: Retry Policy - Inject loi 503 va kiem tra retry
# =====================================================================
write_step "TEST 4: Retry Policy - Inject loi 503 va kiem tra retry"
echo -e "\e[33m  Buoc 4a: Apply Fault Injection (30% loi 503 vao product)...\e[0m"

cat <<EOF | kubectl apply -f -
apiVersion: networking.istio.io/v1beta1
kind: VirtualService
metadata:
  name: product-fault-injection
  namespace: $NS
spec:
  hosts:
    - product
  http:
    - fault:
        abort:
          percentage:
            value: 30
          httpStatus: 503
      retries:
        attempts: 3
        perTryTimeout: 5s
        retryOn: 5xx,gateway-error,connect-failure
      timeout: 20s
      route:
        - destination:
            host: product
            port:
              number: 80
EOF

echo -e "\e[33m  Cho policy propagate (10s)...\e[0m"
sleep 10

echo -e "\e[33m  Buoc 4b: Gui 10 requests storefront-bff -> product...\e[0m"
success=0
fail=0
for i in $(seq 1 10); do
    retryResult=$(kubectl exec -n $NS deployment/storefront-bff -c storefront-bff -- wget -S -O - --timeout=25 "http://product.$NS.svc.cluster.local/product/storefront/products/featured" 2>&1 || true)
    if echo "$retryResult" | grep -qE "200 OK|productList|totalPage|pageNumber"; then
        success=$((success + 1))
        echo -e "    \e[32mRequest $i -> 200 OK (retry thanh cong)\e[0m"
    else
        fail=$((fail + 1))
        echo -e "    \e[31mRequest $i -> Failed (503 khong duoc retry)\e[0m"
    fi
    sleep 1
done

echo ""
echo -e "\e[33m  Ket qua: $success/10 thanh cong | $fail/10 that bai\e[0m"
if [ $success -ge 8 ]; then
    write_result "Retry Policy hap thu loi 503" "true" ">=80% requests thanh cong du 30% bi inject loi, Envoy retry hoat dong"
else
    write_result "Retry Policy hap thu loi 503" "false" "Chi $success/10 thanh cong. Kiem tra VirtualService retry config"
fi

echo ""
echo -e "\e[33m  Buoc 4c: Don dep Fault Injection...\e[0m"
kubectl delete virtualservice product-fault-injection -n $NS 2>/dev/null || true
echo -e "\e[32m  Da xoa fault injection, product tro lai binh thuong\e[0m"

# =====================================================================
# TOM TAT KET QUA
# =====================================================================
write_step "TOM TAT KET QUA"
echo ""
echo -e "\e[36m  TEST 1: mTLS          -> Pod ngoai bi chan\e[0m"
echo -e "\e[36m  TEST 2: AuthzPolicy   -> Service sai bi 403\e[0m"
echo -e "\e[36m  TEST 3: AuthzPolicy   -> Service dung duoc 200\e[0m"
echo -e "\e[36m  TEST 4: Retry Policy  -> Retry hap thu loi\e[0m"
echo ""
echo -e "\e[36m  Mo Kiali de chup screenshot topology:\e[0m"
echo -e "\e[97m    sudo kubectl port-forward --address 0.0.0.0 svc/kiali 20001:20001 -n istio-system\e[0m"
echo -e "\e[97m    http://<Public-IP>:20001 -> Graph -> Namespace: dev\e[0m"
