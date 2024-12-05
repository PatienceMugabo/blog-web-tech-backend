package tech.mag.blog.blog;

import java.util.Optional;
import java.util.UUID;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface BlogRepository extends JpaRepository<Blog, UUID> {
    Optional<Blog> findByTitle(String title);
}
