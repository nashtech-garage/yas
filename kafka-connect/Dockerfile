FROM confluentinc/cp-kafka-connect-base:6.2.1

# Install Avro & JDBC plugins
RUN confluent-hub install --no-prompt confluentinc/kafka-connect-avro-converter:5.5.4
RUN confluent-hub install --no-prompt confluentinc/kafka-connect-jdbc:10.1.1
RUN confluent-hub install --no-prompt confluentinc/kafka-connect-elasticsearch:11.0.1

# Install and configure JMX Exporter
COPY jmx_prometheus_javaagent-0.15.0.jar /opt/
COPY kafka-connect.yml /opt/
