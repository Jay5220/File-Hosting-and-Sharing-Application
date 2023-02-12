package com.ase.download.controller;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.core.io.Resource;
import org.springframework.http.HttpHeaders;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import com.ase.download.service.FileDownloadService;

import java.io.File;
import javax.servlet.http.HttpServletRequest;
import java.io.IOException;


@RestController
public class DownloadFileController {

    private static final Logger logger = LoggerFactory.getLogger(DownloadFileController.class);

    @Autowired
    private FileDownloadService fileDownloadService;
    
    @GetMapping("/getFileList")
    public String[] getFileList() {

//        File directoryPath = new File("/home/ubuntu/Server/userdata/serverFileHolder");
    	File directoryPath = new File("/Users/jatan/UTA/ASE/Server/userdata/serverFileHolder");
        String contents[] = directoryPath.list();
        return contents;
    }

    @GetMapping("/fileDownload/{fileName:.+}")
    public ResponseEntity<Resource> fileDownload(@PathVariable String fileName, HttpServletRequest request) {
        Resource fileResource = fileDownloadService.loadResource(fileName);
        String content = null;
        try {
        	content = request.getServletContext().getMimeType(fileResource.getFile().getAbsolutePath());
        } catch (IOException ex) {
            logger.info("File type does not exist.");
        }

        if (content == null) {
        	content = "application/octet-stream";
        }

        return ResponseEntity.ok()
                .contentType(MediaType.parseMediaType(content))
                .header(HttpHeaders.CONTENT_DISPOSITION, "attachment; filename=\"" + fileResource.getFilename() + "\"")
                .body(fileResource);
    }

}