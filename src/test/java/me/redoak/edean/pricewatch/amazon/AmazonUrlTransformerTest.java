package me.redoak.edean.pricewatch.amazon;

import me.redoak.edean.pricewatch.logic.TransformationException;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvFileSource;

import java.net.URL;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.Matchers.endsWith;
import static org.hamcrest.Matchers.equalTo;
import static org.junit.jupiter.api.Assertions.fail;

public class AmazonUrlTransformerTest {

    private AmazonUrlTransformer transformer = new AmazonUrlTransformer();

    @ParameterizedTest
    @CsvFileSource(resources = "/me/redoak/edean/pricewatch/amazon/amazon_url_parameters.csv")
    public void testApply(String urlStr, boolean shouldSucceed) {
        if(shouldSucceed) {
            URL url = transformer.apply(urlStr);
            assertThat(url.getHost(), equalTo("amazon.de"));
            assertThat(url.getPath(), equalTo("/dp/B01CFWCN14"));
        } else {
            try {
                transformer.apply(urlStr);
                fail();
            } catch (TransformationException e) {
                assertThat(e.getMessage(), endsWith("does not conform to AmazonUrlTransformer."));
            }
        }
    }

    @ParameterizedTest
    @CsvFileSource(resources = "/me/redoak/edean/pricewatch/amazon/amazon_url_parameters.csv")
    public void testAppliesFor(String url, boolean expected) {
        assertThat(transformer.appliesFor(url), equalTo(expected));
    }
}
