curl -i -X PUT -H "Accept:application/json" \
 -H "Content-Type:application/json" http://localhost:8083/connectors/jdbc-source-brand/config \
 -d '{
      "connector.class": "io.confluent.connect.jdbc.JdbcSourceConnector",
      "connection.url": "jdbc:postgresql://postgres:5432/product",
      "connection.user": "admin",
      "connection.password": "${file:/etc/kafka-connect/kafka-connect.properties:jdbc.source.brand.password}",
      "incrementing.column.name": "id",
      "mode": "incrementing",
      "table.whitelist": "brand",
      "topic.prefix": "connect.",
      "transforms": "createKeyStruct,extractStructValue,addNamespace",
      "transforms.createKeyStruct.fields": "id",
      "transforms.createKeyStruct.type": "org.apache.kafka.connect.transforms.ValueToKey",
      "transforms.extractStructValue.field": "id",
      "transforms.extractStructValue.type": "org.apache.kafka.connect.transforms.ExtractField$Key",
      "transforms.addNamespace.schema.name": "connect.Brand",
      "transforms.addNamespace.type": "org.apache.kafka.connect.transforms.SetSchemaMetadata$Value"
 }'

 curl -i -X PUT -H "Accept:application/json" \
  -H "Content-Type:application/json" http://localhost:8083/connectors/es-sink-brand/config \
  -d '{
      "connector.class": "io.confluent.connect.elasticsearch.ElasticsearchSinkConnector",
      "topics": "connect.brand",
      "connection.url": "http://elasticsearch:9200"
  }'