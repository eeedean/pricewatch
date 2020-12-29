package me.redoak.edean.pricewatch.shops.home24;

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
public class Home24ProductUpdaterTest {

    @Autowired
    private TrackedProductRepository repo;

    @Autowired
    private Home24ProductUpdater updater;

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
        Mockito.doReturn("<span data-testid=\"current-price\" display=\"inline-block\" class=\"css-r5j3kg\">8,49 €</span><h1 data-testid=\"article-name\" font-weight=\"bold\" font-size=\"fs_18,fs_20,,fs_24\" color=\"gray.16\" class=\"css-1jyj9ij\">Schwebetürenschrank SKØP</h1><h2 data-testid=\"article-variant-name\" font-size=\"fs_14,fs_16\" font-weight=\"normal\" color=\"gray.15\" class=\"css-rnxbpl\">Höhe: 222 cm - Breite: 181 cm - 2 - Classic</h2>")
                .when(webClientMock).getContent(Mockito.any());

        BigDecimal oldPrice = BigDecimal.valueOf(550, 2);

        assertThat(updater.update(TrackedProduct.builder()
                .price(oldPrice)
                .oldPrice(BigDecimal.TEN)
                .build()), equalTo(true));
        TrackedProduct product = repo.findAll().get(0);
        assertThat(product.getOldPrice(), equalTo(oldPrice));
        assertThat(product.getPrice(), equalTo(BigDecimal.valueOf(849, 2)));
        assertThat(product.getName(), equalTo("Schwebetürenschrank SKØP (Höhe: 222 cm - Breite: 181 cm - 2 - Classic)"));
    }

    @Test
    public void testWithoutOldPrice() {
        Mockito.doReturn("<span data-testid=\"current-price\" display=\"inline-block\" class=\"css-r5j3kg\">679,99 €</span><h1 data-testid=\"article-name\" font-weight=\"bold\" font-size=\"fs_18,fs_20,,fs_24\" color=\"gray.16\" class=\"css-1jyj9ij\">Schwebetürenschrank SKØP</h1><h2 data-testid=\"article-variant-name\" font-size=\"fs_14,fs_16\" font-weight=\"normal\" color=\"gray.15\" class=\"css-rnxbpl\">Höhe: 222 cm - Breite: 181 cm - 2 - Classic</h2>")
                .when(webClientMock).getContent(Mockito.any());

        assertThat(updater.update(TrackedProduct.builder()
                .build()), equalTo(true));
        TrackedProduct product = repo.findAll().get(0);
        assertThat(product.getOldPrice(), nullValue());
        assertThat(product.getPrice(), equalTo(BigDecimal.valueOf(67999, 2)));
        assertThat(product.getName(), equalTo("Schwebetürenschrank SKØP (Höhe: 222 cm - Breite: 181 cm - 2 - Classic)"));
    }

    @Test
    public void testWithoutNewPrice() {
        Mockito.doReturn("<h1 data-testid=\"article-name\" font-weight=\"bold\" font-size=\"fs_18,fs_20,,fs_24\" color=\"gray.16\" class=\"css-1jyj9ij\">Schwebetürenschrank SKØP</h1><h2 data-testid=\"article-variant-name\" font-size=\"fs_14,fs_16\" font-weight=\"normal\" color=\"gray.15\" class=\"css-rnxbpl\">Höhe: 222 cm - Breite: 181 cm - 2 - Classic</h2>")
                .when(webClientMock).getContent(Mockito.any());

        assertThat(updater.update(TrackedProduct.builder()
                .price(BigDecimal.valueOf(1000, 2))
                .build()), equalTo(true));
        TrackedProduct product = repo.findAll().get(0);
        assertThat(product.getOldPrice(), nullValue());
        assertThat(product.getPrice(), equalTo(BigDecimal.valueOf(1000, 2)));
        assertThat(product.getName(), equalTo("Schwebetürenschrank SKØP (Höhe: 222 cm - Breite: 181 cm - 2 - Classic)"));
    }

    @Test
    public void testWithoutNewTitle() {
        Mockito.doReturn("<span data-testid=\"current-price\" display=\"inline-block\" class=\"css-r5j3kg\">679,99 €</span>")
                .when(webClientMock).getContent(Mockito.any());

        assertThat(updater.update(TrackedProduct.builder()
                .price(BigDecimal.valueOf(1000, 2))
                .name("TestName1")
                .build()), equalTo(true));
        TrackedProduct product = repo.findAll().get(0);
        assertThat(product.getOldPrice(), equalTo(BigDecimal.valueOf(1000, 2)));
        assertThat(product.getPrice(), equalTo(BigDecimal.valueOf(67999, 2)));
        assertThat(product.getName(), equalTo("TestName1"));
    }
}
