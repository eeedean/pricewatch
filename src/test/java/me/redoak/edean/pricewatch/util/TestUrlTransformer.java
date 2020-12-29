package me.redoak.edean.pricewatch.util;

import me.redoak.edean.pricewatch.shops.Shop;
import me.redoak.edean.pricewatch.logic.UrlTransformer;

import java.net.URL;

public class TestUrlTransformer implements UrlTransformer {
    @Override
    public URL apply(String url) {
        return null;
    }

    @Override
    public Shop transformsFor() {
        return Shop.MINDFACTORY;
    }

    @Override
    public boolean appliesFor(String url) {
        return true;
    }
}
