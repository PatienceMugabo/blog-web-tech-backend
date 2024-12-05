package tech.mag.blog.comment;

import java.util.List;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

import tech.mag.blog.blog.Blog;

@Repository
public interface CommentRespostory extends JpaRepository<Comment, UUID> {
    List<Comment> findByBlog(Blog blog);
}