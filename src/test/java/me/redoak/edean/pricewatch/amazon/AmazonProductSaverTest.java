package me.redoak.edean.pricewatch.amazon;

import me.redoak.edean.pricewatch.logic.Shop;
import me.redoak.edean.pricewatch.products.TrackedProduct;
import me.redoak.edean.pricewatch.products.TrackedProductRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.TestInstance;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.transaction.Transactional;
import java.net.URL;

import static org.assertj.core.api.Assertions.assertThat;

@TestInstance(TestInstance.Lifecycle.PER_CLASS)
@SpringBootTest
public class AmazonProductSaverTest {

    @Autowired
    private AmazonProductSaver saver;

    @Autowired
    private TrackedProductRepository repo;

    @AfterAll
    public void tearDown() {
        repo.deleteAll();
    }

    @Transactional
    @Test
    public void testSave() throws Exception {
        URL url = new URL("https://test.mee");
        TrackedProduct product = saver.save(url);
        assertThat(product).isNotNull();
        assertThat(product.getId()).isNotNull();
        assertThat(product.getId()).isNotEqualTo("");
        Assertions.assertThat(product.getShop()).isEqualTo(Shop.AMAZON);
        TrackedProduct anotherProduct = saver.save(url);
        assertThat(anotherProduct).isNotNull();
        assertThat(anotherProduct).isEqualTo(product);
    }
}
