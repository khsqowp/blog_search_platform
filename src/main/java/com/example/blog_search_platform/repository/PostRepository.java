package com.example.blog_search_platform.repository;


import com.example.blog_search_platform.domain.Post;
import org.springframework.data.jpa.repository.JpaRepository;

/**
 * Post 엔티티에 대한 데이터 접근(CRUD)을 담당하는 리포지토리 인터페이스
 * JpaRepository를 상속받는 것만으로 기본적인 CRUD 메서드가 자동으로 생성됩니다.
 */
public interface PostRepository extends JpaRepository<Post, Long> { // <엔티티 클래스, ID 타입>
}
