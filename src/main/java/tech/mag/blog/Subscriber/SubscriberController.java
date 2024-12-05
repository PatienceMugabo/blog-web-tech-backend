package tech.mag.blog.Subscriber;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import jakarta.mail.MessagingException;
import jakarta.validation.Valid;
import tech.mag.blog.config.mail.EmailService;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/subscribers")
public class SubscriberController {

    @Autowired
    private SubscriberService subscriberService;

    @Autowired
    private EmailService emailService;

    @GetMapping("/all")
    public ResponseEntity<List<Subscriber>> getAllSubscribers() {
        List<Subscriber> subscribers = subscriberService.getAllSubscriber();
        return ResponseEntity.ok(subscribers);
    }

    @PostMapping("/subscribe")
    public ResponseEntity<String> subscribeToBlog(@Valid @RequestBody Subscriber subscriber) throws MessagingException {
        try {

            String response = subscriberService.subscribeOnBlog(subscriber);

            if (response.equalsIgnoreCase("You are already subscribed to our email")
                    || response.equalsIgnoreCase("subscription failed")) {
                return new ResponseEntity<>(response, HttpStatus.BAD_REQUEST);
            } else {
                String to = subscriber.getEmail();

                String subject = "Subscription for " + subscriber.getFirstName() + " approval ";

                String htmlContent = emailService.renderHtmlTemplate(
                        "Thank you " + subscriber.getFirstName() + " for subscribing to Paty Blogs",
                        "This email serves as a confirmation that you have subscribed to our blog and you will recieve all updates from us.",
                        "http://localhost:8081/swagger-ui/index.html");

                emailService.sendHtmlEmail(to, subject, htmlContent);

                return ResponseEntity.ok(response);
            }

        } catch (Exception e) {
            e.getMessage();
            return new ResponseEntity<>("Internal Server Error", HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @PutMapping("/unsubscribe/{subsriberId}")
    public ResponseEntity<String> updateSubscriptionStatus(@PathVariable UUID subscriberId,
            @RequestBody Subscriber subscriber) {
        String response = subscriberService.updateSubscriptionStatus(subscriber);
        return ResponseEntity.ok(response);
    }
}
