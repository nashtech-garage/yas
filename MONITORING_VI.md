# 🚀 HƯỚNG DẪN CHẠY DỰ ÁN VÀ KIỂM THỬ TÍNH NĂNG MONITORING

---

## **GIAI ĐOẠN 1: CHUẨN BỊ MÔI TRƯỜNG**

### **1.1 Kiểm Tra Yêu Cầu Hệ Thống**

```powershell
# Kiểm tra Docker
docker --version

# Kiểm tra Docker Compose
docker compose version

# Kiểm tra Git branch
cd "g:\devops pro\devops-yas-ci"
git branch
# Kết quả: * feature/monitoring
```

### **1.2 Cấu Hình File Hosts (Windows)**

Mở file `C:\Windows\System32\drivers\etc\hosts` với quyền admin và thêm:

```
127.0.0.1 identity
127.0.0.1 api.yas.local
127.0.0.1 pgadmin.yas.local
127.0.0.1 storefront
127.0.0.1 backoffice
127.0.0.1 loki
127.0.0.1 tempo
127.0.0.1 grafana
127.0.0.1 elasticsearch
127.0.0.1 kafka
```

---

## **GIAI ĐOẠN 2: KHỞI ĐỘNG NGĂN XẾP QUAN SÁT (OBSERVABILITY STACK)**

### **2.1 Khởi Động Prometheus & Grafana**

```powershell
cd "g:\devops pro\devops-yas-ci"

# Chạy các dịch vụ quan sát
docker compose -f docker-compose.o11y.yml up -d

# Hoặc chạy và xem logs thực thời
docker compose -f docker-compose.o11y.yml up
```

**Chờ 30-60 giây để tất cả dịch vụ khởi động**

### **2.2 Kiểm Tra Dịch Vụ Đã Khởi Động**

```powershell
# Xem containers đang chạy
docker compose -f docker-compose.o11y.yml ps

# Kết quả mong đợi:
# NAME              STATUS
# prometheus        Up 30s (healthy)
# grafana           Up 30s (healthy)
# loki              Up 30s
# tempo             Up 30s (healthy)
# collector         Up 30s
```

### **2.3 Kiểm Tra Cấu Hình Prometheus**

```powershell
# Kiểm tra config của Prometheus
curl http://localhost:9090/api/v1/config

# Kiểm tra alert rules được load
curl http://localhost:9090/api/v1/rules

# Kiểm tra scrape targets
curl http://localhost:9090/api/v1/targets
```

---

## **GIAI ĐOẠN 3: KHỞI ĐỘNG CÁC DỊCH VỤ CỐT LÕI**

### **3.1 Khởi Động PostgreSQL & Kafka (Terminal 1)**

```powershell
cd "g:\devops pro\devops-yas-ci"

# Chạy các dịch vụ cốt lõi
docker compose -f docker-compose.yml up

# Chờ tất cả dịch vụ sẵn sàng (5-10 phút)
# Kiểm tra logs xem có các thông báo:
#   ✅ PostgreSQL: "PostgreSQL 15.6"
#   ✅ Kafka: "Cluster Metadata initialization complete"
#   ✅ Redis: "Ready to accept connections"
```

### **3.2 Khởi Động Dịch Vụ BFF (Terminal 2)**

**Cách 1: Chạy từ IDE (IntelliJ IDEA)**

1. Mở dự án trong IDE
2. File → Mở → `backoffice-bff/pom.xml`
3. Chuột phải vào `BackofficeBffApplication.java`
4. Chạy → Chạy 'BackofficeBffApplication'

**Cách 2: Chạy từ Command Line**

```powershell
# Terminal 2
cd "g:\devops pro\devops-yas-ci\backoffice-bff"

# Xây dựng dự án
mvn clean install

# Chạy dịch vụ BFF
mvn spring-boot:run

# Hoặc nếu đã xây dựng:
java -jar target/backoffice-bff-1.0-SNAPSHOT.jar
```

**Chờ kết quả:**
```
2026-05-12 10:30:45.123  INFO 12345 --- [main] ...
Started BackofficeBffApplication in 15.234 seconds (JVM running for 18.567)
```

### **3.3 Kiểm Tra BFF Khởi Động**

```powershell
# Terminal 3 - Kiểm tra endpoint health
curl http://localhost:8087/actuator/health

# Kết quả mong đợi:
# {
#   "status": "UP",
#   "components": {
#     "bff-health": {
#       "status": "UP"
#     }
#   }
# }
```

---

## **GIAI ĐOẠN 4: KIỂM CHỨNG VIỆC COLLECT METRICS**

