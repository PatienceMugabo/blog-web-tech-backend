package tech.mag.blog.Subscriber;

import java.util.List;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
public class SubscriberService {

    @Autowired
    private SubscriberRepository subscriberRepository;

    public List<Subscriber> getAllSubscriber() {
        List<Subscriber> subscribers = subscriberRepository.findAll(Sort.by(Sort.Direction.DESC, "joinDate"));
        return subscribers;
    }

    public String subscribeOnBlog(Subscriber subscriber) {
        if (subscriberRepository.findByEmail(subscriber.getEmail()).isPresent()) {
            return "You are already subscribed to our email";
        }
        ;
        Subscriber theSubscriber = subscriberRepository.save(subscriber);
        if (theSubscriber != null) {
            return "You have successfully subscribed to our blog";
        } else {
            return "subscription failed";
        }
    }

    public String updateSubscriptionStatus(Subscriber subscriber) {
        Subscriber theSubscriber = subscriberRepository.save(subscriber);
        if (theSubscriber != null) {
            return "You have been unsubscribed to our channel";
        } else {
            return "An error occured while trying to subscribe please try again laterx";
        }
    }
}
