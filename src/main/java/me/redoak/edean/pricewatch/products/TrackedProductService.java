package me.redoak.edean.pricewatch.products;

import me.redoak.edean.pricewatch.logic.ProductSaver;
import me.redoak.edean.pricewatch.logic.Shop;
import me.redoak.edean.pricewatch.logic.UrlTransformer;
import me.redoak.edean.pricewatch.subscribers.Subscriber;
import lombok.extern.slf4j.Slf4j;
import me.redoak.edean.pricewatch.subscribers.SubscriberRepository;
import org.quartz.JobKey;
import org.quartz.Scheduler;
import org.quartz.SchedulerException;
import org.springframework.stereotype.Service;

import javax.transaction.Transactional;
import java.net.URL;
import java.util.EnumMap;
import java.util.List;
import java.util.Set;
import java.util.function.Function;
import java.util.stream.Collectors;

@Slf4j
@Service
public class TrackedProductService {

    private List<UrlTransformer> transformers;
    private EnumMap<Shop, ProductSaver> savers;

    private final TrackedProductRepository trackedProductRepository;
    private final SubscriberRepository subscriberRepository;

    private final Scheduler scheduler;

    public TrackedProductService(List<UrlTransformer> transformers,
                                 Set<ProductSaver> savers,
                                 TrackedProductRepository trackedProductRepository,
                                 SubscriberRepository subscriberRepository, Scheduler scheduler) {
        this.transformers = transformers;
        this.savers = new EnumMap<>(savers.stream()
                .collect(Collectors.toMap(ProductSaver::savesFor, Function.identity())));
        this.trackedProductRepository = trackedProductRepository;
        this.subscriberRepository = subscriberRepository;
        this.scheduler = scheduler;
    }

    @Transactional
    public void subscribeAll(Subscriber subscriber) {
        trackedProductRepository.findAll().stream()
                .peek(p -> log.debug("adding Subscriber {} to Product {}", subscriber.getId(), p.getId()))
                .peek(subscriber::addProduct)
                .forEach(trackedProductRepository::save);
    }

    public void triggerUpdate() throws SchedulerException {
        scheduler.triggerJob(JobKey.jobKey("updateAllProductsJobDetail"));
        log.info("manually scheduled update");
    }

    @Transactional
    public TrackedProduct subscribe(ProductRequest request, Subscriber subscriber) {
        UrlTransformer transformer = findUrlTransformer(request);
        URL url = transformer.apply(request.getUrl());
        ProductSaver saver = findProductSaver(transformer);
        TrackedProduct product = saver.save(url);
        product.addSubscriber(subscriber);

        return trackedProductRepository.save(product);
    }

    @Transactional
    public void unsubscribe(ProductRequest request, Subscriber subscriber) {
        UrlTransformer transformer = findUrlTransformer(request);
        URL url = transformer.apply(request.getUrl());
        trackedProductRepository.findByUrl(url).ifPresent((trackedProduct) -> {
            trackedProduct.removeSubscriber(subscriber);
            trackedProductRepository.save(trackedProduct);
            subscriberRepository.save(subscriber);
            log.debug("unsubscribed {} from {}", subscriber.getName(), trackedProduct.getUrl());
        });
    }

    private ProductSaver findProductSaver(UrlTransformer transformer) {
        ProductSaver saver = savers.get(transformer.transformsFor());
        if (saver == null) {
            throw new RuntimeException("No saver found for shop: " + transformer.transformsFor());
        }
        return saver;
    }

    private UrlTransformer findUrlTransformer(ProductRequest request) {
        return transformers.stream()
                .filter(t -> t.appliesFor(request.getUrl()))
                .findFirst().orElseThrow(() -> new RuntimeException("Did not find any transformer for given product."));
    }

    void setTransformers(List<UrlTransformer> transformers) {
        this.transformers = transformers;
    }

    List<UrlTransformer> getTransformers() {
        return this.transformers;
    }
}
