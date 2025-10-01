package com.manga.manga_web;

import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.cloud.client.discovery.EnableDiscoveryClient;
import org.springframework.context.annotation.ComponentScan;
import org.springframework.scheduling.annotation.EnableAsync;

@EnableAsync
@SpringBootApplication
public class MangaWebServiceApplication {
    public static void main(String[] args) {
        SpringApplication.run(MangaWebServiceApplication.class, args);
    }
}
