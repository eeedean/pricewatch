package me.redoak.edean.pricewatch.shops.home24;

import me.redoak.edean.pricewatch.products.TrackedProduct;
import me.redoak.edean.pricewatch.products.TrackedProductRepository;
import me.redoak.edean.pricewatch.shops.Shop;
import me.redoak.edean.pricewatch.shops.amazon.AmazonProductSaver;
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
public class Home24ProductSaverTest {

    @Autowired
    private Home24ProductSaver saver;

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
        Assertions.assertThat(product.getShop()).isEqualTo(Shop.HOME24);
        TrackedProduct anotherProduct = saver.save(url);
        assertThat(anotherProduct).isNotNull();
        assertThat(anotherProduct).isEqualTo(product);
    }
}
