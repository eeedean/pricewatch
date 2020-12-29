package me.redoak.edean.pricewatch.logic;

import me.redoak.edean.pricewatch.products.TrackedProduct;
import me.redoak.edean.pricewatch.shops.Shop;

import java.net.URL;

/**
 * An interface for providing transformation of {@link String}s in {@link URL}s to be used in {@link TrackedProduct}s.
 */
public interface UrlTransformer {

    /**
     * Transforms a {@link String} to a {@link URL} to be used in a {@link TrackedProduct}.
     *
     * @param url the url string to be transformed.
     * @return an {@link URL} for the given string.
     */
    URL apply(String url);

    /**
     * @return The {@link Shop} the transformer may be applied for.
     */
    Shop transformsFor();

    /**
     * Checks, if the transformer works for given URL string.
     * @param url URL as a string to be checked.
     * @return <code>true</code> if it applies, <code>false</code> if not.
     */
    boolean appliesFor(String url);
}