### **4.1 Kiểm Tra Tất Cả Metrics Có Sẵn**

```powershell
# Terminal 3
# Liệt kê tất cả metrics khả dụng
curl http://localhost:8087/actuator/metrics | ConvertFrom-Json | Select-Object -ExpandProperty names | Sort-Object

# Tìm các metrics BFF
curl http://localhost:8087/actuator/metrics | ConvertFrom-Json | Where-Object { $_ -match 'bff' }
```

### **4.2 Kiểm Tra Các Metrics Cụ Thể**

```powershell
# Bộ đếm yêu cầu
curl http://localhost:8087/actuator/metrics/bff.requests.total

# Độ trễ yêu cầu
curl http://localhost:8087/actuator/metrics/bff.request.latency

# Metrics cache
curl http://localhost:8087/actuator/metrics/bff.cache.hits
curl http://localhost:8087/actuator/metrics/bff.cache.misses
```

---

## **GIAI ĐOẠN 5: TẠO LƯU LƯỢNG VÀ KIỂM THỬ METRICS**

### **5.1 Tạo Lưu Lượng Yêu Cầu (Terminal 4)**

**Script PowerShell - Tạo Lưu Lượng:**

```powershell
# Tạo file: traffic-generator.ps1

# Danh sách URL đơn giản (thành công)
$urls = @(
    "http://localhost:8087/api/products",
    "http://localhost:8087/api/categories",
    "http://localhost:8087/api/customers"
)

# Tạo lưu lượng trong 5 phút
$duration = 5 * 60  # 5 phút tính bằng giây
$startTime = [DateTime]::Now
$requestCount = 0

Write-Host "Bắt đầu tạo lưu lượng trong $duration giây..."
Write-Host "Bắt đầu lúc $(Get-Date)"

while ((([DateTime]::Now) - $startTime).TotalSeconds -lt $duration) {
    foreach ($url in $urls) {
        try {
            $response = curl -s -o /dev/null -w "%{http_code}" $url
            $requestCount++
            Write-Host "[$requestCount] GET $url - Trạng thái: $response - Lúc: $(Get-Date -Format 'HH:mm:ss')"
            Start-Sleep -Milliseconds 500
        }
        catch {
            Write-Host "Lỗi: $_" -ForegroundColor Red
        }
    }
}

Write-Host "Hoàn thành tạo lưu lượng. Tổng yêu cầu: $requestCount"
```

**Chạy:**

```powershell
cd "g:\devops pro\devops-yas-ci"
.\traffic-generator.ps1
```

### **5.2 Song song: Tạo Lưu Lượng Lỗi**

```powershell
# Terminal 5 - Tạo lỗi (để kiểm thử alert)
# Tạo yêu cầu đến các endpoint không hợp lệ

$errorUrls = @(
    "http://localhost:8087/api/invalid-endpoint",
    "http://localhost:8087/api/404-not-found"
)

for ($i = 0; $i -lt 20; $i++) {
    foreach ($url in $errorUrls) {
        curl -s -o /dev/null $url
        Write-Host "Yêu cầu lỗi #$($i+1) đến $url"
        Start-Sleep -Milliseconds 200
    }
}

Write-Host "Hoàn thành tạo yêu cầu lỗi"
```

### **5.3 Tạo Yêu Cầu Độ Trễ Cao (Tùy Chọn)**

```powershell
# Terminal 6 - Kiểm thử độ trễ cao
# Tạo yêu cầu và chờ để kích hoạt cảnh báo độ trễ cao

for ($i = 0; $i -lt 10; $i++) {
    $start = Get-Date
    curl -s http://localhost:8087/api/products | Out-Null
    $elapsed = ((Get-Date) - $start).TotalMilliseconds
    Write-Host "Yêu cầu $($i+1) - Độ trễ: ${elapsed}ms"
    Start-Sleep -Seconds 1
}
```

---

## **GIAI ĐOẠN 6: GIÁM SÁT METRICS TRONG PROMETHEUS**

### **6.1 Giao Diện Prometheus**

**URL:** http://localhost:9090

**Các truy vấn để kiểm thử:**

```promql
# 1. Tỷ lệ yêu cầu
rate(bff_requests_total[5m])

# 2. Tỷ lệ lỗi
rate(bff_requests_errors[5m])

# 3. Độ trễ yêu cầu (phần trăm 95)
histogram_quantile(0.95, bff_request_latency_bucket)

# 4. Tỷ lệ cache hit
(bff_cache_hits / (bff_cache_hits + bff_cache_misses)) * 100

# 5. Tất cả metrics
{service="backoffice-bff"}
```

**Các bước:**

