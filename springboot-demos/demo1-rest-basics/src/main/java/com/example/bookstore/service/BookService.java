package com.example.bookstore.service;

import java.util.ArrayList;
import java.util.List;
import java.util.Map;
import java.util.Optional;
import java.util.concurrent.ConcurrentHashMap;
import java.util.concurrent.atomic.AtomicLong;

import org.springframework.stereotype.Service;

import com.example.bookstore.dto.BookRequest;
import com.example.bookstore.model.Book;

/**
 * Service 层：业务逻辑。
 * 这里因为没有数据库，先用一个内存 Map 存数据（正整数 id 由 AtomicLong 自增）。
 * {@code @Service} 注解让 Spring 把这个类管起来，成为容器里的一个 Bean。
 */
@Service
public class BookService {

    /** 内存存储：id -> Book。ConcurrentHashMap 保证并发安全。 */
    private final Map<Long, Book> store = new ConcurrentHashMap<>();

    /** 自增 id 生成器，保证线程安全且不重复。 */
    private final AtomicLong idSeq = new AtomicLong(1);

    public List<Book> findAll() {
        return new ArrayList<>(store.values());
    }

    public Optional<Book> findById(Long id) {
        return Optional.ofNullable(store.get(id));
    }

    public Book create(BookRequest request) {
        Book book = new Book(
                idSeq.getAndIncrement(),
                request.title(),
                request.author(),
                request.price());
        store.put(book.getId(), book);
        return book;
    }
}