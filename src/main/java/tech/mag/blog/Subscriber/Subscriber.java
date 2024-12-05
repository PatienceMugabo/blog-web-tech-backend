package tech.mag.blog.Subscriber;

import java.util.Date;
import java.util.UUID;

import org.hibernate.annotations.CreationTimestamp;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Email;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.RequiredArgsConstructor;
import tech.mag.blog.util.ESubStatus;

@Data
@AllArgsConstructor
@RequiredArgsConstructor
@Entity
@Table(name = "subscribers")
public class Subscriber {

    @Id
    @GeneratedValue(strategy = GenerationType.UUID)
    private UUID id;

    @Column(name = "first_name")
    private String firstName;

    @Column(name = "last_name")
    private String lastName;

    @Email(message = "A valid email is required")
    @Column(nullable = false)
    private String email;

    @Enumerated(EnumType.STRING)
    private ESubStatus status = ESubStatus.SUBSCRIBED;

    @CreationTimestamp
    @Column(name = "join_date")
    private Date joinDate;

}
