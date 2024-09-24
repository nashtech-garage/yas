package com.yas.webhook.controller;

import static io.restassured.RestAssured.given;
import static org.hamcrest.Matchers.equalTo;

import com.yas.webhook.config.IntegrationTestConfiguration;
import com.yas.webhook.config.constants.ApiConstant;
import com.yas.webhook.model.Event;
import com.yas.webhook.model.Webhook;
import com.yas.webhook.model.WebhookEvent;
import com.yas.webhook.model.enums.EventName;
import com.yas.webhook.repository.EventRepository;
import com.yas.webhook.repository.WebhookEventRepository;
import com.yas.webhook.repository.WebhookRepository;
import com.yas.webhook.service.WebhookService;
import io.restassured.http.ContentType;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.context.annotation.PropertySource;
import org.springframework.http.HttpStatus;
import org.springframework.transaction.annotation.EnableTransactionManagement;

@EnableTransactionManagement
@Import(IntegrationTestConfiguration.class)
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
@PropertySource("classpath:application.properties")
public class WebhookControllerIT extends AbstractControllerIT {

  @Autowired
  private WebhookService service;
  @Autowired
  private WebhookRepository webhookRepository;
  @Autowired
  private EventRepository eventRepository;
  @Autowired
  private WebhookEventRepository webhookEventRepository;

  private static final String WEBHOOK_URL = "/webhook" + ApiConstant.WEBHOOK_URL;

  @BeforeEach
  public void init() {
    Webhook webhook = new Webhook();
    webhook.setContentType("application/json");
    Webhook persistedWebhook = webhookRepository.save(webhook);

    Event event = new Event();
    event.setName(EventName.ON_PRODUCT_UPDATED);
    Event persistedEvent = eventRepository.save(event);

    WebhookEvent webhookEvent = new WebhookEvent();
    webhookEvent.setWebhookId(persistedWebhook.getId());
    webhookEvent.setEventId(persistedEvent.getId());

    webhookEventRepository.save(webhookEvent);
  }

  @Test
  public void test_createWebhook_shouldSuccess() {
    given(getRequestSpecification())
        .auth().oauth2(getAccessToken("admin", "admin"))
        .contentType(ContentType.JSON)
        .body("""
            {"id":1,"payloadUrl":"","secret":"","contentType":"","isActive":true
            ,"events":[{"id":1,"name":"ON_PRODUCT_UPDATED"}]}
            """)
        .when()
        .post(WEBHOOK_URL)
        .then()
        .statusCode(HttpStatus.CREATED.value())
        .log().ifValidationFails();
  }

  @Test
  public void test_updateWebhook_shouldSuccess() {
    given(getRequestSpecification())
        .auth().oauth2(getAccessToken("admin", "admin"))
        .contentType(ContentType.JSON)
        .body("""
            {"id":1,"payloadUrl":"","secret":"","contentType":"","isActive":true
            ,"events":[{"id":1,"name":"ON_PRODUCT_UPDATED"}]}
            """)
        .pathParam("id", 1)
        .when()
        .put(WEBHOOK_URL + "/{id}")
        .then()
        .statusCode(HttpStatus.NO_CONTENT.value())
        .log().ifValidationFails();
  }

  @Test
  public void test_deleteWebhook_shouldSuccess() {
    given(getRequestSpecification())
        .auth().oauth2(getAccessToken("admin", "admin"))
        .contentType(ContentType.JSON)
        .pathParam("id", 1)
        .when()
        .delete(WEBHOOK_URL + "/{id}")
        .then()
        .statusCode(HttpStatus.NO_CONTENT.value())
        .log().ifValidationFails();
  }

  @Test
  public void test_deleteWebhook_shouldFailedWhenNotFindId() {
    given(getRequestSpecification())
        .auth().oauth2(getAccessToken("admin", "admin"))
        .contentType(ContentType.JSON)
        .pathParam("id", 10)
        .when()
        .delete(WEBHOOK_URL + "/{id}")
        .then()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .log().ifValidationFails();
  }

  @Test
  public void test_getWebhook_shouldReturnSuccessfully() {
    given(getRequestSpecification())
        .auth().oauth2(getAccessToken("admin", "admin"))
        .contentType(ContentType.JSON)
        .pathParam("id", 1)
        .when()
        .get(WEBHOOK_URL + "/{id}")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("contentType", equalTo("application/json"))
        .log().ifValidationFails();
  }

  @Test
  public void test_getWebhook_shouldReturn404_whenWebhookNotFound() {
    given(getRequestSpecification())
        .auth().oauth2(getAccessToken("admin", "admin"))
        .contentType(ContentType.JSON)
        .pathParam("id", 10)
        .when()
        .get(WEBHOOK_URL + "/{id}")
        .then()
        .statusCode(HttpStatus.NOT_FOUND.value())
        .log().ifValidationFails();
  }

  @Test
  public void test_listWebhooks_shouldReturnSuccessfully() {
    given(getRequestSpecification())
        .auth().oauth2(getAccessToken("admin", "admin"))
        .contentType(ContentType.JSON)
        .when()
        .get(WEBHOOK_URL)
        .then()
        .statusCode(HttpStatus.OK.value())
        .log().ifValidationFails();
  }

  @Test
  public void test_getPageableWebhooks_shouldReturnSuccessfully() {
    given(getRequestSpecification())
        .auth().oauth2(getAccessToken("admin", "admin"))
        .contentType(ContentType.JSON)
        .when()
        .get(WEBHOOK_URL + "/paging")
        .then()
        .statusCode(HttpStatus.OK.value())
        .body("pageNo", equalTo(0))
        .body("pageSize", equalTo(10))
        .body("totalElements", equalTo(5))
        .body("totalPages", equalTo(1))
        .log().ifValidationFails();
  }
}
