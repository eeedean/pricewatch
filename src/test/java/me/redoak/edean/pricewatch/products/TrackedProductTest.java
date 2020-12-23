package me.redoak.edean.pricewatch.products;

import me.redoak.edean.pricewatch.logic.Shop;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.dao.IncorrectResultSizeDataAccessException;

import java.net.URL;
import java.util.List;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.fail;

@Slf4j
@DataJpaTest
public class TrackedProductTest {

    @Autowired
    private TrackedProductRepository repo;

    @AfterEach
    public void tearDown() {
        repo.deleteAll();
    }

    @Test
    public void testSave() {
        TrackedProduct product = TrackedProduct.builder().build();
        product = repo.save(product);
        TrackedProduct read = repo.findById(product.getId()).get();
        assertThat(product).isEqualTo(read);
        // I really want to make sure, that my UUID generation works out 😉
        assertThat(product.getId()).isNotNull();
        assertThat(product.getId()).isNotEqualTo("");
    }

    @Test
    public void testFindAllByShop() {
        Shop desiredShop = Shop.AMAZON;
        TrackedProduct product = repo.save(TrackedProduct.builder().shop(desiredShop).build());
        repo.save(TrackedProduct.builder().shop(Shop.MINDFACTORY).build());
        List<TrackedProduct> read = repo.findAllByShopEagerSubscribers(desiredShop);
        assertThat(read.size()).isEqualTo(1);
        assertThat(read.get(0)).isEqualTo(product);
    }

    @Test
    public void testFindAllByUrl() throws Exception{
        URL desiredUrl = new URL("https://test.meme");
        TrackedProduct product = repo.save(TrackedProduct.builder().url(desiredUrl).build());
        repo.save(TrackedProduct.builder().url(new URL("https://antohertest.meme")).build());
        TrackedProduct read = repo.findByUrl(desiredUrl).get();
        assertThat(read).isEqualTo(product);
    }

    @Test
    public void testFindAllByUrlMultipleExisting() throws Exception{
        URL desiredUrl = new URL("https://test.meme");
        repo.save(TrackedProduct.builder().url(desiredUrl).build());
        repo.save(TrackedProduct.builder().url(desiredUrl).build());
        repo.save(TrackedProduct.builder().url(new URL("https://antohertest.meme")).build());
        try {
            repo.findByUrl(desiredUrl).get();
            fail("Exception not thrown");
        } catch (IncorrectResultSizeDataAccessException e){
            assertThat(e.getMessage())
                    .isEqualTo("query did not return a unique result: 2; nested exception is javax.persistence.NonUniqueResultException: " +
                            "query did not return a unique result: 2");
        }
    }
}
