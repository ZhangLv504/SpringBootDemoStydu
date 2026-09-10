package com.example.bookstore.service;

import java.util.List;

import org.springframework.stereotype.Service;

import com.example.bookstore.dto.BookRequest;
import com.example.bookstore.exception.BookNotFoundException;
import com.example.bookstore.model.Book;
import com.example.bookstore.repository.BookRepository;

/**
 * Service 层：业务逻辑。
 * 相比 Demo2，这里不再把 Optional 上抛给 Controller，
 * 而是找不到时直接抛业务异常 BookNotFoundException，由全局处理器统一兜成 404。
 */
@Service
public class BookService {

    private final BookRepository repository;

    public BookService(BookRepository repository) {
        this.repository = repository;
    }

    public List<Book> findAll() {
        return repository.findAll();
    }

    public Book findById(Long id) {
        return repository.findById(id)
                .orElseThrow(() -> new BookNotFoundException(id));
    }

    public Book create(BookRequest request) {
        Book book = new Book(request.title(), request.author(), request.price());
        // save() 有记录则更新、没有则插入；id 由数据库自增生成
        return repository.save(book);
    }
}