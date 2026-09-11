package com.example.bookstore.exception;

import java.util.List;

import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.MethodArgumentNotValidException;
import org.springframework.web.bind.annotation.ExceptionHandler;
import org.springframework.web.bind.annotation.RestControllerAdvice;

import com.example.bookstore.dto.ErrorResponse;

/**
 * 全局异常处理器：把分散在各 Controller 的错误收拢到一处，统一返回 JSON。
 * - @RestControllerAdvice = 对所有 @RestController 生效的"增强"
 * - @ExceptionHandler(某异常) 声明这个方法是用来处理哪类异常的
 *
 * 好处：Controller 不用再写 try/catch，业务层只负责抛异常，展示层统一兜底。
 */
@RestControllerAdvice
public class GlobalExceptionHandler {

    // 图书不存在 → 404
    @ExceptionHandler(BookNotFoundException.class)
    public ResponseEntity<ErrorResponse> handleNotFound(BookNotFoundException ex) {
        return ResponseEntity.status(HttpStatus.NOT_FOUND)
                .body(new ErrorResponse(HttpStatus.NOT_FOUND.value(), ex.getMessage(), List.of()));
    }

    // 参数校验失败（@Valid 触发）→ 400，逐字段列出错误
    @ExceptionHandler(MethodArgumentNotValidException.class)
    public ResponseEntity<ErrorResponse> handleValidation(MethodArgumentNotValidException ex) {
        List<String> fieldErrors = ex.getBindingResult().getFieldErrors().stream()
                .map(fe -> fe.getField() + ": " + fe.getDefaultMessage())
                .toList();
        return ResponseEntity.status(HttpStatus.BAD_REQUEST)
                .body(new ErrorResponse(HttpStatus.BAD_REQUEST.value(), "参数校验失败", fieldErrors));
    }

    // 兜底：其他未预期异常 → 500
    @ExceptionHandler(Exception.class)
    public ResponseEntity<ErrorResponse> handleOther(Exception ex) {
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                .body(new ErrorResponse(HttpStatus.INTERNAL_SERVER_ERROR.value(),
                        "服务器内部错误", List.of(ex.getClass().getSimpleName())));
    }
}