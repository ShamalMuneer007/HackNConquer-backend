package org.hackncrypt.clanservice.service;

import com.amazonaws.services.s3.AmazonS3;
import com.amazonaws.services.s3.model.*;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import org.springframework.web.multipart.MultipartFile;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.Objects;

@Service
@Slf4j
public class S3Service {

    private AmazonS3 s3client;

    @Value("${aws.s3.bucket}")
    private String bucketName;

    public S3Service(AmazonS3 s3client) {
        this.s3client = s3client;
    }

    public String uploadFile(String keyName, MultipartFile multipartFile) throws IOException {
        File file = convertMultiPartToFile(multipartFile);
        PutObjectRequest putObjectRequest = new PutObjectRequest(bucketName, keyName, file);
        PutObjectResult putObjectResult = s3client.putObject(putObjectRequest);
        file.delete();
        String objectUrl = s3client.getUrl(bucketName, keyName).toString();
        log.info(String.valueOf(putObjectResult.getMetadata()));
        return objectUrl;
    }

    public S3Object getFile(String keyName) {
        return s3client.getObject(bucketName, keyName);
    }
    private File convertMultiPartToFile(MultipartFile file) throws IOException {
        File convertedFile = new File(Objects.requireNonNull(file.getOriginalFilename()));
        FileOutputStream fos = new FileOutputStream(convertedFile);
        fos.write(file.getBytes());
        fos.close();
        return convertedFile;
    }

}
