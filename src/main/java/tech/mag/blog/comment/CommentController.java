package tech.mag.blog.comment;

import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.messaging.handler.annotation.MessageMapping;
import org.springframework.messaging.handler.annotation.SendTo;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.validation.Valid;
import tech.mag.blog.blog.Blog;
import tech.mag.blog.blog.BlogService;
import tech.mag.blog.user.User;
import tech.mag.blog.util.ERole;

@RestController
@RequestMapping(value = "/api/comments")
public class CommentController {

    @Autowired
    BlogService blogService;

    @Autowired
    CommentService commentService;

    @GetMapping("/{blogId}/comments")
    public ResponseEntity<List<Comment>> getBlogComments(@PathVariable UUID blogId) {
        List<Comment> comments = commentService.getBlogComments(blogId);
        return ResponseEntity.ok(comments);
    }

 

    @PostMapping(value = "/{blogId}/newComment")
    public ResponseEntity<?> commentOnBlog(@Valid @PathVariable UUID blogId,
            @RequestBody CommentRequest commentRequest) {
        try {

            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User writer = (User) authentication.getPrincipal();

            Comment comment = new Comment();
            comment.setWriter(writer);
            comment.setText(commentRequest.getText());

            Optional<Blog> optionalBlog = blogService.getBlogById(blogId);
            Blog blog = null;
            if (optionalBlog.isPresent()) {
                blog = optionalBlog.get();
                comment.setBlog(blog);
            }

            // saving the blog comment to the database
            Map<String, Object> response = commentService.createBlogComment(comment);
            if (response.containsKey("Error")) {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(response, HttpStatus.OK);
            }

        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/update/{blogId}/{commentId}")
    public ResponseEntity<?> updateBlogComment(@Valid @PathVariable UUID commentId, @PathVariable UUID blogId,
            @RequestBody CommentRequest commentRequest) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User writer = (User) authentication.getPrincipal();

            Comment comment = new Comment();

            Optional<Blog> optionalBlog = blogService.getBlogById(blogId);
            Optional<Comment> optionalComment = commentService.getCommentById(commentId);
            Blog blog = null;

            if (optionalBlog.isPresent() && optionalComment.isPresent()) {
                blog = optionalBlog.get();
                comment = optionalComment.get();
                if (!writer.getId().equals(blog.getAuthor().getId())) {
                    return new ResponseEntity<>("You are not allowed to update this comment", HttpStatus.UNAUTHORIZED);
                } else {
                    comment.setText(commentRequest.getText());
                    commentService.updateBlogComment(comment);
                    return new ResponseEntity<>("Comment updated successfully", HttpStatus.OK);
                }
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }

    @DeleteMapping(value = "/delete/{blogId}/{commentId}")
    public ResponseEntity<?> deleteComment(@PathVariable UUID commentId, @PathVariable UUID blogId) {
        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User writer = (User) authentication.getPrincipal();

            Optional<Blog> optionalBlog = blogService.getBlogById(blogId);
            Blog blog = null;

            if (optionalBlog.isPresent()) {
                blog = optionalBlog.get();
                if ((!writer.getId().equals(blog.getAuthor().getId())) || (writer.getRole() != ERole.ADMIN)) {
                    return new ResponseEntity<>("You are not allowed to delete this comment", HttpStatus.UNAUTHORIZED);
                }
                String feedback = commentService.deleteBlogComment(commentId);
                if (feedback.equalsIgnoreCase("Comment deleted successfully")) {
                    return new ResponseEntity<>(feedback, HttpStatus.OK);
                }
                return new ResponseEntity<>("Comment not deleted", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
        return null;
    }
}
