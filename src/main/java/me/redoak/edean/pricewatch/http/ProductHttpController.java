package me.redoak.edean.pricewatch.http;

import me.redoak.edean.pricewatch.logic.ProductSaver;
import me.redoak.edean.pricewatch.shops.Shop;
import me.redoak.edean.pricewatch.products.ProductRequest;
import me.redoak.edean.pricewatch.products.TrackedProduct;
import me.redoak.edean.pricewatch.products.TrackedProductService;
import me.redoak.edean.pricewatch.subscribers.SubscriberService;
import org.quartz.SchedulerException;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestHeader;
import org.springframework.web.bind.annotation.RestController;

import javax.validation.Valid;
import java.util.EnumMap;

/**
 * Controller for adding products to be updated, start updates, and so on.
 */
@RestController
public class ProductHttpController {


    private SubscriberService subscriberService;
    private TrackedProductService trackedProductService;

    /**
     * {@link ProductSaver}s are being converted into an {@link EnumMap<Shop, ProductSaver>}.
     * @param subscriberService
     * @param trackedProductService
     */
    public ProductHttpController(SubscriberService subscriberService, TrackedProductService trackedProductService) {
        this.subscriberService = subscriberService;
        this.trackedProductService = trackedProductService;
    }

    @GetMapping(path = "subscribe/all")
    public ResponseEntity<Boolean> subscribeAll(
            @RequestHeader("PricewatchUser") String user,
            @RequestHeader("PricewatchPassword") String password) {
        var subscriber = subscriberService.auth(user, password);
        trackedProductService.subscribeAll(subscriber);
        return ResponseEntity.ok(true);
    }

    /**
     * HTTP-POST method for adding a product to be saved and tracked.
     *
     * @param request The {@link ProductRequest} for tracking a new product.
     * @return The saved {@link TrackedProduct}. Newly created, if not existing yet.
     */
    @PostMapping(path = "subscribe")
    public ResponseEntity<TrackedProduct> subscribe(@RequestBody @Valid ProductRequest request,
                                                    @RequestHeader("PricewatchUser") String user,
                                                    @RequestHeader("PricewatchPassword") String password) {
        var subscriber = subscriberService.auth(user, password);
        return ResponseEntity.ok(trackedProductService.subscribe(request, subscriber));
    }

    @GetMapping(path = "updateAll")
    public ResponseEntity<Boolean> updateall(
            @RequestHeader("PricewatchUser") String user,
            @RequestHeader("PricewatchPassword") String password) throws SchedulerException {
        var subscriber = subscriberService.auth(user, password);
        trackedProductService.triggerUpdate();
        return ResponseEntity.ok(true);
    }
}
