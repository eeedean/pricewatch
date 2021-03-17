package me.redoak.edean.pricewatch.notification.telegram.bot.commands;

import lombok.extern.slf4j.Slf4j;
import me.redoak.edean.pricewatch.products.ProductRequest;
import me.redoak.edean.pricewatch.products.TrackedProductService;
import me.redoak.edean.pricewatch.subscribers.Subscriber;
import me.redoak.edean.pricewatch.subscribers.SubscriberRepository;
import org.quartz.SchedulerException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@ConditionalOnProperty(name = "me.redoak.edean.pricewatch.telegram.enabled", havingValue = "true")
@Component
public class SubscribeCommand extends AuthenticatedCommand {

    private static final int URL_INDEX = 1;

    private final TrackedProductService trackedProductService;

    public SubscribeCommand(SubscriberRepository subscriberRepository, TrackedProductService trackedProductService) {
        super(subscriberRepository);
        this.trackedProductService = trackedProductService;
    }

    @Override
    protected String execute(Message message, Subscriber subscriber, List<Argument> argumentList) {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setUrl(argumentList.get(URL_INDEX).getValue());

        trackedProductService.subscribe(productRequest, subscriber);

        try {
            trackedProductService.triggerUpdate();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }

        return "Erledigt!";
    }

    @Override
    protected void initializeArguments(List<Argument> argumentList) {
        argumentList.add(Argument.builder()
                .name("Abonnieren")
                .description("Abonniere einen Artikel anhand seiner URL, um Benachrichtigungen zu erhalten, " +
                        "wenn sich der Preis ändert.")
                .value("/subscribe")
                .build());
        argumentList.add(Argument.builder()
                .name("URL")
                .description("Die URL des Produkts, für das Du keine Benachrichtigungen mehr erhalten möchtest.")
                .build());
    }
}
