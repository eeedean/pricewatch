package me.redoak.edean.pricewatch.products;

import lombok.extern.slf4j.Slf4j;
import me.redoak.edean.pricewatch.subscribers.Subscriber;
import me.redoak.edean.pricewatch.subscribers.SubscriberService;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityManager;
import javax.transaction.Transactional;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;

@Slf4j
@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class TrackedProductServiceTest {

    private static final String TEST_PASSWORD = "hehe";
    private static final ProductRequest TEST_PRODUCT_REQUEST = ProductRequest.builder().url("https://amazon.de/dp/B07TC2BK1X").build();

    @Autowired
    private EntityManager em;

    @Autowired
    private TrackedProductRepository trackedProductRepository;

    @Autowired
    private TrackedProductService trackedProductService;

    @Autowired
    private SubscriberService subscriberService;
    private Subscriber subscriber;

    @BeforeAll
    public void setUp() {
        log.info("registering test subscriber");
        subscriber = subscriberService.register(Subscriber.builder().name("boy").build(), TEST_PASSWORD);
    }

    @AfterAll
    public void tearDown() {
        log.info("unregistering test subscriber");
        subscriberService.unregister(subscriber);
    }

    @AfterEach
    public void tearDownEach() {
        log.info("removing all TrackedProducts");
        trackedProductRepository.deleteAll();
    }

    @Transactional
    @Test
    public void testSubscribe() {
        TrackedProduct product = trackedProductService.subscribe(TEST_PRODUCT_REQUEST, subscriber);
        assertThat(product.getSubscribers().size()).isEqualTo(1);
        assertThat(product.getSubscribers().stream().findFirst().get().getTrackedProducts().size()).isEqualTo(1);

        List<TrackedProduct> allProducts = em.createQuery("select p from TrackedProduct p left join fetch p.subscribers", TrackedProduct.class).getResultList();
        assertThat(allProducts.size()).isEqualTo(1);
        assertThat(product).isEqualTo(allProducts.get(0));
        assertThat(product.getSubscribers().stream().findFirst().get()).isEqualTo(allProducts.get(0).getSubscribers().stream().findFirst().get());
    }

    @Transactional
    @Test
    public void testUnsubscribe() {
        TrackedProduct product = trackedProductService.subscribe(TEST_PRODUCT_REQUEST, subscriber);

        trackedProductService.unsubscribe(TEST_PRODUCT_REQUEST, subscriber);

        List<TrackedProduct> allProducts = em.createQuery("select p from TrackedProduct p left join fetch p.subscribers", TrackedProduct.class).getResultList();
        assertThat(allProducts.size()).isEqualTo(1);
        TrackedProduct savedProduct = allProducts.get(0);
        assertThat(product).isEqualTo(savedProduct);
        log.debug("Subscribers: {}", savedProduct.getSubscribers());
        assertThat(savedProduct.getSubscribers().size()).isEqualTo(0);
        assertThat(subscriberService.auth(subscriber.getName(), TEST_PASSWORD).getTrackedProducts().size()).isEqualTo(0);
    }
}
