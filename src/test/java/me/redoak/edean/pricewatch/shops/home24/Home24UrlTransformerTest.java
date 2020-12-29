package me.redoak.edean.pricewatch.shops.home24;

import me.redoak.edean.pricewatch.logic.TransformationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;

public class Home24UrlTransformerTest {

    private Home24UrlTransformer transformer = new Home24UrlTransformer();

    @ParameterizedTest
    @CsvFileSource(resources = "/me/redoak/edean/pricewatch/shops/home24/home24_url_parameters.csv")
    public void testApply(String urlStr, boolean shouldSucceed) {
        if(shouldSucceed) {
            URL url = transformer.apply(urlStr);
            assertThat(url.getHost(), equalTo("home24.de"));
            assertThat(url.getPath(), equalTo("/produkt/schwebetuerenschrank-skoep-hoehe-222-cm-breite-181-cm-2-classic-1947"));
        } else {
            try {
                transformer.apply(urlStr);
                fail();
            } catch (TransformationException e) {
                assertThat(e.getMessage(), endsWith("does not conform to Home24UrlTransformer."));
            }
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/me/redoak/edean/pricewatch/shops/home24/home24_url_parameters.csv")
    public void testAppliesFor(String url, boolean expected) {
        assertThat(transformer.appliesFor(url), equalTo(expected));
    }
}
