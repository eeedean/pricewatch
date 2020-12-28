package me.redoak.edean.pricewatch.logic.update;

import me.redoak.edean.pricewatch.logic.Shop;
import me.redoak.edean.pricewatch.products.TrackedProductRepository;
import me.redoak.edean.pricewatch.products.TrackedProduct;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.transaction.Transactional;
import java.math.BigDecimal;
import java.util.List;

/**
 * Job updating all the {@link TrackedProduct}s by using the {@link ProductUpdater}s per {@link Shop}.
 */
@Slf4j
@DisallowConcurrentExecution
public class ProductUpdateJob extends QuartzJobBean {

    @Value("${me.redoak.edean.pricewatch.min-change-percent}")
    private double minPriceChangePercentage;

    private final TrackedProductRepository repo;

    private final List<ProductUpdater> updaters;

    private final List<Notifier> notifiers;

    public ProductUpdateJob(TrackedProductRepository repo, List<ProductUpdater> updaters, List<Notifier> notifiers) {
        this.repo = repo;
        this.updaters = updaters;
        this.notifiers = notifiers;
    }

    @Transactional
    @Override
    protected void executeInternal(JobExecutionContext context) {
        log.info("Starting product updates.");
        updaters.parallelStream()
                .forEach(updater -> {
                    log.debug("Updating products for {} with {}.", updater.getShop(), updater.toString());
                    repo.findAllByShopEagerSubscribers(updater.getShop()).parallelStream()
                            .filter(updater::update) // ProductUpdater.update delivers true, if there was a price difference
                            .filter(product -> product.getPrice() == null ||
                                    (product.getOldPrice() != null && product.getPrice() != null &&
                                    product.getPrice()
                                            .divide(product.getOldPrice())
                                            .subtract(BigDecimal.ONE)
                                            .multiply(BigDecimal.valueOf(100))
                                            .abs()
                                            .compareTo(BigDecimal.valueOf(minPriceChangePercentage)) > 0 ))
                            .forEach(product ->
                                    product.getSubscribers().forEach(subscriber ->
                                            notifiers.forEach(n -> n.inform(product, subscriber))));
                });
        log.info("Finished product updates.");
    }
}
