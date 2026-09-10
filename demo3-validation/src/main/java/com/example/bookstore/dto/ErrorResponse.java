package com.example.bookstore.dto;

import java.util.List;

/**
 * 统一错误响应体：所有异常都返回这个结构。
 * 前端拿到后可直接展示，字段稳定，便于排查。
 */
public record ErrorResponse(
        int status,
        String message,
        List<String> errors
) {
}