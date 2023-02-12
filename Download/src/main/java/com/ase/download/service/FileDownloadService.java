package com.ase.download.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.core.io.UrlResource;
import org.springframework.stereotype.Service;
import org.springframework.util.StringUtils;
import org.springframework.web.multipart.MultipartFile;

import com.ase.download.exception.DownloadException;
import com.ase.download.exception.DownloadFileNotFoundException;
import com.ase.download.model.FileProperties;

import java.io.IOException;
import java.net.MalformedURLException;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.Paths;
import java.nio.file.StandardCopyOption;

@Service
public class FileDownloadService {

    private final Path fileDownloadLocation;

    @Autowired
    public FileDownloadService(FileProperties fileStorageProperties) {
        this.fileDownloadLocation = Paths.get(fileStorageProperties.getUploadDir())
                .toAbsolutePath().normalize();

        try {
            Files.createDirectories(this.fileDownloadLocation);
        } catch (Exception ex) {
            throw new DownloadException("Could not create the directory where the uploaded files will be stored.", ex);
        }
    }

    public Resource loadResource(String fileName) {
        try {
            Path filePath = this.fileDownloadLocation.resolve(fileName).normalize();
            Resource resource = new UrlResource(filePath.toUri());
            if (resource.exists()) {
                return resource;
            } else {
                throw new DownloadFileNotFoundException("File not found " + fileName);
            }
        } catch (MalformedURLException ex) {
            throw new DownloadFileNotFoundException("File not found " + fileName, ex);
        }
    }
}
