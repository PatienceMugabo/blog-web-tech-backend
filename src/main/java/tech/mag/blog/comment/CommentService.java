package tech.mag.blog.comment;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import tech.mag.blog.blog.Blog;
import tech.mag.blog.blog.BlogService;

@Service
public class CommentService {

    @Autowired
    private CommentRespostory commentRespostory;

    @Autowired
    BlogService blogService;

    // a function to get all comment on a blog
    public List<Comment> getBlogComments(UUID blogId) {
        Optional<Blog> optionalBlog = blogService.getBlogById(blogId);
        Blog blog = null;
        if (optionalBlog.isPresent()) {
            blog = optionalBlog.get();
        }
        return commentRespostory.findByBlog(blog);
    }

    public Map<String, Object> createBlogComment(Comment comment) {
        Map<String, Object> response = new HashMap();
        Comment theComment = commentRespostory.save(comment);
        if (theComment != null) {
            response.put("message", "You comment on this post");
            response.put("data", theComment);
            return response;
        } else {
            response.put("Error", "Commenting failed");
            return response;
        }
    }

    public String deleteBlogComment(UUID commentId) {
        commentRespostory.deleteById(commentId);
        return "Comment deleted successfully";
    }

    public Comment updateBlogComment(Comment comment) {
        return commentRespostory.save(comment);
    }

    public Optional<Comment> getCommentById(UUID commentId) {
        return commentRespostory.findById(commentId);

    }
}
