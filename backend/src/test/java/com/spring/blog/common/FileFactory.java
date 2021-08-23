package com.spring.blog.common;

import java.io.File;
import java.io.IOException;
import java.net.URL;
import java.nio.file.Files;
import java.util.Arrays;
import java.util.List;
import org.apache.tika.Tika;
import org.springframework.mock.web.MockMultipartFile;
import org.springframework.web.multipart.MultipartFile;

public class FileFactory {

    private static final Tika TIKA = new Tika();
    private static final ClassLoader CLASS_LOADER = FileFactory.class.getClassLoader();

    private FileFactory() {
    }

    public static List<MultipartFile> getSuccessImageFiles() {
        return Arrays.asList(getTestSuccessImage1(), getTestSuccessImage2());
    }

    public static MultipartFile getTestFailData() {
        return createMultipartFile("testFile.sh");
    }

    public static MultipartFile getTestFailData2() {
        return createMultipartFile("testFile.akak");
    }

    public static MultipartFile getTestFailImage1() {
        return createMultipartFile("testFailImage1.jpg");
    }

    public static MultipartFile getTestFailImage2() {
        return createMultipartFile("testFailImage2.jpg");
    }

    public static MultipartFile getTestSuccessImage1() {
        return createMultipartFile("testSuccessImage1.png");
    }

    public static MultipartFile getTestSuccessImage2() {
        return createMultipartFile("testSuccessImage2.png");
    }

    private static MultipartFile createMultipartFile(String fileName) {
        File file = createFile(fileName);
        try {
            return new MockMultipartFile(
                "files",
                fileName,
                TIKA.detect(file),
                Files.readAllBytes(file.toPath())
            );
        } catch (IOException e) {
            throw new RuntimeException();
        }
    }

    private static File createFile(String fileName) {
        URL resource = CLASS_LOADER.getResource(fileName);
        return new File(resource.getFile());
    }
}
