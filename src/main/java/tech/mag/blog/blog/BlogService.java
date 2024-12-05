package tech.mag.blog.blog;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class BlogService {

    @Autowired
    private BlogRepository blogRepository;

    public Page<Blog> getAllBlogs(int page, int size, String sortBy, String sortDir) {
        Sort sort = sortDir.equalsIgnoreCase(Sort.Direction.ASC.name()) ? Sort.by(sortBy).ascending()
                : Sort.by(sortBy).descending();
        Pageable pageable = PageRequest.of(page, size, sort);
        return blogRepository.findAll(pageable);
    }

    public Optional<Blog> getBlogById(UUID id) {
        return blogRepository.findById(id);
    }

    public Map<String, Object> createBlog(Blog blog) {
        Map<String, Object> response = new HashMap<>();
        if (blogRepository.findByTitle(blog.getTitle()).isPresent()) {
            response.put("message", "Blog with this title already exists");
            return response;
        } else {
            Blog theBlog = blogRepository.save(blog);
            response.put("message", "Blog created successfully");
            response.put("data", theBlog);
            return response;
        }
    }

    public Map<String, Object> updateBlog(Blog updatedBlog) {
        Optional<Blog> optionalBlog = blogRepository.findById(updatedBlog.getId());
        Map<String, Object> response = new HashMap<>();

        if (optionalBlog.isPresent()) {
            response.put("message", "Blog updated successfully");
            response.put("data", blogRepository.save(updatedBlog));
            return response;
        } else {
            response.put("message", "Blog not found");
            return response;
        }

    }

    public void deleteBlog(UUID id) {
        blogRepository.deleteById(id);
    }
}
