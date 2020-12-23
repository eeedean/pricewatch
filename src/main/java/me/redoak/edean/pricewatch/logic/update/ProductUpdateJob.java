package me.redoak.edean.pricewatch.logic.update;

import me.redoak.edean.pricewatch.logic.Shop;
import me.redoak.edean.pricewatch.products.TrackedProductRepository;
import me.redoak.edean.pricewatch.products.TrackedProduct;
import lombok.extern.slf4j.Slf4j;
import org.quartz.DisallowConcurrentExecution;
import org.quartz.JobExecutionContext;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.scheduling.quartz.QuartzJobBean;

import javax.transaction.Transactional;
import java.util.List;

/**
 * Job updating all the {@link TrackedProduct}s by using the {@link ProductUpdater}s per {@link Shop}.
 */
@Slf4j
@DisallowConcurrentExecution
public class ProductUpdateJob extends QuartzJobBean {

    @Autowired
    private TrackedProductRepository repo;

    @Autowired
    private List<ProductUpdater> updaters;

    @Autowired
    private List<Notifier> notifiers;

    @Transactional
    @Override
    protected void executeInternal(JobExecutionContext context) {
        log.info("Starting product updates.");
        updaters.parallelStream()
                .forEach(updater -> {
                    log.debug("Updating products for {} with {}.", updater.getShop(), updater.toString());
                    repo.findAllByShopEagerSubscribers(updater.getShop()).parallelStream()
                            .filter(updater::update) // ProductUpdater.update delivers true, if there was a price difference
                            .forEach(product ->
                                    product.getSubscribers().forEach(subscriber ->
                                            notifiers.forEach(n -> n.inform(product, subscriber))));
                });
        log.info("Finished product updates.");
    }
}
