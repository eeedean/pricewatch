package me.redoak.edean.pricewatch.shops.home24;

import lombok.AccessLevel;
import lombok.Getter;
import lombok.Setter;
import lombok.extern.slf4j.Slf4j;
import me.redoak.edean.pricewatch.logic.WebClient;
import me.redoak.edean.pricewatch.logic.update.ProductUpdater;
import me.redoak.edean.pricewatch.products.TrackedProduct;
import me.redoak.edean.pricewatch.products.TrackedProductRepository;
import me.redoak.edean.pricewatch.shops.Shop;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import java.math.BigDecimal;
import java.text.NumberFormat;
import java.text.ParseException;
import java.time.ZoneId;
import java.time.ZonedDateTime;
import java.util.Locale;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import static java.util.Objects.isNull;

/**
 * {@link ProductUpdater} for updating {@link TrackedProduct}s from {@link Shop#HOME24}.
 */
@Slf4j
@Getter(AccessLevel.PACKAGE)
@Setter(AccessLevel.PACKAGE)
@Component
public class Home24ProductUpdater implements ProductUpdater {

    @Autowired
    private WebClient webClient;

    @Autowired
    private TrackedProductRepository repo;

    @Override
    public Shop getShop() {
        return Shop.HOME24;
    }

    @Override
    public boolean update(TrackedProduct trackedProduct) {
        log.debug("updating product: {}: {}", trackedProduct.getId(), trackedProduct.getName());
        String content = webClient.getContent(trackedProduct.getUrl());
        Document doc = Jsoup.parse(content);

        boolean priceChanged = updatePrice(doc, trackedProduct);
        boolean titleChanged = updateTitle(doc, trackedProduct);
        boolean didChange = priceChanged || titleChanged;

        if (didChange) {
            trackedProduct.setUpdatedAt(ZonedDateTime.now(ZoneId.of("UTC")));
            repo.save(trackedProduct);
            log.debug("product changed and saved!");
            return true;
        } else {
            log.debug("product did not change.");
            return false;
        }
    }

    private boolean updateTitle(Document doc, TrackedProduct trackedProduct) {
        Element titleElement = doc.getElementsByAttributeValue("data-testid", "article-name").first();
        Element variantElement = doc.getElementsByAttributeValue("data-testid", "article-variant-name").first();
        if (titleElement != null) {
            String newName = titleElement.text().trim() + " (" + variantElement.text().trim() + ")";
            String oldName = trackedProduct.getName();
            if (!newName.equals(oldName)) {
                trackedProduct.setName(newName);
                log.debug("title did change. New title: {}",trackedProduct.getName());
                return true;
            } else {
                log.debug("title did not change");
            }
        } else {
            log.warn("did not find title titleElement! Product ID: {}", trackedProduct.getId());
        }
        return false;
    }

    private boolean updatePrice(Document doc, TrackedProduct trackedProduct) {
        BigDecimal newPrice = getPrice(doc);
        BigDecimal oldPrice = trackedProduct.getPrice();

        // If the old and the new price are both null, the price did not change.
        // Probably the product is not available since a while.
        boolean bothNull = (isNull(newPrice) && isNull(oldPrice));
        boolean didChange = !bothNull && !isNull(newPrice) && !newPrice.equals(oldPrice);

        if (didChange) {
            trackedProduct.setOldPrice(oldPrice);
            trackedProduct.setPrice(newPrice);
            log.debug("price did change. New price: {}", trackedProduct.getPrice());
            return true;
        } else {
            log.debug("price did not change.");
            return false;
        }
    }

    private BigDecimal getPrice(Document doc) {
        Element element = doc.getElementsByAttributeValue("data-testid", "current-price").first();
        if (element != null) {
            Matcher matcher = Pattern.compile("([0-9,.]+)").matcher(element.text());
            matcher.find();
            String priceString = matcher.group();

            try {
                double val = NumberFormat.getInstance(Locale.GERMANY).parse(priceString).doubleValue();
                return BigDecimal.valueOf((long)(val * 100), 2);
            } catch (ParseException e) {
                throw new RuntimeException("Number of unsupported format: " + priceString, e);
            }
        } else {
            log.warn("did not find price element!");
            return null;
        }
    }
}
