package tech.mag.blog.auth;

import java.util.HashMap;
import java.util.Map;
import java.util.Optional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.security.authentication.AuthenticationManager;
import org.springframework.security.authentication.BadCredentialsException;
import org.springframework.security.authentication.UsernamePasswordAuthenticationToken;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.validation.annotation.Validated;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import jakarta.servlet.http.Cookie;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.validation.Valid;
import tech.mag.blog.config.JwtService;
import tech.mag.blog.config.mail.EmailService;
import tech.mag.blog.user.User;
import tech.mag.blog.user.UserRepository;
import tech.mag.blog.user.UserService;

@RestController
@RequestMapping("/api/auth")
@Validated
public class AuthController {

    @Autowired
    private UserService userService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private AuthenticationManager authenticationManager;

    @Autowired
    private JwtService jwtService;

    @Autowired
    private EmailService emailService;

    @PostMapping("/login")
    public ResponseEntity<Map<String, String>> authenticateUser(@Valid @RequestBody LoginRequest loginRequest,
            HttpServletResponse response) {
        Map<String, String> responseBody = new HashMap<>();
        try {
            authenticationManager.authenticate(
                    new UsernamePasswordAuthenticationToken(loginRequest.getEmail(), loginRequest.getPassword()));

            final UserDetails userDetails = userService.loadUserByUsername(loginRequest.getEmail());

            Optional<User> user = userRepository.findByEmail(loginRequest.getEmail());
            User theUser = user.orElse(null);
            final String token = jwtService.generateToken(userDetails);

            // Create a cookie with the token
            Cookie cookie = new Cookie("token", token);
            cookie.setHttpOnly(true);
            cookie.setSecure(false);
            cookie.setPath("/");
            cookie.setMaxAge(24 * 60 * 60);

            response.addCookie(cookie);

            // Map<String, String> responseBody = new HashMap<>();
            responseBody.put("message", "Login successful");
            if (theUser != null) {
                responseBody.put("userId", theUser.getId().toString());
            }
            return ResponseEntity.ok(responseBody);
        } catch (BadCredentialsException e) {
            responseBody.put("error", "Invalid email or password");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        } catch (UsernameNotFoundException e) {
            responseBody.put("error", "User not found");
            return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(responseBody);
        } catch (Exception e) {
            e.printStackTrace();
            responseBody.put("error", "Internal server error");
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(responseBody);
        }
    }

    @PostMapping(value = "/register", consumes = { MediaType.APPLICATION_JSON_VALUE }, produces = {
            MediaType.APPLICATION_JSON_VALUE })
    public ResponseEntity<?> registerUser(
            @Valid @RequestBody UserCreationRequest creationUser) {
        try {

            User user = new User();
            user.setNames(creationUser.getUsername());
            user.setEmail(creationUser.getEmail());

            // Hashing the password
            BCryptPasswordEncoder passwordEncoder = new BCryptPasswordEncoder();
            String hashedPassword = passwordEncoder.encode(creationUser.getPassword());
            user.setPassword(hashedPassword);

            User theUser = userService.registerUser(user);

            if (theUser == null) {
                return new ResponseEntity<>("User with email: " + creationUser.getEmail() + " already exists",
                        HttpStatus.BAD_REQUEST);
            } else {
                String to = user.getEmail();

                String subject = "Account created for " + user.getUsername();

                String htmlContent = emailService.renderHtmlTemplate(
                        "Account Creation Successful!",
                        "<p>Dear " + user.getNames() + ",</p>" +
                                "<p>We are thrilled to confirm that your account has been successfully created. You can now enjoy the following features on our platform:</p>"
                                +
                                "<ul>" +
                                "<li>Commenting on posts</li>" +
                                "<li>Liking and sharing posts</li>" +
                                "<li>Authoring your own blog posts</li>" +
                                "</ul>" +
                                "<p>We are excited to have you join our community. Feel free to <a href='http://localhost:8081/swagger-ui/index.html'>explore more</a> and start sharing your ideas.</p>"
                                +
                                "<p>Cheers,</p>" +
                                "<p>The Paty Blogging Team</p>",
                        "https://magbrand.netlify.app/blog-pages/blog");

                emailService.sendHtmlEmail(to, subject, htmlContent);
                return new ResponseEntity<>(theUser, HttpStatus.OK);
            }
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}
