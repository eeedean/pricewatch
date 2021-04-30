package me.redoak.edean.pricewatch.shops.ottode;

import me.redoak.edean.pricewatch.logic.TransformationException;
import me.redoak.edean.pricewatch.logic.UrlTransformer;
import me.redoak.edean.pricewatch.shops.Shop;
import org.springframework.stereotype.Component;

import java.net.MalformedURLException;
import java.net.URL;
import java.util.Arrays;
import java.util.List;
import java.util.Optional;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An Transformer for creating {@link Shop#AMAZON} {@link URL}s
 */
@Component
public class OttoDEUrlTransformer implements UrlTransformer {

    @Override
    public URL apply(String url) {
        if (url == null || !this.appliesFor(url))
            throw new TransformationException("Given URL '" + url + "' does not conform to OttoDEUrlTransformer.");

        Optional<String> opt = findId(url);
        String id = opt.orElseThrow(() -> new RuntimeException("could not find product id in url " + url));
        try {
            return new URL("https://otto.de/p/" + id);
        } catch (MalformedURLException e) {
            throw new TransformationException("There was an issue creating the URL. Found id: " + id, e);
        }
    }

    private Optional<String> findId(String url) {
        String captureGroupName = "id";
        List<Matcher> matchers = Arrays.asList(
                Pattern.compile("p/.*\\-(?<" + captureGroupName + ">[0-9]+)\\/").matcher(url + "/"),
                Pattern.compile("p/(?<" + captureGroupName + ">[0-9]+)\\/").matcher(url + "/")
        );
        return matchers.stream().filter(Matcher::find).findFirst().map(m -> m.group(captureGroupName));
    }

    @Override
    public Shop transformsFor() {
        return Shop.OTTODE;
    }

    @Override
    public boolean appliesFor(String url) {
        return url != null && url.toLowerCase().contains("otto.de");
    }
}