1. Truy cập http://localhost:9090
2. Bấm "Graph"
3. Nhập truy vấn (ví dụ: `rate(bff_requests_total[5m])`)
4. Bấm "Execute"
5. Chọn tab "Graph" để hiển thị biểu đồ

### **6.2 Kiểm Tra Trạng Thái Cảnh Báo**

**URL:** http://localhost:9090/alerts

**Cảnh báo dự kiến:**
- ✅ BFFHighErrorRate (nếu có nhiều lỗi)
- ✅ BFFHighLatency (nếu có yêu cầu chậm)
- ✅ DownstreamServiceError
- ℹ️ Các cảnh báo khác (tùy thuộc vào lưu lượng)

---

## **GIAI ĐOẠN 7: GIÁM SÁT METRICS TRONG GRAFANA**

### **7.1 Truy Cập Bảng Điều Khiển Grafana**

**URL:** http://grafana/d/bff-overview

**Đăng nhập:**
- Tên người dùng: `admin` (tự động đăng nhập, bật chế độ ẩn danh)
- Mật khẩu: (không cần)

### **7.2 Các Biểu Đồ Trên Bảng Điều Khiển**

Bảng điều khiển có 4 biểu đồ:

**Biểu đồ 1: Tỷ Lệ Yêu Cầu (yêu cầu/giây)**
- Hiển thị: số yêu cầu mỗi giây
- Dự kiến: ~3-5 yêu cầu/giây (từ bộ tạo lưu lượng)
- Loại: Biểu đồ đường

**Biểu đồ 2: Độ Trễ Yêu Cầu (Phần Trăm)**
- Hiển thị: độ trễ p50, p95, p99
- Dự kiến:
  - p50: 10-50ms
  - p95: 50-200ms
  - p99: 200-500ms
- Loại: Biểu đồ đường

**Biểu đồ 3: Tỷ Lệ Lỗi**
- Hiển thị: số lỗi mỗi giây
- Dự kiến:
  - Bình thường: ~0 (chỉ yêu cầu hợp lệ)
  - Với lỗi: ~1-3 (khi chạy lưu lượng lỗi)
- Loại: Biểu đồ cột

**Biểu đồ 4: Tỷ Lệ Cache Hit**
- Hiển thị: % cache hit
- Dự kiến: 0-100% (tùy logic cache)

### **7.3 Làm Mới Bảng Điều Khiển**

- Làm mới mặc định: mỗi 10 giây
- Làm mới thủ công: Bấm nút làm mới
- Thay đổi khoảng thời gian: Chọn phạm vi thời gian (1h, 6h, 24h)

---

## **GIAI ĐOẠN 8: KIỂM THỬ ENDPOINT HEALTH**

### **8.1 Lấy Trạng Thái HEALTH CỦA BFF**

```powershell
# Kiểm tra sức khỏe chi tiết
curl http://localhost:8087/actuator/health/bff-health | ConvertFrom-Json | ConvertTo-Json

# Kết quả:
# {
#   "status": "UP",
#   "timestamp": "2026-05-12T10:30:00",
#   "version": "1.0",
#   "services": {
#     "product": { "name": "product", "healthy": true },
#     "customer": { "name": "customer", "healthy": true },
#     "cart": { "name": "cart", "healthy": true },
#     "rating": { "name": "rating", "healthy": true }
#   }
# }
```

### **8.2 Mô Phỏng Dịch Vụ Ngừng Hoạt Động**

```powershell
# Dừng một dịch vụ để kiểm thử chỉ báo sức khỏe
docker compose stop product

# Chờ 10 giây
Start-Sleep -Seconds 10

# Kiểm tra sức khỏe - sẽ hiển thị degraded
curl http://localhost:8087/actuator/health/bff-health

# Khởi động lại dịch vụ
docker compose up -d product
```

---

## **GIAI ĐOẠN 9: KIỂM THỬ CẢNH BÁO**

### **9.1 Kích Hoạt Cảnh Báo Tỷ Lệ Lỗi Cao**

```powershell
# Terminal - Tạo nhiều lỗi (hơn 5 mỗi giây)
$errorCount = 0
$duration = 300  # 5 phút

$startTime = Get-Date
while (((Get-Date) - $startTime).TotalSeconds -lt $duration) {
    # Tạo 10 lỗi
    for ($i = 0; $i -lt 10; $i++) {
        curl -s -o /dev/null http://localhost:8087/api/invalid-$i
        $errorCount++
    }
    
    # Tạo 2 yêu cầu thành công (để tỷ lệ lỗi vẫn cao)
    curl -s -o /dev/null http://localhost:8087/api/products
    curl -s -o /dev/null http://localhost:8087/api/categories
    
    Write-Host "Tổng lỗi được tạo: $errorCount"
    Start-Sleep -Seconds 1
}
```

