package com.quangntn.whatsappclone.file;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

public class FileServiceTest {

    @Mock
    private MultipartFile multipartFile;

    @InjectMocks
    private FileService fileService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testSaveFile() throws IOException {
        String userId = "12345";
        String originalFilename = "testfile.txt";
        byte[] fileContent = "Hello, World!".getBytes();

        when(multipartFile.getOriginalFilename()).thenReturn(originalFilename);
        when(multipartFile.getBytes()).thenReturn(fileContent);

        String result = fileService.saveFile(multipartFile, userId);

        assertNotNull(result);
        assertTrue(result.contains(userId));
        assertTrue(result.endsWith(".txt"));

        Path path = Path.of(result);
        assertTrue(Files.exists(path));

        // Clean up
        Files.deleteIfExists(path);
        new File(path.getParent().toString()).delete();
    }
}
