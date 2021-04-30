package me.redoak.edean.pricewatch.shops.ottode;

import lombok.extern.slf4j.Slf4j;
import me.redoak.edean.pricewatch.logic.ProductSaver;
import me.redoak.edean.pricewatch.products.TrackedProduct;
import me.redoak.edean.pricewatch.products.TrackedProductRepository;
import me.redoak.edean.pricewatch.shops.Shop;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.net.URL;

/**
 * A {@link ProductSaver} for products from {@link Shop#AMAZON}.
 */
@Slf4j
@Component
public class OttoDEProductSaver implements ProductSaver {

    @Autowired
    private TrackedProductRepository repo;

    @Override
    public Shop savesFor() {
        return Shop.OTTODE;
    }

    @Override
    public TrackedProduct save(URL url) {
        log.debug("Saving product with URL {} if not exists.", url.toString());
        return repo.findByUrl(url)
                .orElseGet(() -> {
                    log.debug("Product did not exist. Saving new TrackedProduct with URL {}.", url.toString());
                    return repo.save(TrackedProduct.builder()
                            .shop(Shop.OTTODE)
                            .url(url)
                            .build());
                });
    }
}
