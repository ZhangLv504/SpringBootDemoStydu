package com.example.bookstore.exception;

/**
 * 业务异常：图书不存在。由 Service 抛出，GlobalExceptionHandler 兜成 404。
 */
public class BookNotFoundException extends RuntimeException {

    public BookNotFoundException(Long id) {
        super("图书不存在: id=" + id);
    }
}