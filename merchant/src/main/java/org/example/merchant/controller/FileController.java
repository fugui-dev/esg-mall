package org.example.merchant.controller;

import io.minio.BucketExistsArgs;
import io.minio.MakeBucketArgs;
import io.minio.MinioClient;
import io.minio.PutObjectArgs;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.example.merchant.bean.SingleResponse;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;

import java.util.UUID;

@Slf4j
@RestController
@RequestMapping("/file")
@RequiredArgsConstructor
public class FileController {

    private final MinioClient minioClient;

    @Value("${spring.minio.bucket-name}")
    private String bucket;

    @Value("${spring.minio.review.url}")
    private String endpoint;

    /**
     * 上传文件
     *
     * @param file 文件
     * @return 文件访问地址
     */
    @PostMapping("/upload")
    public SingleResponse<String> upload(@RequestParam("file") MultipartFile file) {

        try {
            // 检查bucket是否存在
            boolean found = minioClient.bucketExists(BucketExistsArgs.builder().bucket(bucket).build());
            if (!found) {
                minioClient.makeBucket(MakeBucketArgs.builder().bucket(bucket).build());
            }

            // 生成文件名
            String originalFilename = file.getOriginalFilename();
            String fileName = UUID.randomUUID().toString().replaceAll("-", "") +
                    getFileExtension(originalFilename);

            // 上传文件
            minioClient.putObject(
                    PutObjectArgs.builder()
                            .bucket(bucket)
                            .object(fileName)
                            .stream(file.getInputStream(), file.getSize(), -1)
                            .contentType(file.getContentType())
                            .build()
            );

            // 生成文件访问地址
            String fileUrl = endpoint + "/" + bucket + "/" + fileName;

            return SingleResponse.of(fileUrl);

        } catch (Exception e) {
            log.error("文件上传失败", e);
            throw new RuntimeException("文件上传失败");
        }
    }

    /**
     * 获取文件扩展名
     */
    private String getFileExtension(String filename) {
        if (filename == null || filename.lastIndexOf(".") == -1) {
            return "";
        }
        return filename.substring(filename.lastIndexOf("."));
    }


}