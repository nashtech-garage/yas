package com.yas.media.controller;

import com.yas.media.model.Media;
import com.yas.media.model.dto.MediaDto;
import com.yas.media.service.MediaService;
import com.yas.media.viewmodel.ErrorVm;
import com.yas.media.viewmodel.MediaPostVm;
import com.yas.media.viewmodel.MediaVm;
import com.yas.media.viewmodel.NoFileMediaVm;
import io.swagger.v3.oas.annotations.Hidden;
import io.swagger.v3.oas.annotations.media.Content;
import io.swagger.v3.oas.annotations.media.Schema;
import io.swagger.v3.oas.annotations.responses.ApiResponse;
import io.swagger.v3.oas.annotations.responses.ApiResponses;
import jakarta.validation.Valid;
import jakarta.validation.constraints.NotEmpty;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.core.io.InputStreamResource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.ModelAttribute;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

@Validated
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
@RequiredArgsConstructor
public class MediaController {
    private final MediaService mediaService;

    @PostMapping(path = "/medias", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok",
            content = @Content(schema = @Schema(implementation = NoFileMediaVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Object> create(@ModelAttribute @Valid MediaPostVm mediaPostVm) {
        Media media = mediaService.saveMedia(mediaPostVm);
        NoFileMediaVm noFileMediaVm =
            new NoFileMediaVm(media.getId(), media.getCaption(), media.getFileName(), media.getMediaType());
        return ResponseEntity.ok().body(noFileMediaVm);
    }

    @DeleteMapping("/medias/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "204", description = "Deleted",
            content = @Content(schema = @Schema(implementation = MediaVm.class))),
        @ApiResponse(responseCode = "400", description = "Bad request",
            content = @Content(schema = @Schema(implementation = ErrorVm.class)))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mediaService.removeMedia(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/medias/{id}")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok",
            content = @Content(schema = @Schema(implementation = MediaVm.class)))})
    public ResponseEntity<MediaVm> get(@PathVariable Long id) {
        MediaVm media = mediaService.getMediaById(id);
        if (media == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(media);
    }

    @GetMapping("/medias")
    @ApiResponses(value = {
        @ApiResponse(responseCode = "200", description = "Ok",
            content = @Content(schema = @Schema(implementation = MediaVm.class)))})
    public ResponseEntity<List<MediaVm>> getByIds(@RequestParam @NotEmpty List<Long> ids) {
        var medias = mediaService.getMediaByIds(ids);
        if (medias.isEmpty()) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(medias);
    }

    @Hidden
    @GetMapping("/medias/{id}/file/{fileName}")
    public ResponseEntity<InputStreamResource> getFile(@PathVariable Long id, @PathVariable String fileName) {
        MediaDto mediaDto = mediaService.getFile(id, fileName);

        return ResponseEntity.ok()
            .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileName + "\"")
            .contentType(mediaDto.getMediaType())
            .body(new InputStreamResource(mediaDto.getContent()));
    }
}
