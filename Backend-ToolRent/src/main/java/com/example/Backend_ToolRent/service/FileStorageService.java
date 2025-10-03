package com.example.Backend_ToolRent.service;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import java.io.IOException;
import java.nio.file.*;
import java.util.UUID;

@Service
public class FileStorageService {

    @Value("${app.upload.dir:/uploads/}")
    private String uploadDir;

    public String store(MultipartFile file, String subfolder) throws IOException {
        String original = StringUtils.cleanPath(file.getOriginalFilename());
        if (original.contains("..")) {
            throw new IOException("Nombre de archivo inválido: " + original);
        }

        String ext = "";
        int i = original.lastIndexOf('.');
        if (i > 0) ext = original.substring(i);

        String filename = UUID.randomUUID().toString() + ext;
        Path dirPath = Paths.get(uploadDir, subfolder).toAbsolutePath().normalize();
        Files.createDirectories(dirPath);

        Path target = dirPath.resolve(filename);
        Files.copy(file.getInputStream(), target, StandardCopyOption.REPLACE_EXISTING);

        // Ruta pública que guardarás en la BD
        return "/uploads/" + subfolder + "/" + filename;
    }

    public boolean delete(String publicPath) throws IOException {
        if (publicPath == null || !publicPath.startsWith("/uploads/")) return false;
        String sub = publicPath.substring("/uploads/".length());
        Path filePath = Paths.get(uploadDir).resolve(sub).normalize();
        return Files.deleteIfExists(filePath);
    }
}
