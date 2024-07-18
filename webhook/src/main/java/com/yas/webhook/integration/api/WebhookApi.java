package com.yas.webhook.integration.api;

import com.fasterxml.jackson.databind.JsonNode;
import com.yas.webhook.utils.HmacUtils;
import lombok.RequiredArgsConstructor;
import lombok.SneakyThrows;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;

@Component
@RequiredArgsConstructor
public class WebhookApi {

  public static final String X_HUB_SIGNATURE_256 = "X-Hub-Signature-256";

  private final RestClient restClient;

  @SneakyThrows
  public void notify(String url, String secret, JsonNode jsonNode) {

    String secretToken = HmacUtils.hash(jsonNode.toString(), secret);

    restClient.post()
        .uri(url)
        .header(X_HUB_SIGNATURE_256, secretToken)
        .body(jsonNode)
        .retrieve()
        .toBodilessEntity();
  }
}
