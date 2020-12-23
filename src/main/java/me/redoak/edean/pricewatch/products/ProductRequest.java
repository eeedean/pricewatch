package me.redoak.edean.pricewatch.products;

import lombok.Data;

import javax.validation.constraints.NotNull;

/**
 * A request object for tracking a new product.
 */
@Data
public class ProductRequest {

    @NotNull
    private String url;
}
