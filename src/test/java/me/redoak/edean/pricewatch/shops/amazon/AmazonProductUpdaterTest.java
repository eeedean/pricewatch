package me.redoak.edean.pricewatch.shops.amazon;

import me.redoak.edean.pricewatch.products.TrackedProduct;
import me.redoak.edean.pricewatch.products.TrackedProductRepository;
import me.redoak.edean.pricewatch.logic.WebClient;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mockito;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.math.BigDecimal;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.equalTo;
import static org.hamcrest.Matchers.nullValue;

@SpringBootTest
public class AmazonProductUpdaterTest {

    @Autowired
    private TrackedProductRepository repo;

    @Autowired
    private AmazonProductUpdater updater;

    @Autowired
    private WebClient webClient;

    private WebClient webClientMock = Mockito.mock(WebClient.class);

    @BeforeEach
    public void setUp() {
        updater.setWebClient(webClientMock);
    }

    @AfterEach
    public void tearDown() {
        updater.setWebClient(webClient);
        repo.deleteAll();
    }

    @Test
    public void testWithOldPrice() {
        Mockito.doReturn("<span id=\"priceblock_ourprice\" class=\"a-size-medium a-color-price\">EUR 8,49</span><span id=\"productTitle\">TestName </span>")
                .when(webClientMock).getContent(Mockito.any());

        BigDecimal oldPrice = BigDecimal.valueOf(550, 2);

        assertThat(updater.update(TrackedProduct.builder()
                .price(oldPrice)
                .oldPrice(BigDecimal.TEN)
                .build()), equalTo(true));
        TrackedProduct product = repo.findAll().get(0);
        assertThat(product.getOldPrice(), equalTo(oldPrice));
        assertThat(product.getPrice(), equalTo(BigDecimal.valueOf(849, 2)));
        assertThat(product.getName(), equalTo("TestName"));
    }

    @Test
    public void testWithoutOldPrice() {
        Mockito.doReturn("<span id=\"priceblock_ourprice\" class=\"a-size-medium a-color-price\">EUR 8,49</span><span id=\"productTitle\">TestName </span>")
                .when(webClientMock).getContent(Mockito.any());

        assertThat(updater.update(TrackedProduct.builder()
                .build()), equalTo(true));
        TrackedProduct product = repo.findAll().get(0);
        assertThat(product.getOldPrice(), nullValue());
        assertThat(product.getPrice(), equalTo(BigDecimal.valueOf(849, 2)));
        assertThat(product.getName(), equalTo("TestName"));
    }

    @Test
    public void testWithoutNewPrice() {
        Mockito.doReturn("<span id=\"productTitle\">TestName </span>")
                .when(webClientMock).getContent(Mockito.any());

        assertThat(updater.update(TrackedProduct.builder()
                .price(BigDecimal.valueOf(1000, 2))
                .build()), equalTo(true));
        TrackedProduct product = repo.findAll().get(0);
        assertThat(product.getOldPrice(), nullValue());
        assertThat(product.getPrice(), equalTo(BigDecimal.valueOf(1000, 2)));
        assertThat(product.getName(), equalTo("TestName"));
    }

    @Test
    public void testWithoutNewTitle() {
        Mockito.doReturn("<span id=\"priceblock_ourprice\" class=\"a-size-medium a-color-price\">EUR 8,49</span>")
                .when(webClientMock).getContent(Mockito.any());

        assertThat(updater.update(TrackedProduct.builder()
                .price(BigDecimal.valueOf(1000, 2))
                .name("TestName1")
                .build()), equalTo(true));
        TrackedProduct product = repo.findAll().get(0);
        assertThat(product.getOldPrice(), equalTo(BigDecimal.valueOf(1000, 2)));
        assertThat(product.getPrice(), equalTo(BigDecimal.valueOf(849, 2)));
        assertThat(product.getName(), equalTo("TestName1"));
    }

    @Test
    public void testDealPrice() {
        Mockito.doReturn("<span id=\"priceblock_dealprice\" class=\"a-size-medium a-color-price priceBlockDealPriceString\">8,49&nbsp;€</span>")
                .when(webClientMock).getContent(Mockito.any());

        assertThat(updater.update(TrackedProduct.builder()
                .price(BigDecimal.valueOf(1000, 2))
                .name("TestName1")
                .build()), equalTo(true));
        TrackedProduct product = repo.findAll().get(0);
        assertThat(product.getOldPrice(), equalTo(BigDecimal.valueOf(1000, 2)));
        assertThat(product.getPrice(), equalTo(BigDecimal.valueOf(849, 2)));
        assertThat(product.getName(), equalTo("TestName1"));
    }
}
