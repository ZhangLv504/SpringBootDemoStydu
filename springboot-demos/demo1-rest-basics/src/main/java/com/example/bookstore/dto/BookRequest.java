package com.example.bookstore.dto;

/**
 * DTO (Data Transfer Object)：专门用于接收前端 POST 请求体。
 * 用 Java record 书写，简洁且天然不可变。
 * 注意：这里没有 id，id 由服务端生成，客户端不应传入。
 */
public record BookRequest(String title, String author, Double price) {
}