package tech.mag.blog.user;

import java.util.List;
import java.util.Optional;
import java.util.UUID;

import org.hibernate.Hibernate;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.io.File;
import java.io.FileOutputStream;
import java.io.IOException;
import java.time.LocalDateTime;
import java.time.format.DateTimeFormatter;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.web.multipart.MultipartFile;

import jakarta.persistence.EntityNotFoundException;
import jakarta.transaction.Transactional;
import tech.mag.blog.blog.Blog;

@Service
public class UserService implements UserDetailsService {

    @Value("${profile.picture.upload.directory}")
    private String uploadDirectory;

    @Autowired
    private UserRepository userRepository;

    @Transactional
    public void initializeUserLikedBlogs(User user) {
        Hibernate.initialize(user.getLikedBlogs());
    }

    // a function to get all users
    public List<User> getAllUsers() {
        List<User> users = userRepository.findAll();
        return users;
    }

    // a function to get a user by id
    public User getUserById(UUID id) {

        Optional<User> user = userRepository.findById(id);

        User thUser = null;
        if (user.isPresent()) {
            thUser = user.get();
            return thUser;
        }
        return null;
    }

    // a function to save new user
    public User registerUser(User user) {
        Optional<User> theUser = userRepository.findByEmail(user.getEmail());
        if (theUser.isPresent()) {
            return null;
        } else {
            user = userRepository.save(user);
            return user;
        }
    }

    // a function to update user
    @Transactional
    public String updateUser(User user) {
        Optional<User> optionalUser = userRepository.findById(user.getId());
        if (optionalUser.isPresent()) {
            userRepository.save(user);
            return "User updated successfully.";
        } else {
            return "User not found.";
        }
    }

    // a function to delete user
    public String deleteUser(UUID id) {
        Optional<User> user = userRepository.findById(id);
        if (user.isPresent()) {
            userRepository.deleteById(id);
            return "User deleted successfully";
        } else {
            return "User not found.";
        }
    }

    // Function to handle image upload
    public String uploadProfilePicture(UUID userId, MultipartFile file) {
        try {

            String fileName = LocalDateTime.now().format(DateTimeFormatter.ofPattern("yyyyMMddHHmmss"))
                    + "_" + file.getOriginalFilename();

            // Create the upload directory if it doesn't exist
            File directory = new File(uploadDirectory);
            if (!directory.exists()) {
                directory.mkdirs();
            }

            // Save the file to the specified directory
            String filePath = uploadDirectory + fileName;
            FileOutputStream fos = new FileOutputStream(filePath);
            fos.write(file.getBytes());
            fos.close();

            // Update the user entity with the image path
            Optional<User> optionalUser = userRepository.findById(userId);
            if (optionalUser.isPresent()) {
                User user = optionalUser.get();
                user.setProfilePicture(fileName);
                userRepository.save(user);
                return "Profile picture uploaded successfully.";
            } else {
                return "User not found.";
            }
        } catch (IOException e) {
            e.printStackTrace();
            return "Failed to upload profile picture.";
        }
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Optional<User> user = userRepository.findByEmail(username);
        if (user.isPresent()) {
            User theUser = user.get();
            return theUser;
        } else {
            throw new UsernameNotFoundException("User not found with username: " + username);
        }
    }

    @Transactional
    public User getUserWithLikedBlogs(UUID userId) {
        return userRepository.findByIdWithLikedBlogs(userId)
                .orElseThrow(() -> new EntityNotFoundException("User not found"));
    }

    public List<Blog> getUserBlogs(UUID userId) {
        User user = userRepository.findById(userId).orElse(null);
        if (user != null) {
            return user.getBlogs();
        } else {
            return List.of();
        }
    }

}
