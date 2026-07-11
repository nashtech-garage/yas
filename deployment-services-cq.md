## **Phân Lo i Services: GIỮ ạ** 

## **PHẢI GIỮ— Cốt lõi cho demo E-commerce + Service Mesh** 

|**Service**|**Lý do giữ**|
|---|---|
|product|Sản phẩm—trung tâm của shop|
|cart|Giỏhàng—demofow mua hàng|
|order|Đơn hàng—demofow đặt hàng, test**retry policy**<br>(order→cart/payment/inventory/tax)|
|customer|Thông tin khách hàng|
|inventory|Kho hàng—order phụthuộc|
|tax|Thuế—order phụthuộc, demo**VirtualService retry**|
|media|Upload hìnhảnh sản phẩm|
|search|Tìm kiếm—phụthuộc product, demo**AuthorizationPolicy**|
|storefront-bff|BFF cho giao diện người dùng|
|storefront-ui|Giao diện cửa hàng—demo cho giảng viên|
|backoffice-bff|BFF cho quản trị|
|backoffice-ui|Giao diện quản trị|
|swagger-ui|APIdocumentation|
|sampledata|Dữliệu mẫu—chỉchạy1lần, sau khi chạy có data thì bạn có thểtắt đi|



**Tổng: 14 services - 1 service (sample data - chạy 1 lần)** 

