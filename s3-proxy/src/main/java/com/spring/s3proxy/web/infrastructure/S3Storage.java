package com.spring.s3proxy.web.infrastructure;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.ObjectMetadata;
import com.spring.s3proxy.exception.upload.UploadFailureException;
import com.spring.s3proxy.web.domain.FileStorage;
import com.spring.s3proxy.web.domain.StoreResult;
import java.util.List;
import java.util.stream.Collectors;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Repository;
import org.springframework.web.multipart.MultipartFile;

@Repository
public class S3Storage implements FileStorage {

    @Value("${aws.s3.bucket.name}")
    private String bucket;

    @Value("${aws.cloud_front.file_url_format}")
    private String fileUrlFormat;

    private final AmazonS3 amazonS3;

    private final FileNameGenerator fileNameGenerator;

    public S3Storage(AmazonS3 amazonS3, FileNameGenerator fileNameGenerator) {
        this.amazonS3 = amazonS3;
        this.fileNameGenerator = fileNameGenerator;
    }

    @Override
    public List<StoreResult> store(List<MultipartFile> files, String userName) {
        return files.stream()
            .map(file -> upload(file, userName))
            .collect(Collectors.toList());
    }

    private StoreResult upload(MultipartFile file, String userName) {
        String fileName = fileNameGenerator.generateFileName(file, userName);
        ObjectMetadata objectMetadata = generateObjectMetaData(file);
        try {
            amazonS3.putObject(bucket, fileName, file.getInputStream(), objectMetadata);
            return new StoreResult(fileName, String.format(fileUrlFormat, fileName));
        } catch (Exception e) {
            throw new UploadFailureException();
        }
    }

    private ObjectMetadata generateObjectMetaData(MultipartFile file) {
        ObjectMetadata objectMetadata = new ObjectMetadata();
        objectMetadata.setContentLength(file.getSize());
        objectMetadata.setContentType(file.getContentType());
        return objectMetadata;
    }
}
