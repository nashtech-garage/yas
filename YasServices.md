# Khảo sát Source YAS

Dưới đây là danh sách các service trong hệ thống YAS yêu cầu build Docker image, kèm theo context build và đường dẫn Dockerfile tương ứng:

| Service Name | Build Context | Dockerfile Path |
| --- | --- | --- |
| `webhook` | `./webhook` | `webhook/Dockerfile` |
| `tax` | `./tax` | `tax/Dockerfile` |
| `storefront` | `./storefront` | `storefront/Dockerfile` |
| `storefront-bff` | `./storefront-bff` | `storefront-bff/Dockerfile` |
| `sampledata` | `./sampledata` | `sampledata/Dockerfile` |
| `search` | `./search` | `search/Dockerfile` |
| `recommendation` | `./recommendation` | `recommendation/Dockerfile` |
| `rating` | `./rating` | `rating/Dockerfile` |
| `promotion` | `./promotion` | `promotion/Dockerfile` |
| `payment-paypal` | `./payment-paypal` | `payment-paypal/Dockerfile` |
| `product` | `./product` | `product/Dockerfile` |
| `payment` | `./payment` | `payment/Dockerfile` |
| `media` | `./media` | `media/Dockerfile` |
| `order` | `./order` | `order/Dockerfile` |
| `location` | `./location` | `location/Dockerfile` |
| `inventory` | `./inventory` | `inventory/Dockerfile` |
| `customer` | `./customer` | `customer/Dockerfile` |
| `backoffice-bff` | `./backoffice-bff` | `backoffice-bff/Dockerfile` |
| `cart` | `./cart` | `cart/Dockerfile` |
| `backoffice` | `./backoffice` | `backoffice/Dockerfile` |

> [!NOTE]
> Có một số folder khác cũng chứa `Dockerfile` như `docker/postgres` và `docker-jenkins` nhưng đây là các container hạ tầng hoặc công cụ CI/CD, không thuộc nhóm microservices ứng dụng của YAS.
