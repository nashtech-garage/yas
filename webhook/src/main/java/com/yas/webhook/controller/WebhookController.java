package com.yas.webhook.controller;

import com.yas.webhook.config.constants.ApiConstant;
import com.yas.webhook.config.constants.PageableConstant;
import com.yas.webhook.model.viewmodel.error.ErrorVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookDetailVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookListGetVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookPostVm;
import com.yas.webhook.model.viewmodel.webhook.WebhookVm;
import com.yas.webhook.service.WebhookService;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import java.util.List;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.util.UriComponentsBuilder;

@RestController
@RequestMapping(ApiConstant.WEBHOOK_URL)
public class WebhookController {
    private final WebhookService webhookService;

    public WebhookController(WebhookService webhookService) {
        this.webhookService = webhookService;
    }

    @GetMapping("/paging")
    public ResponseEntity<WebhookListGetVm> getPageableWebhooks(
        @RequestParam(value = "pageNo",
            defaultValue = PageableConstant.DEFAULT_PAGE_NUMBER, required = false) final int pageNo,
        @RequestParam(value = "pageSize",
            defaultValue = PageableConstant.DEFAULT_PAGE_SIZE, required = false) final int pageSize) {
        return ResponseEntity.ok(webhookService.getPageableWebhooks(pageNo, pageSize));
    }

    @GetMapping
    public ResponseEntity<List<WebhookVm>> listWebhooks() {
        return ResponseEntity.ok(webhookService.findAllWebhooks());
    }

    @GetMapping("/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = ApiConstant.CODE_200, description = ApiConstant.OK,
            content = @Content(schema = @Schema(implementation = WebhookVm.class))),
        @ApiResponse(responseCode = ApiConstant.CODE_404, description = ApiConstant.NOT_FOUND,
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<WebhookDetailVm> getWebhook(@PathVariable("id") final Long id) {
        return ResponseEntity.ok(webhookService.findById(id));
    }

    @PostMapping
    @ApiResponses(value = {
        @ApiResponse(responseCode = ApiConstant.CODE_201, description = ApiConstant.CREATED,
            content = @Content(schema = @Schema(implementation = WebhookVm.class))),
        @ApiResponse(responseCode = ApiConstant.CODE_400, description = ApiConstant.BAD_REQUEST,
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<WebhookDetailVm> createWebhook(
        @Valid @RequestBody final WebhookPostVm webhookPostVm,
        final UriComponentsBuilder uriComponentsBuilder) {
        WebhookDetailVm webhook = webhookService.create(webhookPostVm);
        return ResponseEntity.created(
                uriComponentsBuilder
                    .replacePath("/webhooks/{id}")
                    .buildAndExpand(webhook)
                    .toUri())
            .body(webhook);
    }

    @PutMapping("/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = ApiConstant.CODE_204, description = ApiConstant.NO_CONTENT,
            content = @Content()),
        @ApiResponse(responseCode = ApiConstant.CODE_404, description = ApiConstant.NOT_FOUND,
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = ApiConstant.CODE_400, description = ApiConstant.BAD_REQUEST,
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> updateWebhook(@PathVariable final Long id,
                                              @Valid @RequestBody final WebhookPostVm webhookPostVm) {
        webhookService.update(webhookPostVm, id);
        return ResponseEntity.noContent().build();
    }

    @DeleteMapping("/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = ApiConstant.CODE_204, description = ApiConstant.NO_CONTENT, content = @Content()),
        @ApiResponse(responseCode = ApiConstant.CODE_404, description = ApiConstant.NOT_FOUND,
            content = @Content(schema = @Schema(implementation = ErrorVm.class))),
        @ApiResponse(responseCode = ApiConstant.CODE_400, description = ApiConstant.BAD_REQUEST,
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Void> deleteWebhook(@PathVariable(name = "id") final Long id) {
        webhookService.delete(id);
        return ResponseEntity.noContent().build();
    }
}
