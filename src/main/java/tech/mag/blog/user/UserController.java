package tech.mag.blog.user;

import java.util.List;
import java.util.UUID;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RequestPart;
import org.springframework.web.bind.annotation.RestController;
import org.springframework.web.multipart.MultipartFile;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;

import jakarta.validation.Valid;
import tech.mag.blog.blog.Blog;

@RestController
@RequestMapping("/api/users")
@Validated
public class UserController {

    @Autowired
    private UserService userService;

    @PostMapping(value = "/createUser", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> registerUser(
            @Valid @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestPart(value = "profilePicture", required = false) MultipartFile profilePicture) {
        try {
            User user = new User();
            user.setNames(username);
            user.setEmail(email);

            // Hashing the password
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(password);
            user.setPassword(hashedPassword);

            User theUser = userService.registerUser(user);

            if (theUser == null) {
                return new ResponseEntity<>("User with email: " + email + " already exists",
                        HttpStatus.BAD_REQUEST);
            } else {
                if (profilePicture != null && !profilePicture.isEmpty()) {
                    userService.uploadProfilePicture(user.getId(), profilePicture);
                }

                return new ResponseEntity<>(theUser, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/")
    public ResponseEntity<?> getAllUsers() {
        try {
            List<User> users = userService.getAllUsers();
            return new ResponseEntity<>(users, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping("/{userId}/blogs")
    public ResponseEntity<?> getUserBlogs(@PathVariable UUID userId) {
        // Call the service method to retrieve the user's blogs
        List<Blog> userBlogs = userService.getUserBlogs(userId);

        // Check if the user's blogs were found
        if (!userBlogs.isEmpty()) {
            return ResponseEntity.ok(userBlogs);
        } else {
            return ResponseEntity.notFound().build();
        }
    }

    @GetMapping("/{id}")
    public ResponseEntity<?> getUser(@PathVariable UUID id) {
        try {
            User user = userService.getUserById(id);
            return new ResponseEntity<>(user, HttpStatus.OK);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal server error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping(value = "/", consumes = { MediaType.MULTIPART_FORM_DATA_VALUE }, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> updateUser(
            @Valid @RequestParam("id") UUID id,
            @RequestParam("username") String username,
            @RequestParam("email") String email,
            @RequestParam("password") String password,
            @RequestParam(value = "profilePicture", required = false) MultipartFile profilePicture) {
        try {
            // Create a new User object with the received attributes
            User user = new User();
            user.setId(id);
            user.setNames(username);
            user.setEmail(email);

            // Hashing the password
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(password);
            user.setPassword(hashedPassword);

            // Check if profilePicture is provided
            if (profilePicture != null && !profilePicture.isEmpty()) {
                userService.uploadProfilePicture(user.getId(), profilePicture);
            }

            // Update the user
            String feedback = userService.updateUser(user);

            // Check the update result
            if (feedback.equalsIgnoreCase("User updated successfully.")) {
                return new ResponseEntity<>(feedback, HttpStatus.OK);
            } else if (feedback.equalsIgnoreCase("User not found")) {
                return new ResponseEntity<>(feedback, HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>("Unknown error occurred", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> deleteUser(@PathVariable UUID id) {
        try {
            String feedback = userService.deleteUser(id);
            if (feedback.equalsIgnoreCase("User deleted successfully")) {
                return new ResponseEntity<>(feedback, HttpStatus.OK);
            } else if (feedback.equalsIgnoreCase("User not found")) {
                return new ResponseEntity<>(feedback, HttpStatus.BAD_REQUEST);
            } else {
                return new ResponseEntity<>("Unkown error occured", HttpStatus.BAD_REQUEST);
            }
        } catch (Exception ex) {
            ex.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
