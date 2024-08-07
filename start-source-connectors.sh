
curl -i -X PUT -H  "Content-Type:application/json" \
    http://localhost:8083/connectors/product-connector/config \
    -d @kafka/connects/debezium-product.json

curl -i -X PUT -H  "Content-Type:application/json" \
    http://localhost:8083/connectors/order-connector/config \
    -d @kafka/connects/debezium-order.json