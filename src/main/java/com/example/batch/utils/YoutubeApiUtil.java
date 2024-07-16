package com.example.batch.utils;

import org.springframework.http.ResponseEntity;

public interface YoutubeApiUtil {
    ResponseEntity conn(String query);
}
