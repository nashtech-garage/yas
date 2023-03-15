package com.yas.media.controller;

import com.yas.media.model.Media;
import com.yas.media.repository.MediaRepository;
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
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.*;

@Validated
@RestController
@CrossOrigin(origins = "*", maxAge = 3600)
public class MediaController {
    private final MediaService mediaService;
    private final MediaRepository mediaRepository;

    public MediaController(MediaService mediaService, MediaRepository mediaRepository) {
        this.mediaService = mediaService;
        this.mediaRepository = mediaRepository;
    }

    @PostMapping(path = "/medias", consumes = {MediaType.MULTIPART_FORM_DATA_VALUE})
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = NoFileMediaVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))})
    public ResponseEntity<Object> create(@ModelAttribute @Valid MediaPostVm mediaPostVm) {
        Media media = mediaService.saveMedia(mediaPostVm);
        NoFileMediaVm noFileMediaVm = new NoFileMediaVm(media.getId(), media.getCaption(), media.getFileName(), media.getMediaType());
        return ResponseEntity.ok().body(noFileMediaVm);
    }

    @DeleteMapping("/medias/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "204", description = "Deleted", content = @Content(schema = @Schema(implementation = MediaVm.class))),
            @ApiResponse(responseCode = "400", description = "Bad request", content = @Content(schema = @Schema(implementation = ErrorVm.class)))
    })
    public ResponseEntity<Void> delete(@PathVariable Long id) {
        mediaService.removeMedia(id);
        return ResponseEntity.noContent().build();
    }

    @GetMapping("/medias/{id}")
    @ApiResponses(value = {
            @ApiResponse(responseCode = "200", description = "Ok", content = @Content(schema = @Schema(implementation = MediaVm.class)))})
    public ResponseEntity<MediaVm> get(@PathVariable Long id) {
        MediaVm media = mediaService.getMediaById(id);
        if (media == null) {
            return ResponseEntity.notFound().build();
        }
        return ResponseEntity.ok().body(media);
    }

    @Hidden
    @GetMapping("/medias/{id}/file/{fileName}")
    public ResponseEntity<byte[]> getFile(@PathVariable Long id, @PathVariable String fileName) {
        Media media = mediaRepository.findById(id).orElse(null);
        if (media == null || !fileName.equalsIgnoreCase(media.getFileName())) {
            return ResponseEntity.notFound().build();
        }
        MediaType mediaType = MediaType.valueOf(media.getMediaType());
        return ResponseEntity.ok()
                .contentType(mediaType)
                .body(media.getData());
    }
}
