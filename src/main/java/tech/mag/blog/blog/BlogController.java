package tech.mag.blog.blog;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.*;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.data.domain.Page;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.*;
import org.springframework.web.multipart.MultipartFile;

import jakarta.validation.Valid;
import tech.mag.blog.user.User;
import tech.mag.blog.user.UserService;
import tech.mag.blog.util.EBlogCategory;

@RestController
@RequestMapping("/api/blogs")
@CrossOrigin("*")
public class BlogController {

    @Autowired
    private BlogService blogService;

    @Autowired
    private UserService userService;

    @Value("${profile.picture.upload.directory}")
    private String uploadDirectory;

    // controller for getting all the blogs
    @GetMapping(value = "/all", produces = MediaType.APPLICATION_JSON_VALUE)
    public ResponseEntity<?> getAllBlogs(
            @RequestParam(defaultValue = "0") int page,
            @RequestParam(defaultValue = "10") int size,
            @RequestParam(defaultValue = "publicationDate") String sortBy,
            @RequestParam(defaultValue = "desc") String sortDir) {

        Page<Blog> blogsPage = blogService.getAllBlogs(page, size, sortBy, sortDir);
        Map<String, Object> response = new HashMap<>();
        response.put("blogs", blogsPage.getContent());
        response.put("currentPage", blogsPage.getNumber());
        response.put("totalItems", blogsPage.getTotalElements());
        response.put("totalPages", blogsPage.getTotalPages());

        return ResponseEntity.ok(response);
    }

    // a controller for getting a single blog by id
    @GetMapping(value = "/{id}", produces = { MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> getAllblogs(@PathVariable UUID id) {
        Map<String, Object> response = new HashMap<>();
        try {
            Blog blog;
            if (blogService.getBlogById(id).isPresent()) {
                blog = blogService.getBlogById(id).get();
                response.put("blog", blog);
                response.put("author", blog.getAuthor());
                response.put("blog_comment", blog.getComments());
                response.put("blog_likes", blog.getLikedByUsers());
                return ResponseEntity.ok(response);
            } else {
                response.put("Error", "Blog not found");
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.getMessage();
            response.put("Error", "Internal Server Error");
            return new ResponseEntity<>(response, HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    // a controller for creating a new blog with a unique title
    @PostMapping(value = "/create", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> createBlog(
            @Valid @RequestParam("title") String title,
            @RequestParam("content") String content,
            @RequestParam("category") String category,
            @RequestParam(value = "blogThumbnail", required = true) MultipartFile image) throws IOException {

        try {
            Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
            User author = (User) authentication.getPrincipal();
            System.out.println("Here I am checking the user: " + author);

            String blogThumbnail = System.currentTimeMillis() + image.getOriginalFilename();

            System.out.println("Here I am checking the the image" + image.getOriginalFilename());

            // Create the upload directory if it doesn't exist
            File directory = new File(uploadDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Save the file to the specified directory
            String filePath = uploadDirectory + blogThumbnail;
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(image.getBytes());
            fos.close();

            Blog blog = new Blog();
            blog.setTitle(title);
            blog.setContent(content);
            blog.setBlogThumbnail(blogThumbnail);
            blog.setCategory(EBlogCategory.valueOf(category));
            blog.setAuthor(author);

            Map<String, Object> response = blogService.createBlog(blog);
            if (response.get("message").equals("Blog with this title already exists")) {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // a controller for updating blog information
    @PutMapping(value = "/update/{id}", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE })
    public ResponseEntity<?> updateBlog(
            @Valid @PathVariable UUID id, @RequestParam(value = "title", required = false) String title,
            @RequestParam(value = "content", required = false) String content,
            @RequestParam(value = "category", required = false) String category,
            @RequestParam(value = "readTime", required = false) String readTime,
            @RequestParam(value = "blogThumbnail", required = false) MultipartFile image) throws IOException {

        try {
            // Fetch the existing blog
            Optional<Blog> existingBlogOptional = blogService.getBlogById(id);
            if (!existingBlogOptional.isPresent()) {
                return new ResponseEntity<>("Blog not found", HttpStatus.NOT_FOUND);
            }

            Blog blog = existingBlogOptional.get();
            // Update fields only if they are provided
            if (title != null) {
                blog.setTitle(title);
            }
            if (content != null) {
                blog.setContent(content);
            }
            if (category != null) {
                blog.setCategory(EBlogCategory.valueOf(category));
            }
            if (image != null && !image.isEmpty()) {
                String blogThumbnail = System.currentTimeMillis() + image.getOriginalFilename();

                // Create the upload directory if it doesn't exist
                File directory = new File(uploadDirectory);
                if (!directory.exists()) {
                    directory.mkdirs();
                }

                // Save the file to the specified directory
                String filePath = uploadDirectory + blogThumbnail;
                FileOutputStream fos = new FileOutputStream(filePath);
                fos.write(image.getBytes());
                fos.close();

                blog.setBlogThumbnail(blogThumbnail);
            }

            // Save the updated blog
            Map<String, Object> response = blogService.updateBlog(blog);
            if (response.get("messgae").equals("Blog not found")) {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>(response, HttpStatus.OK);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // a controller for deleting blog by Id
    @DeleteMapping(value = "/delete/{id}")
    public ResponseEntity<?> deleteBlog(@PathVariable UUID id) {
        try {
            if (blogService.getBlogById(id).isPresent()) {
                blogService.deleteBlog(id);
                return ResponseEntity.ok("Blog deleted successfully");
            } else {
                return new ResponseEntity<>("Blog not found", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception e) {
            e.getMessage();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }

    }

    @GetMapping("/{blogId}/toggleLike")
    public ResponseEntity<?> likeBlog(@PathVariable UUID blogId) {
        Optional<Blog> blogOptional = blogService.getBlogById(blogId);

        Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
        User user = (User) authentication.getPrincipal();
        user = userService.getUserWithLikedBlogs(user.getId());

        if (blogOptional.isPresent()) {
            Blog blog = blogOptional.get();
            System.out.println("Liked users before: " + blog.getLikedByUsers());
            if (!blog.getLikedByUsers().contains(user)) {
                blog.getLikedByUsers().add(user);
                user.getLikedBlogs().add(blog);
                blogService.updateBlog(blog);
                userService.updateUser(user);
                return ResponseEntity.ok("You liked this blog");
            } else {
                blog.getLikedByUsers().remove(user);
                System.out.println("Liked users afer remove: " + blog.getLikedByUsers());
                user.getLikedBlogs().remove(blog);
                System.out.println(blogService.updateBlog(blog).get("data"));
                System.out.println(blog.getLikedByUsers());
                userService.updateUser(user);
                
                return ResponseEntity.ok("You unliked this blog");
            }
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    // Endpoint to get likes for a specific blog
    @GetMapping("/{blogId}/likes")
    public ResponseEntity<?> getBlogLikes(@PathVariable UUID blogId) {
        Optional<Blog> blogOptional = blogService.getBlogById(blogId);
        if (blogOptional.isPresent()) {
            Blog blog = blogOptional.get();
            Set<User> likedByUsers = blog.getLikedByUsers();
            return ResponseEntity.ok(likedByUsers);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

}
