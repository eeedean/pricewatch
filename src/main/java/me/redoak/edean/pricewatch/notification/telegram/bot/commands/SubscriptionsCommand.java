package me.redoak.edean.pricewatch.notification.telegram.bot.commands;

import me.redoak.edean.pricewatch.products.TrackedProduct;
import me.redoak.edean.pricewatch.subscribers.Subscriber;
import me.redoak.edean.pricewatch.subscribers.SubscriberRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class SubscriptionsCommand extends AuthenticatedCommand {

    public SubscriptionsCommand(SubscriberRepository subscriberRepository) {
        super(subscriberRepository);
    }

    @Override
    protected void initializeArguments(List<Argument> argumentList) {
        argumentList.add(Argument.builder()
                .name("Abonnements")
                .description("Gebe alle Deine Abonnements aus.")
                .value("/subscriptions")
                .build());
    }

    @Override
    protected String execute(Message message, Subscriber subscriber, List<Argument> argumentList) {
        StringBuilder sb = new StringBuilder();
        int counter = 0;
        for (TrackedProduct p : subscriber.getTrackedProducts()) {
            if (p.getName() != null) {
                counter++;
                String s = String.join("\n",
                        counter + ". " + getTitleString(p),
                        getPriceString(p)) + "\n\n";
                sb.append(s);
            }
        }
        return sb.toString();
    }

    private String getTitleString(me.redoak.edean.pricewatch.products.TrackedProduct product) {
        return String.format("<a href=\"%s\">%s</a>", product.getUrl().toExternalForm(), product.getName());
    }

    private String getPriceString(me.redoak.edean.pricewatch.products.TrackedProduct product) {
        return product.getPrice() != null ?
                String.format("%s €", product.getPrice()) :
                "Aktuell kein Preis verfügbar";
    }
}
