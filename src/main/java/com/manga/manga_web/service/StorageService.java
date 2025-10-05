package com.manga.manga_web.service;

import java.io.InputStream;

public interface StorageService {
    String upload(String objectName, InputStream data, long size, String contentType);
}
