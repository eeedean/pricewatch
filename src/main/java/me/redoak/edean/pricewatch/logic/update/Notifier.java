package me.redoak.edean.pricewatch.logic.update;

import me.redoak.edean.pricewatch.subscribers.Subscriber;
import me.redoak.edean.pricewatch.products.TrackedProduct;

/**
 * A {@link Notifier} informs all registered clients about a price difference of a specific {@link TrackedProduct}.
 */
public interface Notifier {

    /**
     * Notifies all registered clients about a price difference of a specific {@link TrackedProduct}.
     *
     * @param trackedProduct The {@link TrackedProduct} with a price difference.
     * @param subscriber
     * @return <code>true</code> if information worked, <code>false</code>, if it failed.
     */
    boolean inform(TrackedProduct trackedProduct, Subscriber subscriber);
}
