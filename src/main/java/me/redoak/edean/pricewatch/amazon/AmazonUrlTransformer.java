package me.redoak.edean.pricewatch.amazon;

import me.redoak.edean.pricewatch.logic.Shop;
import me.redoak.edean.pricewatch.logic.TransformationException;
import me.redoak.edean.pricewatch.logic.UrlTransformer;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An Transformer for creating {@link Shop#AMAZON} {@link URL}s
 */
@Component
public class AmazonUrlTransformer implements UrlTransformer {

    @Override
    public URL apply(String url) {
        if(url == null || !this.appliesFor(url))
            throw new TransformationException("Given URL '" + url + "' does not conform to AmazonUrlTransformer.");

        Matcher matcher = Pattern.compile("(dp/[Aa0-Zz9]+)").matcher(url);
        matcher.find();
        String match = matcher.group();
        try {
            return new URL("https://amazon.de/" + match);
        } catch (MalformedURLException e) {
            throw new TransformationException("There was an issue creating the URL. Found id: " + match, e);
        }
    }

    @Override
    public Shop transformsFor() {
        return Shop.AMAZON;
    }

    @Override
    public boolean appliesFor(String url) {
        return url != null && url.toLowerCase().contains("amazon.de");
    }
}
