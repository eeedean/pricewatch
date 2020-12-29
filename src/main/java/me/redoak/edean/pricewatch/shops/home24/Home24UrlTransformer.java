package me.redoak.edean.pricewatch.shops.home24;

import me.redoak.edean.pricewatch.logic.TransformationException;
import me.redoak.edean.pricewatch.logic.UrlTransformer;
import me.redoak.edean.pricewatch.shops.Shop;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An Transformer for creating {@link Shop#HOME24} {@link URL}s
 */
@Component
public class Home24UrlTransformer implements UrlTransformer {

    @Override
    public URL apply(String url) {
        if(url == null || !this.appliesFor(url))
            throw new TransformationException("Given URL '" + url + "' does not conform to Home24UrlTransformer.");

        Matcher matcher = Pattern.compile("(produkt\\/[A-Za-z0-9\\-]+)").matcher(url);
        matcher.find();
        String match = matcher.group();
        try {
            return new URL("https://home24.de/" + match);
        } catch (MalformedURLException e) {
            throw new TransformationException("There was an issue creating the URL. Found id: " + match, e);
        }
    }

    @Override
    public Shop transformsFor() {
        return Shop.HOME24;
    }

    @Override
    public boolean appliesFor(String url) {
        return url != null && url.toLowerCase().contains("home24.de/produkt/");
    }
}