**Kiểm tra cảnh báo:**
1. Prometheus: http://localhost:9090/alerts
2. Cảnh báo sẽ chuyển từ "Inactive" → "Pending" (1-5 phút) → "Firing"

### **9.2 Kích Hoạt Cảnh Báo Độ Trễ Cao**

```powershell
# Tạo yêu cầu và chờ để đạt p95 > 1000ms
# (Trong thực tế, có thể thêm độ trễ nhân tạo hoặc mạng chậm)

for ($i = 0; $i -lt 20; $i++) {
    $start = Get-Date
    curl -s http://localhost:8087/api/products | Out-Null
    $elapsed = ((Get-Date) - $start).TotalMilliseconds
    
    if ($elapsed -gt 1000) {
        Write-Host "⚠️  Yêu cầu chậm: ${elapsed}ms" -ForegroundColor Yellow
    }
    Start-Sleep -Milliseconds 100
}
```

### **9.3 Xem Chi Tiết Cảnh Báo**

```powershell
# Lấy metadata cảnh báo
curl http://localhost:9090/api/v1/alerts

# Lấy nhóm cảnh báo
curl http://localhost:9090/api/v1/rules | ConvertFrom-Json | Select-Object -ExpandProperty data
```

---

## **GIAI ĐOẠN 10: KIỂM CHỨNG LOGS**

### **10.1 Kiểm Tra Logs BFF**

**Trong IDE:**
- Xem output console
- Tìm: `INFO`, `WARN`, `ERROR`
- Lọc: "bff.request", "latency", "error"

**Từ Docker:**

```powershell
# Nếu BFF chạy trong container
docker compose logs backoffice-bff -f

# 50 dòng cuối cùng
docker compose logs backoffice-bff --tail=50

# Lọc logs
docker compose logs backoffice-bff | Select-String "ERROR|WARN"
```

### **10.2 Kiểm Tra Logs Prometheus**

```powershell
docker compose -f docker-compose.o11y.yml logs prometheus -f

# Lọc lỗi
docker compose -f docker-compose.o11y.yml logs prometheus | Select-String "error|failed"
```

---

## **GIAI ĐOẠN 11: XUẤT METRICS (TỰA CHỌN)**

### **11.1 Xuất Từ Prometheus**

```powershell
# Truy vấn metrics và xuất
curl "http://localhost:9090/api/v1/query?query=bff_requests_total" > metrics.json

# Xem
Get-Content metrics.json | ConvertFrom-Json | ConvertTo-Json
```

### **11.2 Xuất Bảng Điều Khiển Grafana**

1. Truy cập http://grafana/d/bff-overview
2. Bấm menu ≡ (góc trên cùng bên trái)
3. Chia sẻ → Xuất → Tải JSON
4. Lưu file: `bff-overview-export.json`

---

## **GIAI ĐOẠN 12: DỌN DẸP VÀ TẮT**

### **12.1 Dừng Bộ Tạo Lưu Lượng**

```powershell
# Nhấn Ctrl+C trong terminal bộ tạo lưu lượng
```

### **12.2 Dừng Dịch Vụ BFF**

**Từ IDE:** Dừng trình gỡ lỗi / Ctrl+C

**Từ Command Line:**
```powershell
# Ctrl+C
```

### **12.3 Dừng Các Dịch Vụ Cốt Lõi**

```powershell
# Terminal 1
docker compose down -v

# Hoặc chỉ dừng (giữ lại volumes):
docker compose stop
```

### **12.4 Dừng Ngăn Xếp Quan Sát**

```powershell
docker compose -f docker-compose.o11y.yml down

# Hoặc giữ containers:
docker compose -f docker-compose.o11y.yml stop
```

---

## **✅ DANH SÁCH KIỂM THỬ**

