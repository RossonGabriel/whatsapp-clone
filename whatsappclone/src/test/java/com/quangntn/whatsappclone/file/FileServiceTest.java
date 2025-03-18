package com.quangntn.whatsappclone.file;

import java.io.IOException;
import java.lang.reflect.Field;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;
import static org.junit.jupiter.api.Assertions.assertTrue;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.io.TempDir;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import static org.mockito.Mockito.when;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

public class FileServiceTest {

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private FileService fileService;

    @TempDir
    Path tempDir;

    @BeforeEach
    void setUp() throws Exception {
        MockitoAnnotations.openMocks(this);
        // Set the upload directory using reflection
        Field fileUploadPathField = FileService.class.getDeclaredField("fileUploadPath");
        fileUploadPathField.setAccessible(true);
        fileUploadPathField.set(fileService, tempDir.toString());
    }

    @Test
    void testSaveFile() throws IOException {
        // Given
        String userId = "12345";
        String originalFilename = "testfile.txt";
        byte[] fileContent = "Hello, World!".getBytes();

        when(multipartFile.getOriginalFilename()).thenReturn(originalFilename);
        when(multipartFile.getBytes()).thenReturn(fileContent);

        // When
        String result = fileService.saveFile(multipartFile, userId);

        // Then
        assertNotNull(result);
        assertTrue(result.contains(userId));
        assertTrue(result.endsWith(".txt"));
        assertTrue(result.contains(tempDir.toString()));

        // Verify the file was created in the temp directory
        Path expectedPath = tempDir.resolve(result);
        assertTrue(Files.exists(expectedPath));
        
        // Verify file contents
        String savedContent = new String(Files.readAllBytes(expectedPath));
        assertEquals("Hello, World!", savedContent);
    }
}
