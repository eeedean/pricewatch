package me.redoak.edean.pricewatch.products;

import me.redoak.edean.pricewatch.logic.Shop;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.net.URL;
import java.util.List;
import java.util.Optional;

/**
 * CRUD-Repository for working with {@link TrackedProduct}s.
 */
public interface TrackedProductRepository extends JpaRepository<TrackedProduct, String> {

    /**
     * Loads all products with given shop from database.
     *
     * @param shop The shop to be searched for.
     * @return all tracked products from given shop.
     */
    @Query("select p from TrackedProduct p left join fetch p.subscribers where p.shop = :shop")
    List<TrackedProduct> findAllByShopEagerSubscribers(@Param("shop") Shop shop);

    /**
     * @return If existing, a filled {@link Optional<TrackedProduct>} with given {@link URL}.
     */
    @Query("select p from TrackedProduct p left join fetch p.subscribers where p.url = :url")
    Optional<TrackedProduct> findByUrl(@Param("url") URL url);
}
