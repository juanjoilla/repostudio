package com.artesluis.artesluis_backend.controller;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.util.StringUtils;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;

@RestController
@RequestMapping("/api/upload")
@CrossOrigin(origins = "*")
public class UploadController {

    private static final String UPLOAD_DIR = "uploads/";

    @PostMapping("/imagen")
    public ResponseEntity<String> subirImagen(@RequestParam("file") MultipartFile file) {
        try {
            // Asegurarse de que el archivo tenga nombre válido
            String nombreArchivo = StringUtils.cleanPath(file.getOriginalFilename());
            Path rutaArchivo = Paths.get(UPLOAD_DIR + nombreArchivo);

            // Crear directorio si no existe
            Files.createDirectories(rutaArchivo.getParent());

            // Guardar archivo
            Files.copy(file.getInputStream(), rutaArchivo, StandardCopyOption.REPLACE_EXISTING);

            // Retornar URL accesible
            String url = "/uploads/" + nombreArchivo;
            return ResponseEntity.ok(url);

        } catch (IOException e) {
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                    .body("Error al subir la imagen: " + e.getMessage());
        }
    }
}
