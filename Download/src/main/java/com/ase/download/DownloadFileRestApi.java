package com.ase.download;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.boot.context.properties.EnableConfigurationProperties;

import com.ase.download.model.FileProperties;

@SpringBootApplication
@EnableConfigurationProperties({
        FileProperties.class
})
public class DownloadFileRestApi {

    public static void main(String[] args) {
        SpringApplication.run(DownloadFileRestApi.class, args);
    }
}
