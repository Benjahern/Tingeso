package com.example.Backend_ToolRent.service;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.test.util.ReflectionTestUtils;

import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

class FileStorageServiceTest {

    private FileStorageService fileStorageService;

    // @TempDir crea una carpeta temporal real que se borra al terminar el test
    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() {
        fileStorageService = new FileStorageService();
        // Inyectamos la ruta temporal en la variable privada 'uploadDir' usando Reflection
        // Esto simula: @Value("${app.upload.dir}")
        ReflectionTestUtils.setField(fileStorageService, "uploadDir", tempDir.toString());
    }

    // --- Tests para store() ---

    @Test
    @DisplayName("store() debe guardar archivo y retornar ruta válida")
    void store_savesFileSuccessfully() throws IOException {
        // Given
        MockMultipartFile file = new MockMultipartFile(
                "file",
                "test-image.png",
                "image/png",
                "contenido dummy".getBytes()
        );
        String subfolder = "tools";

        // When
        String resultPath = fileStorageService.store(file, subfolder);

        // Then
        // 1. Verificar que el path retornado empieza con el prefijo esperado
        assertThat(resultPath).startsWith("/uploads/tools/");
        assertThat(resultPath).endsWith(".png");

        // 2. Verificar que el archivo FÍSICAMENTE existe en la carpeta temporal
        // Extraemos el nombre del archivo generado (quitando /uploads/tools/)
        String generatedFilename = resultPath.replace("/uploads/tools/", "");
        Path savedFile = tempDir.resolve(subfolder).resolve(generatedFilename);

        assertThat(Files.exists(savedFile)).isTrue();
        assertThat(Files.readAllBytes(savedFile)).isEqualTo("contenido dummy".getBytes());
    }

    @Test
    @DisplayName("store() debe lanzar excepción con nombres de archivo inválidos (Path Traversal)")
    void store_throwsException_whenFilenameContainsDotDot() {
        MockMultipartFile badFile = new MockMultipartFile(
                "file",
                "../hack.exe", // Nombre malicioso
                "text/plain",
                "virus".getBytes()
        );

        assertThatThrownBy(() -> fileStorageService.store(badFile, "tools"))
                .isInstanceOf(IOException.class)
                .hasMessageContaining("Nombre de archivo inválido");
    }

    @Test
    @DisplayName("store() debe crear subdirectorios si no existen")
    void store_createsDirectories() throws IOException {
        MockMultipartFile file = new MockMultipartFile("file", "doc.pdf", "application/pdf", "data".getBytes());

        fileStorageService.store(file, "new-folder/deep");

        Path createdPath = tempDir.resolve("new-folder/deep");
        assertThat(Files.exists(createdPath)).isTrue();
        assertThat(Files.isDirectory(createdPath)).isTrue();
    }

    // --- Tests para delete() ---

    @Test
    @DisplayName("delete() debe eliminar archivo existente y retornar true")
    void delete_removesFile() throws IOException {
        // Given: Creamos un archivo real en el directorio temporal
        Path subDir = tempDir.resolve("users");
        Files.createDirectories(subDir);
        Path fileToDelete = subDir.resolve("avatar.jpg");
        Files.write(fileToDelete, "data".getBytes());

        // Construimos el path público que recibiría el método (ej: /uploads/users/avatar.jpg)
        String publicPath = "/uploads/users/avatar.jpg";

        // When
        boolean deleted = fileStorageService.delete(publicPath);

        // Then
        assertThat(deleted).isTrue();
        assertThat(Files.exists(fileToDelete)).isFalse(); // El archivo ya no debe existir
    }

    @Test
    @DisplayName("delete() retorna false si el archivo no existe")
    void delete_returnsFalse_whenFileDoesNotExist() throws IOException {
        boolean result = fileStorageService.delete("/uploads/ghost/phantom.jpg");
        assertThat(result).isFalse();
    }

    @Test
    @DisplayName("delete() retorna false si el path es nulo o inválido")
    void delete_returnsFalse_whenPathIsInvalid() throws IOException {
        assertThat(fileStorageService.delete(null)).isFalse();
        assertThat(fileStorageService.delete("http://otro-sitio.com/img.jpg")).isFalse();
        assertThat(fileStorageService.delete("uploads/sin-barra-inicial.jpg")).isFalse();
    }
}
