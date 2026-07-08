#!/bin/bash
# ============================================================
# restart-after-boot.sh
# Chạy script này SAU KHI khởi động lại máy và bật lại K3s
# để đảm bảo Kafka, Zookeeper và các dịch vụ phục hồi đúng cách.
# Cách dùng: wsl -d Ubuntu bash /mnt/f/Devops/yas/scripts/restart-after-boot.sh
# ============================================================

set -e
export KUBECONFIG=/etc/rancher/k3s/k3s.yaml

echo "================================================================"
echo " YAS Cluster - Post-Boot Recovery Script"
echo "================================================================"

# ── BƯỚC 1: Chờ CoreDNS sẵn sàng ──────────────────────────────────
echo ""
echo "[1/4] Đang chờ CoreDNS sẵn sàng..."
kubectl rollout status deployment/coredns -n kube-system --timeout=120s
echo "    ✓ CoreDNS đã sẵn sàng"

# Chờ thêm 10s để đảm bảo DNS propagation
sleep 10

# ── BƯỚC 2: Xóa Zookeeper và Kafka để Strimzi tạo lại ─────────────
echo ""
echo "[2/4] Đang xóa Zookeeper và Kafka để tạo lại với cấu hình mới..."
kubectl delete pods -n kafka -l strimzi.io/cluster=kafka-cluster --ignore-not-found
echo "    ✓ Đã xóa Zookeeper/Kafka pods, Strimzi đang tạo lại..."

# ── BƯỚC 3: Chờ Zookeeper chạy trước ──────────────────────────────
echo ""
echo "[3/4] Đang chờ Zookeeper Running (tối đa 3 phút)..."
for i in $(seq 1 36); do
    STATUS=$(kubectl get pod kafka-cluster-zookeeper-0 -n kafka -o jsonpath='{.status.phase}' 2>/dev/null || echo "NotFound")
    READY=$(kubectl get pod kafka-cluster-zookeeper-0 -n kafka -o jsonpath='{.status.containerStatuses[0].ready}' 2>/dev/null || echo "false")
    if [ "$STATUS" = "Running" ] && [ "$READY" = "true" ]; then
        echo "    ✓ Zookeeper đã Running và Ready"
        break
    fi
    echo "    ... Zookeeper đang khởi động ($((i*5))s / 180s)"
    sleep 5
done

# ── BƯỚC 4: Kiểm tra trạng thái cuối ──────────────────────────────
echo ""
echo "[4/4] Trạng thái các dịch vụ sau khi khởi động lại:"
echo ""
echo "--- kafka namespace ---"
kubectl get pods -n kafka

echo ""
echo "--- dev namespace ---"
kubectl get pods -n dev

echo ""
echo "================================================================"
echo " ✓ Hoàn tất! Kafka sẽ tự phục hồi trong vài phút tiếp theo."
echo " Nếu Kafka vẫn còn CrashLoopBackOff sau 5 phút, chạy lại script."
echo "================================================================"
