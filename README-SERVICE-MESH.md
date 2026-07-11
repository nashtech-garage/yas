# 📦 YAS – Service Mesh Engineering: Tuần 1 (04/06 – 10/06)

> **Phụ trách:** Thanh Phong – System & Service Mesh Engineer  
> **Mục tiêu tuần 1:** Phân tích kiến trúc YAS, vẽ service flow, và lập kế hoạch áp dụng Istio/Kiali.

---

## 📋 Mục lục

1. [Phân tích kiến trúc YAS (Microservices Architecture)](#1-phân-tích-kiến-trúc-yas)
2. [Service Flow – Sơ đồ kết nối](#2-service-flow)
3. [Service Mesh Plan – Kế hoạch Istio/Kiali](#3-service-mesh-plan)

---

## 1. Phân tích kiến trúc YAS

### 1.1 Tổng quan

**YAS (Yet Another Shop)** là ứng dụng e-commerce demo được xây dựng theo kiến trúc **microservices** sử dụng Java/Spring Boot 4.0 và Next.js. Hệ thống bao gồm:

- **2 lớp Frontend:** Storefront (khách hàng) và Backoffice (quản trị)
- **2 lớp BFF (Backend For Frontend):** storefront-bff và backoffice-bff – đóng vai trò API Gateway
- **12+ backend microservices** chạy độc lập, mỗi service có database riêng
- **Hạ tầng hỗ trợ:** PostgreSQL, Kafka, Redis, Elasticsearch, Keycloak

### 1.2 Danh sách các Service chính

| # | Service | Công nghệ | Port nội bộ | Context Path | Database | Mô tả |
|---|---------|-----------|-------------|--------------|----------|-------|
| 1 | **nginx** | Nginx 1.27.2 | 80 | `/` | – | API Gateway / Reverse Proxy duy nhất ra ngoài |
| 2 | **identity** | Keycloak 26.0.2 | 80 | `/` | PostgreSQL (`keycloak`) | Xác thực & phân quyền (OAuth2/OIDC) |
| 3 | **storefront-nextjs** | Next.js | 80 | `/` | – | Giao diện khách hàng |
| 4 | **storefront** (BFF) | Spring Boot | 80 | – | – | BFF cho storefront, proxy + aggregator |
| 5 | **backoffice-nextjs** | Next.js | 80 | `/` | – | Giao diện quản trị |
| 6 | **backoffice** (BFF) | Spring Boot | 80 | – | – | BFF cho backoffice, proxy + aggregator |
| 7 | **product** | Spring Boot | 80 | `/product` | PostgreSQL (`product`) | Quản lý sản phẩm, danh mục, thương hiệu |
| 8 | **media** | Spring Boot | 80 | `/media` | PostgreSQL (`media`) | Upload & quản lý hình ảnh |
| 9 | **customer** | Spring Boot | 80 | `/customer` | PostgreSQL (`customer`) | Quản lý thông tin khách hàng |
| 10 | **cart** | Spring Boot | 80 | `/cart` | PostgreSQL (`cart`) | Giỏ hàng |
| 11 | **order** | Spring Boot | 80 | `/order` | PostgreSQL (`order`) | Đặt hàng, quản lý đơn hàng |
| 12 | **payment** | Spring Boot | 80 | `/payment` | PostgreSQL (`payment`) | Xử lý thanh toán |
| 13 | **payment-paypal** | Spring Boot | 80 | `/payment-paypal` | – | Tích hợp PayPal |
| 14 | **inventory** | Spring Boot | 80 | `/inventory` | PostgreSQL (`inventory`) | Quản lý tồn kho |
| 15 | **location** | Spring Boot | 80 | `/location` | PostgreSQL (`location`) | Địa lý (tỉnh/thành, quận/huyện) |
| 16 | **tax** | Spring Boot | 80 | `/tax` | PostgreSQL (`tax`) | Tính thuế theo địa điểm |
| 17 | **promotion** | Spring Boot | 80 | `/promotion` | PostgreSQL (`promotion`) | Khuyến mãi, coupon |
| 18 | **rating** | Spring Boot | 80 | `/rating` | PostgreSQL (`rating`) | Đánh giá sản phẩm |
| 19 | **search** | Spring Boot | 80 | `/search` | Elasticsearch | Tìm kiếm sản phẩm full-text |
| 20 | **sampledata** | Spring Boot | 80 | `/sampledata` | PostgreSQL (`product`, `media`) | Dữ liệu mẫu cho dev |
| 21 | **swagger-ui** | Swagger UI | 80 | `/swagger-ui` | – | Tài liệu API tổng hợp |

### 1.3 Hạ tầng hỗ trợ (Infrastructure Services)

| Service | Image | Port | Vai trò |
|---------|-------|------|---------|
| **postgres** | debezium/postgres:16 | 5444 (host) | Shared PostgreSQL – mỗi service dùng 1 database riêng |
| **pgadmin** | dpage/pgadmin4 | 80 | Web UI quản trị PostgreSQL |
| **redis** | redis:7.4.1-alpine | 6379 | Cache (dùng cho cart/session) |
| **kafka** | confluentinc/cp-kafka:7.7.1 | 9092, 29092 | Message broker (CDC via Debezium) |
| **zookeeper** | debezium/zookeeper | 2181, 2888, 3888 | Điều phối Kafka cluster |
| **kafka-connect** | debezium/connect:2.7.3 | 8083 | Kafka Connect – Debezium CDC connector |
| **kafka-ui** | provectuslabs/kafka-ui | 8089 | Web UI quản trị Kafka |
| **elasticsearch** | elasticsearch:9.2.3 | 9200, 9300 | Full-text search engine |

### 1.4 Observability Stack (docker-compose.o11y.yml)

| Service | Vai trò |
|---------|---------|
| **OpenTelemetry Collector** | Thu thập metrics/traces/logs từ tất cả services (gRPC :5555, HTTP :6666) |
| **Grafana** | Dashboard visualize metrics, logs, traces |
| **Prometheus** | Metrics scraping (:9090) |
| **Loki** | Log aggregation |
| **Tempo** | Distributed tracing backend |

### 1.5 Inter-Service Dependencies

```
cart         → product, media
order        → cart, customer, product, tax
payment      → order, media
rating       → product, customer, order
inventory    → product, location
promotion    → product
tax          → location
customer     → location
search       → product (via Kafka CDC → Elasticsearch)
storefront   → customer, cart, rating, order, location, inventory, tax, promotion, payment
backoffice   → (tất cả services qua nginx proxy)
```

### 1.6 Cơ chế xác thực

- **Keycloak** đóng vai trò Identity Provider (OAuth2/OIDC)
- Tất cả BFF đều kết nối với Keycloak để xác thực token
- Access URL: `http://identity/` (nội bộ) | `http://identity` (host mapping)
- Realm: `yas` – Client: `swagger-ui` (PKCE)

---

## 2. Service Flow

### 2.1 Luồng giao tiếp tổng thể

```
                    ┌─────────────────────────────────────────────┐
                    │              INTERNET / BROWSER              │
                    └─────────────┬───────────────┬───────────────┘
                                  │               │
                    ┌─────────────▼───────────────▼───────────────┐
                    │              NGINX (Port 80)                 │
                    │         api.yas.local | storefront           │
                    │         backoffice | identity                │
                    └──┬────────────────────────────────┬─────────┘
                       │                                │
          ┌────────────▼──────────┐      ┌─────────────▼────────────┐
          │  storefront-nextjs    │      │   backoffice-nextjs       │
          │     (Next.js UI)      │      │     (Next.js UI)          │
          └────────────┬──────────┘      └─────────────┬────────────┘
                       │ HTTP                           │ HTTP
          ┌────────────▼──────────┐      ┌─────────────▼────────────┐
          │   storefront-bff      │      │    backoffice-bff         │
          │  (Spring Boot BFF)    │      │   (Spring Boot BFF)       │
          │  - Auth: Keycloak     │      │   - Auth: Keycloak        │
          └────┬──────────────────┘      └──────────────┬───────────┘
               │                                        │
               │         HTTP (via nginx internal)      │
               ▼                                        ▼
  ┌────────────────────────────────────────────────────────────────┐
  │                   BACKEND MICROSERVICES LAYER                   │
  │                                                                  │
  │  ┌──────────┐  ┌──────────┐  ┌──────────┐  ┌──────────────┐   │
  │  │ product  │  │  media   │  │ customer │  │    cart      │   │
  │  │ :80/prod │  │ :80/med  │  │ :80/cust │  │  :80/cart    │   │
  │  └────┬─────┘  └────┬─────┘  └────┬─────┘  └──────┬───────┘   │
  │       │             │             │                │            │
  │  ┌────▼─────┐  ┌────▼─────┐  ┌───▼──────┐  ┌─────▼────────┐  │
  │  │  order   │  │ payment  │  │ location │  │  inventory   │  │
  │  │ :80/ord  │  │ :80/pay  │  │ :80/loc  │  │  :80/inv     │  │
  │  └────┬─────┘  └────┬─────┘  └────┬─────┘  └──────┬───────┘  │
  │       │             │             │                │            │
  │  ┌────▼─────┐  ┌────▼─────┐  ┌───▼──────┐  ┌─────▼────────┐  │
  │  │  rating  │  │   tax    │  │promotion │  │   search     │  │
  │  │ :80/rat  │  │ :80/tax  │  │ :80/promo│  │  :80/search  │  │
  │  └──────────┘  └──────────┘  └──────────┘  └──────────────┘  │
  └─────────────────┬──────────────────────┬───────────────────────┘
                    │                      │
       ┌────────────▼──────────┐    ┌──────▼──────────────────┐
       │  PostgreSQL (:5444)   │    │  Kafka (:9092)           │
       │  - product DB         │    │  ← Debezium CDC          │
       │  - order DB           │    │  → kafka-connect (:8083) │
       │  - customer DB        │    └──────┬──────────────────┘
       │  - cart DB            │           │ publish events
       │  - payment DB         │    ┌──────▼──────────────────┐
       │  - inventory DB       │    │  Elasticsearch (:9200)   │
       │  - location DB        │    │  ← search-service index  │
       │  - rating DB          │    └─────────────────────────┘
       │  - promotion DB       │
       │  - tax DB             │    ┌─────────────────────────┐
       │  - media DB           │    │  Redis (:6379)           │
       │  - keycloak DB        │    │  - Session cache         │
       └───────────────────────┘    └─────────────────────────┘
```

### 2.2 Luồng xử lý đơn hàng (Order Flow chi tiết)

```
User Browser
    │
    ▼
Nginx (port 80)
    │
    ▼
storefront-nextjs
    │  (fetch API)
    ▼
storefront-bff ──[auth token]──► identity (Keycloak)
    │
    ├──► cart service ──► product service ──► media service
    │         │
    │         └──► PostgreSQL (cart DB)
    │
    ├──► order service
    │         │──► cart service (get cart items)
    │         │──► customer service (get address)
    │         │──► product service (verify product)
    │         │──► tax service
    │         │         └──► location service
    │         └──► PostgreSQL (order DB)
    │
    ├──► payment service
    │         │──► order service (verify order)
    │         └──► PostgreSQL (payment DB)
    │
    └──► inventory service ──► product service
                                └──► location service
```

### 2.3 Luồng Search (CDC via Kafka)

```
product service
    │  (INSERT/UPDATE/DELETE)
    ▼
PostgreSQL (product DB)
    │  Debezium CDC
    ▼
kafka-connect (:8083)
    │  publish
    ▼
Kafka (:9092) [topic: yas.product.*]
    │  consume
    ▼
search service
    │  index documents
    ▼
Elasticsearch (:9200)
    │
    ▼
storefront → search service → Elasticsearch (full-text query)
```

### 2.4 Ports Summary

| Host Port | Service | Ghi chú |
|-----------|---------|---------|
| **80** | nginx | Entry point duy nhất (HTTP) |
| **5444** | postgres | PostgreSQL (host access) |
| **6379** | redis | Redis cache |
| **9092** | kafka | Kafka broker (PLAINTEXT) |
| **29092** | kafka | Kafka broker (PLAINTEXT_HOST) |
| **2181** | zookeeper | ZK client port |
| **8083** | kafka-connect | REST API Debezium |
| **8089** | kafka-ui | Kafka UI dashboard |
| **9200** | elasticsearch | REST API |
| **9300** | elasticsearch | Transport port |
| **5555** | otel-collector | gRPC receiver |
| **6666** | otel-collector | HTTP receiver |
| **3000** | grafana | Dashboard |
| **9090** | prometheus | Metrics |

---

## 3. Service Mesh Plan

### 3.1 Mục tiêu

Áp dụng **Istio Service Mesh** lên môi trường Kubernetes để đạt được:

- 🔒 **mTLS** – Mã hóa và xác thực mọi giao tiếp service-to-service
- 🔄 **Traffic Management** – Retry, timeout, circuit breaker tự động
- 🛡️ **AuthorizationPolicy** – Kiểm soát truy cập dựa trên identity (SPIFFE/X.509)
- 📊 **Observability** – Kiali dashboard + tích hợp Prometheus/Grafana/Jaeger có sẵn

### 3.2 Phạm vi Test (Service Mesh Test Scope)

#### Phase 1 – mTLS (Mutual TLS)

**Services được chọn để test mTLS:**

| Service | Lý do chọn | Giao tiếp cần bảo vệ |
|---------|-----------|----------------------|
| `order` | Service trung tâm, nhiều dependency | → cart, customer, product, tax |
| `payment` | Nhạy cảm nhất (tài chính) | → order, media |
| `cart` | Tần suất gọi cao | → product, media |

**Cấu hình mTLS (file: `istio/mtls/`):**

```yaml
# istio/mtls/peer-authentication.yaml
apiVersion: security.istio.io/v1beta1
kind: PeerAuthentication
metadata:
  name: yas-mtls-strict
  namespace: yas
spec:
  mtls:
    mode: STRICT
```

```yaml
# istio/mtls/destination-rule.yaml
apiVersion: networking.istio.io/v1alpha3
kind: DestinationRule
metadata:
  name: yas-services-mtls
  namespace: yas
spec:
  host: "*.yas.svc.cluster.local"
  trafficPolicy:
    tls:
      mode: ISTIO_MUTUAL
```

#### Phase 2 – Retry & Timeout Policy

**Services được chọn để test Retry:**

| Service | Retry config | Timeout | Lý do |
|---------|-------------|---------|-------|
| `product` | 3 lần, 2s delay | 10s | Được gọi nhiều nhất, ảnh hưởng cart/order/search |
| `tax` | 2 lần, 1s delay | 5s | Dependency của order, cần fail-fast |
| `inventory` | 3 lần, 2s delay | 10s | Đọc dữ liệu tồn kho, có thể retry an toàn |

**Cấu hình (file: `istio/traffic/`):**

```yaml
# istio/traffic/virtual-service-product.yaml
apiVersion: networking.istio.io/v1alpha3
kind: VirtualService
metadata:
  name: product-vs
  namespace: yas
spec:
  hosts:
    - product
  http:
    - retries:
        attempts: 3
        perTryTimeout: 2s
        retryOn: 5xx,reset,connect-failure
      timeout: 10s
```

#### Phase 3 – AuthorizationPolicy

**Chính sách truy cập giữa các services:**

| Service (đích) | Được phép gọi từ | Bị chặn |
|----------------|-----------------|---------|
| `payment` | `storefront-bff`, `order` | Tất cả service khác |
| `order` | `storefront-bff`, `payment`, `rating` | Tất cả service khác |
| `customer` | `storefront-bff`, `order`, `rating` | Tất cả service khác |
| `product` | Tất cả services (read) | Không (public read) |

**Cấu hình (file: `istio/security/`):**

```yaml
# istio/security/authz-payment.yaml
apiVersion: security.istio.io/v1beta1
kind: AuthorizationPolicy
metadata:
  name: payment-authz
  namespace: yas
spec:
  selector:
    matchLabels:
      app: payment
  rules:
    - from:
        - source:
            principals:
              - "cluster.local/ns/yas/sa/storefront-bff"
              - "cluster.local/ns/yas/sa/order"
```

### 3.3 Công cụ

| Công cụ | Vai trò | Access |
|---------|---------|--------|
| **Istio** | Service mesh control plane | `istioctl install` |
| **Kiali** | Topology graph, traffic flow visualization | `http://kiali.yas.local` |
| **Jaeger** | Distributed tracing (tích hợp Istio) | `http://jaeger.yas.local:16686` |
| **Prometheus** | Metrics từ Envoy sidecar | `http://prometheus.yas.local:9090` |
| **Grafana** | Dashboard Istio metrics | `http://grafana.yas.local` |

### 3.4 Lộ trình thực hiện tuần 1

```
Thứ 2-3 (04-05/06):  ✅ Phân tích kiến trúc, đọc docker-compose, xác định services
Thứ 4   (06/06):     ✅ Vẽ service flow diagram, xác định dependency
Thứ 5   (07/06):     ✅ Nghiên cứu Istio install on K8s, chuẩn bị test scope
Thứ 6   (08/06):     ✅ Viết các file YAML Istio (mtls, traffic, security)
Thứ 7   (09/06):     ✅ Hoàn thiện tài liệu, peer review
```

### 3.5 Cấu trúc thư mục Istio (đề xuất)

```
yas/
└── istio/
    ├── mtls/
    │   ├── peer-authentication.yaml     # STRICT mTLS toàn namespace
    │   └── destination-rule.yaml        # ISTIO_MUTUAL cho tất cả services
    ├── traffic/
    │   ├── virtual-service-product.yaml # Retry + timeout cho product
    │   ├── virtual-service-tax.yaml     # Retry + timeout cho tax
    │   └── virtual-service-inventory.yaml
    └── security/
        ├── authz-payment.yaml           # Chỉ order + storefront-bff gọi được
        ├── authz-order.yaml             # Chỉ storefront-bff + payment gọi được
        └── authz-customer.yaml          # Chỉ các service được phép
```

### 3.6 Tiêu chí thành công (Definition of Done)

- [ ] Kiali hiển thị topology graph với tất cả service connections
- [ ] Kiali hiển thị lock icon (🔒) trên mọi đường kết nối = mTLS hoạt động
- [ ] `order → cart` retry 3 lần khi cart service trả về 503
- [ ] `storefront-bff → payment` được phép; `product → payment` bị 403
- [ ] Jaeger traces hiển thị span từ storefront-bff → order → cart → product

---

## 📁 Tài liệu liên quan

- [README gốc của YAS](./README.md)
- [Kiến trúc local deployment](./yas-architecture-local.png)
- [docker-compose.yml](./docker-compose.yml) – Core services
- [docker-compose.search.yml](./docker-compose.search.yml) – Elasticsearch + Search service
- [docker-compose.o11y.yml](./docker-compose.o11y.yml) – Observability stack
- [k8s/](./k8s/) – Kubernetes deployment configs
- [istio/](./istio/) – Istio configs (mTLS, traffic, security)

---

> *Tài liệu được tạo bởi Thanh Phong – 09/06/2026*
