package me.redoak.edean.pricewatch.logic;

import me.redoak.edean.pricewatch.products.TrackedProduct;
import me.redoak.edean.pricewatch.shops.Shop;

import java.net.URL;

/**
 * Interface for saving products as {@link TrackedProduct}s.
 */
public interface ProductSaver {

    /**
     * @return The {@link Shop} this implementation saves {@link TrackedProduct}s for.
     */
    Shop savesFor();

    /**
     * Saves a {@link TrackedProduct} for given {@link URL}, if not existing yet.
     *
     * @param url The URL of the product to be tracked.
     * @return The saved TrackedProduct.
     */
    TrackedProduct save(URL url);
}
