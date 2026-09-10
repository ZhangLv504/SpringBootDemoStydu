package com.example.bookstore.dto;

/**
 * DTO：接收前端 POST 请求体，与 Demo1 一致。
 */
public record BookRequest(String title, String author, Double price) {
}