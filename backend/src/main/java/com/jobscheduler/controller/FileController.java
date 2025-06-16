package com.jobscheduler.controller;

import com.jobscheduler.service.MinioService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/files")
@CrossOrigin(origins = "*")
public class FileController {

    @Autowired
    private MinioService minioService;

    @PostMapping("/upload")
    public ResponseEntity<Map<String, Object>> uploadFile(
            @RequestParam("file") MultipartFile file,
            @RequestParam(value = "bucket", required = false) String bucket) {
        
        try {
            String objectName = minioService.uploadFile(file, bucket);
            String presignedUrl = minioService.getPresignedUrl(
                bucket != null ? bucket : "job-scheduler-files", 
                objectName, 
                3600 // 1 hour expiry
            );

            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("objectName", objectName);
            response.put("presignedUrl", presignedUrl);
            response.put("fileName", file.getOriginalFilename());
            response.put("fileSize", file.getSize());
            response.put("contentType", file.getContentType());

            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @GetMapping("/presigned-url")
    public ResponseEntity<Map<String, String>> getPresignedUrl(
            @RequestParam String bucket,
            @RequestParam String objectName,
            @RequestParam(defaultValue = "3600") int expirySeconds) {
        
        try {
            String presignedUrl = minioService.getPresignedUrl(bucket, objectName, expirySeconds);
            
            Map<String, String> response = new HashMap<>();
            response.put("presignedUrl", presignedUrl);
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }

    @DeleteMapping
    public ResponseEntity<Map<String, Object>> deleteFile(
            @RequestParam String bucket,
            @RequestParam String objectName) {
        
        try {
            minioService.deleteFile(bucket, objectName);
            
            Map<String, Object> response = new HashMap<>();
            response.put("success", true);
            response.put("message", "File deleted successfully");
            
            return ResponseEntity.ok(response);

        } catch (Exception e) {
            Map<String, Object> errorResponse = new HashMap<>();
            errorResponse.put("success", false);
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}