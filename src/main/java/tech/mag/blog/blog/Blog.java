package tech.mag.blog.blog;

import java.util.ArrayList;
import java.util.Date;
import java.util.List;
import java.util.Set;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;
import org.hibernate.annotations.UpdateTimestamp;

import com.fasterxml.jackson.annotation.JsonBackReference;
import jakarta.persistence.*;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import tech.mag.blog.comment.Comment;
import tech.mag.blog.user.User;
import tech.mag.blog.util.EBlogCategory;
import tech.mag.blog.util.EStatus;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "blogs")
public class Blog {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Size(min = 5, max = 255, message = "title must be between 5 and 1024 characters")
    @NotBlank(message = "title is required and must be 5 characters minimum")
    @Column(name = "title", nullable = false, unique = true)
    private String title;

    @Size(min = 200, max = 10000, message = "content must be 100 characters minimum")
    @NotBlank(message = "content must not be empty")
    @Column(name = "post_content")
    private String content;

    @NotBlank(message = "A Featured image is required")
    @Column(name = "blog_thumbnail")
    private String blogThumbnail;

    @Column(name = "blog_status")
    @Enumerated(EnumType.STRING)
    private EStatus status = EStatus.PUBLISHED;

    @Column(name = "blog_category")
    @Enumerated(EnumType.STRING)
    private EBlogCategory category;

    @CreationTimestamp
    @Column(name = "created_at")
    private Date publicationDate;

    @UpdateTimestamp
    @Column(name = "updated_at")
    private Date lastModifiedDate;

    @JsonBackReference
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "author_id")
    private User author;

    @JsonBackReference
    @ManyToMany(mappedBy = "likedBlogs", fetch = FetchType.LAZY)
    private Set<User> likedByUsers;

    @JsonBackReference
    @OneToMany(mappedBy = "blog", cascade = CascadeType.ALL, orphanRemoval = true)
    List<Comment> comments = new ArrayList<>();

    @Override
    public String toString() {
        return "Blog{" +
                "id=" + id +
                ", title='" + title + '\'' +
                ", commentsCount=" + (comments != null ? comments.size() : "null") +
                '}';
    }
}
