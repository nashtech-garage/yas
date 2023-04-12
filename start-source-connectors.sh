
curl -i -X PUT -H  "Content-Type:application/json" \
    http://kafka-connect:8083/connectors/product-connector/config \
    -d @connectors-config/connections/source.json
    