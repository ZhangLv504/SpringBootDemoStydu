package com.example.bookstore.dto;

import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.NotNull;
import jakarta.validation.constraints.Positive;

/**
 * 入参 DTO：除了传输数据，还承担【参数校验】职责。
 * 校验注解来自 Bean Validation（jakarta.validation）。
 * 由 Controller 上的 @Valid 触发，字段不合法时抛 MethodArgumentNotValidException，
 * 交给 GlobalExceptionHandler 统一转成 400。
 */
public record BookRequest(
        @NotBlank(message = "书名不能为空")
        String title,

        @NotBlank(message = "作者不能为空")
        String author,

        @NotNull(message = "价格不能为空")
        @Positive(message = "价格必须大于 0")
        Double price
) {
}