```
✅ Giai Đoạn 1: Yêu Cầu Hệ Thống
  □ Docker cài đặt
  □ Docker Compose hoạt động
  □ Git branch: feature/monitoring
  □ File hosts được cấu hình

✅ Giai Đoạn 2: Ngăn Xếp Quan Sát
  □ Prometheus hoạt động (http://localhost:9090)
  □ Grafana hoạt động (http://grafana)
  □ Loki hoạt động
  □ Tempo hoạt động
  □ Collector hoạt động

✅ Giai Đoạn 3: Dịch Vụ Cốt Lõi
  □ PostgreSQL sẵn sàng
  □ Kafka sẵn sàng
  □ Redis sẵn sàng
  □ Dịch vụ BFF khởi động (port 8087)

✅ Giai Đoạn 4: Collect Metrics
  □ /actuator/health phản hồi
  □ /actuator/metrics liệt kê metrics bff.*
  □ bff.requests.total có sẵn
  □ bff.request.latency có sẵn

✅ Giai Đoạn 5-6: Lưu Lượng & Prometheus
  □ Lưu lượng được tạo thành công
  □ Prometheus scraping metrics
  □ Các truy vấn tỷ lệ hoạt động
  □ Các truy vấn tỷ lệ lỗi hoạt động

✅ Giai Đoạn 7: Bảng Điều Khiển Grafana
  □ Bảng điều khiển tải (bff-overview)
  □ Biểu đồ Tỷ Lệ Yêu Cầu hiển thị dữ liệu
  □ Biểu đồ Độ Trễ hiển thị dữ liệu
  □ Biểu đồ Tỷ Lệ Lỗi hiển thị dữ liệu
  □ Biểu đồ Tỷ Lệ Cache hiển thị dữ liệu

✅ Giai Đoạn 8-9: Health & Cảnh Báo
  □ Endpoint health phản hồi
  □ Trạng thái dịch vụ hiển thị
  □ Cảnh báo được cấu hình trong Prometheus
  □ Các quy tắc cảnh báo được kích hoạt theo yêu cầu

✅ Giai Đoạn 10-11: Logs & Xuất
  □ Logs BFF hiển thị yêu cầu
  □ Logs Prometheus sạch (không có lỗi)
  □ Metrics có thể được xuất
  □ Bảng điều khiển có thể được xuất

✅ Giai Đoạn 12: Dọn Dẹp
  □ Tất cả dịch vụ đã dừng
  □ Volumes được giữ lại/làm sạch
  □ Không có tiến trình treo
```

---

## **🐛 KHẮC PHỤC SỰ CỐ**

| Vấn Đề | Giải Pháp |
|--------|----------|
| Prometheus không scraping | Kiểm tra config prometheus.yml, khởi động lại: `docker restart prometheus` |
| Bảng điều khiển Grafana trống | Chờ 2-3 phút cho dữ liệu, làm mới trình duyệt, kiểm tra datasource |
| BFF crash khi khởi động | Kiểm tra logs, xác minh kết nối DB, đảm bảo ngăn xếp quan sát hoạt động |
| Metrics không xuất hiện | Tạo lưu lượng, kiểm tra `/actuator/metrics`, xác minh filter được đăng ký |
| Sử dụng bộ nhớ cao | Giảm retention trong prometheus.yml, giảm scrape interval |
| Cảnh báo không kích hoạt | Kiểm tra quy tắc: `curl localhost:9090/api/v1/rules`, xác minh ngưỡng |
| Xung đột cổng | Thay đổi cổng trong docker-compose files, cập nhật URLs |

---

## **📚 CÁC BƯỚC TIẾP THEO SAU KHI KIỂM THỬ**

1. **Tài Liệu Kết Quả:**
   - Ảnh chụp màn hình bảng điều khiển
   - Ghi chú các số liệu hiệu suất
   - Ghi lại độ trễ cảnh báo

2. **Chia Sẻ Kết Quả:**
   - Push branch: `git push origin feature/monitoring`
   - Tạo PR với ảnh chụp màn hình
   - Yêu cầu code review

3. **Tối Ưu Hóa:**
   - Điều chỉnh ngưỡng cảnh báo dựa trên dữ liệu
   - Tinh chỉnh scrape intervals
   - Thêm thêm custom metrics nếu cần

4. **Triển Khai Sang Staging:**
   - Kiểm thử trong môi trường staging
   - Load test với lưu lượng cao hơn
   - Giám sát trong 24-48 giờ

---

## **⏱️ THỜI GIAN ƯỚC TÍNH**

| Giai Đoạn | Thời Gian |
|-----------|-----------|
| 1-2: Cấu Hình & Quan Sát | 5 phút |
| 3: Khởi Động Dịch Vụ Cốt Lõi | 10 phút |
| 4: Kiểm Chứng Metrics | 2 phút |
| 5-6: Tạo Lưu Lượng | 10 phút |
| 7: Xem Xét Bảng Điều Khiển | 5 phút |
| 8-9: Health & Cảnh Báo | 5 phút |
| 10-12: Logs & Dọn Dẹp | 5 phút |
| **TỔNG CỘNG** | **~40 phút** |

---

**Sẵn sàng kiểm thử? Bắt đầu với GIAI ĐOẠN 1!** 🚀
