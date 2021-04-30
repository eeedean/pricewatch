package me.redoak.edean.pricewatch.shops.ottode;

import me.redoak.edean.pricewatch.logic.WebClient;
import me.redoak.edean.pricewatch.products.TrackedProduct;
import me.redoak.edean.pricewatch.products.TrackedProductRepository;
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
public class OttoDEProductUpdaterTest {

    @Autowired
    private TrackedProductRepository repo;

    @Autowired
    private OttoDEProductUpdater updater;

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
        Mockito.doReturn("<meta itemprop=\"name\" content=\"variationId\"/><body><span id=\"normalPriceAmount\" itemprop=\"price\" content=\"99.99\">99,99</span><h1 itemprop=\"name\" class=\"js_shortInfo__variationName prd_shortInfo__variationName\" data-qa=\"variationName\">Test Name-1</h1></body>")
                .when(webClientMock).getContent(Mockito.any());

        BigDecimal oldPrice = BigDecimal.valueOf(550, 2);

        assertThat(updater.update(TrackedProduct.builder()
                .price(oldPrice)
                .oldPrice(BigDecimal.TEN)
                .build()), equalTo(true));
        TrackedProduct product = repo.findAll().get(0);
        assertThat(product.getOldPrice(), equalTo(oldPrice));
        assertThat(product.getPrice(), equalTo(BigDecimal.valueOf(9999, 2)));
        assertThat(product.getName(), equalTo("Test Name-1"));
    }

    @Test
    public void testWithoutOldPrice() {
        Mockito.doReturn("<meta itemprop=\"name\" content=\"variationId\"/><body><span id=\"normalPriceAmount\" itemprop=\"price\" content=\"99.99\">99,99</span><h1 itemprop=\"name\" class=\"js_shortInfo__variationName prd_shortInfo__variationName\" data-qa=\"variationName\">Test Name-1</h1></body>")
                .when(webClientMock).getContent(Mockito.any());

        assertThat(updater.update(TrackedProduct.builder()
                .build()), equalTo(true));
        TrackedProduct product = repo.findAll().get(0);
        assertThat(product.getOldPrice(), nullValue());
        assertThat(product.getPrice(), equalTo(BigDecimal.valueOf(9999, 2)));
        assertThat(product.getName(), equalTo("Test Name-1"));
    }

    @Test
    public void testWithoutNewPrice() {
        Mockito.doReturn("<meta itemprop=\"name\" content=\"variationId\"/><body><h1 itemprop=\"name\" class=\"js_shortInfo__variationName prd_shortInfo__variationName\" data-qa=\"variationName\">Test Name-1</h1></body>")
                .when(webClientMock).getContent(Mockito.any());

        assertThat(updater.update(TrackedProduct.builder()
                .price(BigDecimal.valueOf(1000, 2))
                .build()), equalTo(true));
        TrackedProduct product = repo.findAll().get(0);
        assertThat(product.getOldPrice(), nullValue());
        assertThat(product.getPrice(), equalTo(BigDecimal.valueOf(1000, 2)));
        assertThat(product.getName(), equalTo("Test Name-1"));
    }

    @Test
    public void testWithoutNewTitle() {
        Mockito.doReturn("<meta itemprop=\"name\" content=\"variationId\"/><body><span id=\"normalPriceAmount\" itemprop=\"price\" content=\"99.99\">99,99</span></body>")
                .when(webClientMock).getContent(Mockito.any());

        assertThat(updater.update(TrackedProduct.builder()
                .price(BigDecimal.valueOf(1000, 2))
                .name("Test Name-1")
                .build()), equalTo(true));
        TrackedProduct product = repo.findAll().get(0);
        assertThat(product.getOldPrice(), equalTo(BigDecimal.valueOf(1000, 2)));
        assertThat(product.getPrice(), equalTo(BigDecimal.valueOf(9999, 2)));
        assertThat(product.getName(), equalTo("Test Name-1"));
    }
}
