package me.redoak.edean.pricewatch.notification.telegram.bot.commands;

import me.redoak.edean.pricewatch.products.ProductRequest;
import me.redoak.edean.pricewatch.products.TrackedProductService;
import me.redoak.edean.pricewatch.subscribers.Subscriber;
import me.redoak.edean.pricewatch.subscribers.SubscriberRepository;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Optional;

@ConditionalOnProperty(name = "me.redoak.edean.pricewatch.telegram.enabled", havingValue = "true")
@Component
public class UnsubscribeCommand extends AuthenticatedCommand {

    private static final int URL_INDEX = 1;

    private final TrackedProductService trackedProductService;

    public UnsubscribeCommand(SubscriberRepository subscriberRepository, TrackedProductService trackedProductService) {
        super(subscriberRepository);
        this.trackedProductService = trackedProductService;
    }

    @Override
    protected String execute(Message message, Subscriber subscriber, List<Argument> argumentList) {
        ProductRequest productRequest = new ProductRequest();
        productRequest.setUrl(argumentList.get(URL_INDEX).getValue());

        trackedProductService.unsubscribe(productRequest, subscriber);

        return "Erledigt!";
    }

    @Override
    protected void initializeArguments(List<Argument> argumentList) {
        argumentList.add(Argument.builder()
                .name("De-abonnieren")
                .description("Beende das Abonnement zu einem Produkt, damit Du keine Benachrichtigungen dazu mehr erhältst.")
                .value("/unsubscribe")
                .build());
        argumentList.add(Argument.builder()
                .name("URL")
                .description("Die URL des Produkts, für das Du keine Benachrichtigungen mehr erhalten möchtest.")
                .build());
    }
}
