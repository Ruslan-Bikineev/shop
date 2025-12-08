package edu.school21.controllers;

import edu.school21.annotation.GeneralApiResponses;
import edu.school21.dto.MessageDto;
import edu.school21.entity.Image;
import edu.school21.exceptions.EmptyFileException;
import edu.school21.services.ImageService;
import jakarta.validation.constraints.NotNull;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.ResponseStatus;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.util.UUID;

@RequiredArgsConstructor
@Validated
@RestController
@RequestMapping("/api/v1/images")
public class ImageController {

    private final ImageService imageService;

    @GeneralApiResponses(summary = "Get image by id")
    @GetMapping(value = "/{id}",
            produces = MediaType.APPLICATION_OCTET_STREAM_VALUE)
    public byte[] getImage(@PathVariable UUID id) {
        return imageService.findById(id).getImage();
    }

    @GeneralApiResponses(summary = "Create image")
    @PostMapping
    @ResponseStatus(HttpStatus.CREATED)
    public MessageDto saveImage(
            @NotNull(message = "cannot be empty")
            @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new EmptyFileException("File cannot be empty");
        }
        Image image = new Image(file.getBytes());
        return imageService.saveImage(image);
    }

    @GeneralApiResponses(summary = "Create or update image by id")
    @PutMapping("/{id}")
    public MessageDto putUpdateImage(
            @PathVariable("id") UUID id,
            @NotNull(message = "cannot be empty")
            @RequestParam("file") MultipartFile file) throws IOException {
        if (file.isEmpty()) {
            throw new EmptyFileException("File cannot be empty");
        }
        Image image = new Image(id, file.getBytes());
        return imageService.putUpdateImage(image);
    }

    @GeneralApiResponses(summary = "Delete image by id")
    @DeleteMapping("/{id}")
    @ResponseStatus(HttpStatus.NO_CONTENT)
    public void deleteImage(@PathVariable UUID id) {
        imageService.deleteById(id);
    }
}
