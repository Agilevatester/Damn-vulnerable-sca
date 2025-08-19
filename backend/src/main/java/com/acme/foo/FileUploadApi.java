package com.acme.foo;

import org.apache.logging.log4j.LogManager;
import org.apache.logging.log4j.Logger;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;
import org.zeroturnaround.zip.ZipUtil;
import java.io.IOException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;

@RestController
@CrossOrigin("*")
@RequestMapping("/api/files")
public class FileUploadApi {

	private static final Logger logger = LogManager.getLogger(FileUploadApi.class);


	private static final String UPLOAD_DIR = "uploads/";

	@PostMapping("/upload")
	public ResponseEntity<String> uploadFile(@RequestParam("file") MultipartFile file) {
		try {
			// Ensure the uploads directory exists
			Path uploadPath = Paths.get(UPLOAD_DIR);
			if (!Files.exists(uploadPath)) {
				Files.createDirectories(uploadPath);
			}

     		// Save the uploaded file
            Path savedFilePath = uploadPath.resolve(file.getOriginalFilename());
            Files.write(savedFilePath, file.getBytes());

            // If the file is a zip, extract it
            if (file.getOriginalFilename().toLowerCase().endsWith(".zip")) {
                ZipUtil.unpack(savedFilePath.toFile(), uploadPath.toFile());
            }


			return ResponseEntity.status(HttpStatus.OK).body("File uploaded successfully: " + file.getOriginalFilename() + " -> " + path + " -> " + zipPath);
		} catch (IOException e) {
			logger.error("Failed to upload file", e);
			return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body("Failed to upload file: " + e.getMessage());
		}
	}
}