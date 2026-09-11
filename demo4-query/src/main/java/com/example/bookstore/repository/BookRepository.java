package com.example.bookstore.repository;

import java.util.List;

import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import com.example.bookstore.model.Book;

/**
 * Repository 层 —— 本 Demo 的核心教学点。
 *
 * 【第 1 把钥匙：方法名派生查询】
 *   不用写 SQL，方法名本身带语义，Spring Data 自动翻译：
 *     List<Book> findByTitleContainingIgnoreCase(String keyword);
 *     → select * from books where lower(title) like concat('%', lower(?), '%')
 *   关键词拆解：findBy(查) + Title(字段) + Containing(包含=LIKE %kw%) + IgnoreCase(忽略大小写)
 *
 * 【第 2 把钥匙：@Query 手写 JPQL】
 *   复杂/多字段查用注解写在接口上，:kw 是命名参数，@Param 绑定。
 *
 * 【第 3 把钥匙：Pageable 分页】
 *   方法多接一个 Pageable 参数，Spring Data 自动拼 LIMIT/OFFSET 并返回
 *   Page 对象（含总条数、总页数、当前页内容）。
 */
public interface BookRepository extends JpaRepository<Book, Long> {

    // 派生查询：按"标题包含关键字"模糊搜（忽略大小写）
    List<Book> findByTitleContainingIgnoreCase(String keyword);

    // 派生查询：精确按作者查
    List<Book> findByAuthor(String author);

    // @Query 手写 JPQL：标题或作者任一包含关键词
    @Query("SELECT b FROM Book b " +
           "WHERE LOWER(b.title) LIKE LOWER(CONCAT('%', :kw, '%')) " +
           "   OR LOWER(b.author) LIKE LOWER(CONCAT('%', :kw, '%'))")
    List<Book> search(@Param("kw") String kw);

    // 分页：每页带 Pageable，返回 Page<Book>
    Page<Book> findAll(Pageable pageable);
}