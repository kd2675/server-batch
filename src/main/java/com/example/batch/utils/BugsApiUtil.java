package com.example.batch.utils;

import org.springframework.http.ResponseEntity;

public interface BugsApiUtil {
    ResponseEntity conn(String type, String query, int pageNo, int pagePerCnt);
}
