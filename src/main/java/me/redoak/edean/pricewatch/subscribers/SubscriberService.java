package me.redoak.edean.pricewatch.subscribers;

import lombok.extern.slf4j.Slf4j;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;

@Slf4j
@Service
public class SubscriberService {

    private final SubscriberRepository subscriberRepository;
    private final PasswordEncoder passwordEncoder = new BCryptPasswordEncoder();

    public SubscriberService(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    public Subscriber auth(String name, String password) {
        var opt = subscriberRepository.findByName(name);
        if (opt.isPresent()) {
            var readSubscriber = opt.get();
            if(passwordEncoder.matches(password, readSubscriber.getPasswordHash())) {
                return readSubscriber;
            } else {
                throw new SecurityException("That's not your account!");
            }
        } else {
            throw new SecurityException("Who are you even?");
        }
    }

    @Transactional
    public Subscriber register(Subscriber request, String password) {
        Subscriber result = null;
        var encodedpw = passwordEncoder.encode(password);
        var opt = subscriberRepository.findByName(request.getName());
        if (opt.isPresent()) {
            var readSubscriber = opt.get();
            if(passwordEncoder.matches(password, readSubscriber.getPasswordHash())) {
                result = readSubscriber;
                doChanges(request, result);
            } else {
                throw new SecurityException("That's not your account!");
            }
        } else {
            result = request;
            result.setPasswordHash(encodedpw);
        }

        return subscriberRepository.save(result);
    }

    private void doChanges(Subscriber request, Subscriber result) {
        result.setEmail(request.getEmail());
    }

    @Transactional
    public void unregister(Subscriber subscriber) {
        subscriberRepository.deleteById(subscriber.getId());
    }
}
