package me.redoak.edean.pricewatch.shops.ottode;

import me.redoak.edean.pricewatch.logic.TransformationException;
import me.redoak.edean.pricewatch.shops.ottode.OttoDEUrlTransformer;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;

public class OttoDEUrlTransformerTest {

    private OttoDEUrlTransformer transformer = new OttoDEUrlTransformer();

    @ParameterizedTest
    @CsvFileSource(resources = "/me/redoak/edean/pricewatch/shops/ottode/ottode_url_parameters.csv")
    public void testApply(String urlStr, boolean shouldSucceed) {
        if(shouldSucceed) {
            URL url = transformer.apply(urlStr);
            assertThat(url.getHost(), equalTo("otto.de"));
            assertThat(url.getPath(), equalTo("/p/1243459966"));
        } else {
            try {
                transformer.apply(urlStr);
                fail();
            } catch (TransformationException e) {
                assertThat(e.getMessage(), endsWith("does not conform to OttoDEUrlTransformer."));
            }
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/me/redoak/edean/pricewatch/shops/ottode/ottode_url_parameters.csv")
    public void testAppliesFor(String url, boolean expected) {
        assertThat(transformer.appliesFor(url), equalTo(expected));
    }
}
