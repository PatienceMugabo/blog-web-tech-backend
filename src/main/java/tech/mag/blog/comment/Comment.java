package tech.mag.blog.comment;

import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.NotBlank;
import jakarta.validation.constraints.Size;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import tech.mag.blog.blog.Blog;
import tech.mag.blog.user.User;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "blog_comments")
public class Comment {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @NotBlank(message = "comment content is requiredF")
    @Size(min = 10, max = 1024, message = "comment text must be between10 and 1024 characters ")
    @Column(name = "comment_text")
    private String text;

    @CreationTimestamp
    @Column(name = "comment_at")
    private Date commentAt;

    @ManyToOne(fetch = FetchType.EAGER)
    @JoinColumn(name = "writer_id")
    private User writer;

    @JsonIgnore
    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "blog_id")
    private Blog blog;

    @Override
    public String toString() {
        return "Comment{" +
                "id=" + id +
                ", content='" + text + '\'' +
                ", blogId=" + (blog != null ? blog.getId() : "null") +
                '}';
    }
}
