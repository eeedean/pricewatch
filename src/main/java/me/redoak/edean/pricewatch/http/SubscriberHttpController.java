package me.redoak.edean.pricewatch.http;

import me.redoak.edean.pricewatch.subscribers.Subscriber;
import me.redoak.edean.pricewatch.subscribers.SubscriberService;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

@RestController
public class SubscriberHttpController {

    private final SubscriberService subscriberService;

    public SubscriberHttpController(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    /**
     * Creates a new Subscriber. Username and password must be given in HTTP-Headers (PricewatchUser and PricewatchPassword).
     * An E-Mail-Address may be given in Request. In order to change data, jsut register again with username and correct password.
     *
     * @param requestedSubscriber An E-Mail-Address may be given in Request
     * @return The created subscriber
     */
    @PostMapping(path = "/register")
    public ResponseEntity<Subscriber> register(@RequestBody Subscriber requestedSubscriber,
                                               @RequestHeader("PricewatchUser") String user,
                                               @RequestHeader("PricewatchPassword") String password) {

        requestedSubscriber.setName(user);
        Subscriber result = subscriberService.register(requestedSubscriber, password);
        return ResponseEntity.ok(result);
    }
}
