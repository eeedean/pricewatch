package me.redoak.edean.pricewatch.logic.update;

import me.redoak.edean.pricewatch.logic.Shop;
import me.redoak.edean.pricewatch.products.TrackedProduct;

/**
 * Interface for Beans to be used for updating {@link TrackedProduct}s of a specific {@link Shop}.
 */
public interface ProductUpdater {

    /**
     * @return The {@link Shop} this updater can process {@link TrackedProduct}s for.
     */
    Shop getShop();

    /**
     * Updates a specific {@link TrackedProduct}.
     *
     * @param trackedProduct The {@link TrackedProduct} to be updated.
     * @return <code>true</code>, if the product has a new price.
     */
    boolean update(TrackedProduct trackedProduct);
}
