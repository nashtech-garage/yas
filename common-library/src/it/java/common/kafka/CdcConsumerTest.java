package common.kafka;

import static org.mockito.ArgumentMatchers.any;
import static org.mockito.Mockito.when;
import java.net.URI;
import java.time.Duration;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;
import java.util.concurrent.TimeUnit;
import java.util.concurrent.TimeoutException;
import lombok.Getter;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.apache.kafka.clients.producer.ProducerConfig;
import org.apache.kafka.common.serialization.StringSerializer;
import org.jetbrains.annotations.NotNull;
import static org.mockito.ArgumentMatchers.any;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.test.context.bean.override.mockito.MockitoBean;
import org.springframework.context.annotation.Import;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.ResponseEntity;
import org.springframework.kafka.core.DefaultKafkaProducerFactory;
import org.springframework.kafka.core.KafkaTemplate;
import org.springframework.kafka.support.serializer.JsonSerializer;
import org.springframework.util.Assert;
import org.springframework.web.client.RestClient;
import org.testcontainers.kafka.ConfluentKafkaContainer;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.MockitoAnnotations;

/**
 * Base class providing support for testing a CDC (Change Data Capture) consumer.
 * This class requires Kafka containers to be running (e.g., {@link Import}), and it provides support
 * for a Kafka producer to send messages, as well as simulating a REST client
 * for microservice integration.
 *
 * @param <M> Message Type
 */
@Getter
public abstract class CdcConsumerTest<K, M> {

    private final Logger logger = LoggerFactory.getLogger(CdcConsumerTest.class);

    private final Class<K> keyType;
    private final Class<M> messageType;

    private final String cdcEvent;

    @Autowired
    private ConfluentKafkaContainer kafkaContainer;

    @MockitoBean
    private RestClient restClient;

    @Mock
    RestClient.ResponseSpec responseSpec;

    @Mock
    RestClient.RequestHeadersUriSpec requestHeadersUriSpec;

    private KafkaTemplate<K, M> kafkaTemplate;

    public CdcConsumerTest(Class<K> keyType, Class<M> messageType, String topicEvent) {
        Assert.notNull(topicEvent, "CDC topic must not be null");
        Assert.notNull(messageType, "Message type must not be null");
        Assert.notNull(keyType, "Key type must not be null");
        this.cdcEvent = topicEvent;
        this.keyType = keyType;
        this.messageType = messageType;
    }

    @BeforeEach
    public void initMocks() {
        MockitoAnnotations.openMocks(this);
    }

    public synchronized KafkaTemplate<K, M> getKafkaTemplate() {
        // Now, we haven't had any process need Kafka Producer,
        // then just temporary config producer like this for testing
        if (kafkaTemplate == null) {
            synchronized (this) {
                final Map<String, Object> props = getProducerProps();
                kafkaTemplate = new KafkaTemplate<>(new DefaultKafkaProducerFactory<K, M>(props));
            }
        }
        return kafkaTemplate;
    }

    protected void sendMsg(M message)
            throws InterruptedException, ExecutionException, TimeoutException {
        var rs = getKafkaTemplate()
                .send(this.cdcEvent, message)
                .get(10, TimeUnit.SECONDS);
        logger.info("Sent message completed: {}", rs);
    }

    protected void sendMsg(K key, M message)
        throws InterruptedException, ExecutionException, TimeoutException {
        var rs = getKafkaTemplate()
            .send(this.cdcEvent, key, message)
            .get(10, TimeUnit.SECONDS);
        logger.info("Sent message completed: {}", rs);
    }

    protected <R> void simulateHttpRequestWithResponse(URI url, R response, Class<R> responseType) {
        setupMockGetRequest(url);
        when(responseSpec.body(responseType)).thenReturn(response);
    }

    protected <R> void simulateHttpRequestWithResponseToEntity(URI url, R response, Class<R> responseType) {
        setupMockGetRequest(url);
        when(responseSpec.toEntity(any(ParameterizedTypeReference.class)))
                .thenReturn(ResponseEntity.ok(response));
    }

    protected <R> void simulateHttpRequestWithError(URI url, Throwable exception, Class<R> responseType) {
        setupMockGetRequest(url);
        when(responseSpec.body(responseType)).thenThrow(exception);
    }

    private void setupMockGetRequest(URI url) {
        when(restClient.get()).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.uri(url)).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.headers(any())).thenReturn(requestHeadersUriSpec);
        when(requestHeadersUriSpec.retrieve()).thenReturn(responseSpec);
    }

    /**
     * Pauses the current thread until the consumer has finished processing. The wait time is dynamic and
     * depends on the specific scenario (e.g., retry cases or normal processing).
     *
     * @param processTime  The time (in seconds) the consumer takes to process each record.
     * @param numOfRecords The number of records sent to the consumer.
     * @param attempts     The number of retry attempts. If no retries are needed, set this to '0'.
     * @param backOff      The backoff time (in seconds) between retries.
     * @throws InterruptedException If the thread is interrupted while sleeping.
     */
    public void waitForConsumer(
            long processTime,
            int numOfRecords,
            int attempts,
            long backOff
    ) throws InterruptedException {
        // retryTime =  (1st run) + (total run when retrying) + (total back off time)
        long retryTime = processTime + (attempts * processTime) + (backOff * attempts);
        long waitTime = (attempts > 0 ? retryTime : processTime) * numOfRecords;
        logger.info("Consumer Processing expected time: {}s", waitTime);
        Thread.sleep(Duration.ofSeconds(waitTime));
    }

    private @NotNull Map<String, Object> getProducerProps() {
        final Map<String, Object> props = new HashMap<>();
        props.put(ProducerConfig.BOOTSTRAP_SERVERS_CONFIG, kafkaContainer.getBootstrapServers());
        props.put(ProducerConfig.ACKS_CONFIG, "all");
        props.put(ProducerConfig.RETRIES_CONFIG, 0);
        props.put(ProducerConfig.KEY_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        props.put(ProducerConfig.VALUE_SERIALIZER_CLASS_CONFIG, JsonSerializer.class);
        return props;
    }
}