package me.redoak.edean.pricewatch.notification.telegram.bot.commands;

import me.redoak.edean.pricewatch.products.ProductRequest;
import me.redoak.edean.pricewatch.products.TrackedProductService;
import me.redoak.edean.pricewatch.subscribers.Subscriber;
import me.redoak.edean.pricewatch.subscribers.SubscriberRepository;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

@Component
public class UnsubscribeCommand implements PricewatchTelegramBotCommand {

    private final SubscriberRepository subscriberRepository;
    private final TrackedProductService trackedProductService;

    public UnsubscribeCommand(SubscriberRepository subscriberRepository, TrackedProductService trackedProductService) {
        this.subscriberRepository = subscriberRepository;
        this.trackedProductService = trackedProductService;
    }

    @Override
    public boolean appliesTo(Message message) {
        return message.getText().split(" ")[0].toLowerCase().startsWith("/unsubscribe");
    }

    @Override
    public String execute(Message message) {
        var s = message.getText().split(" ");
        if (s.length != 2)
            return "Falsche Menge an Argumenten! `/unsubscribe »url«`";
        Optional<Subscriber> subscriberOpt = subscriberRepository.findByTelegramChatId(String.valueOf(message.getChatId()));
        if (!subscriberOpt.isPresent()) {
            return "Authentifiziere Dich zuerst mit `/auth`. Wenn Du noch nicht registriert bist, verwende `/register`.";
        }
        Subscriber subscriber = subscriberOpt.get();
        ProductRequest productRequest = new ProductRequest();
        productRequest.setUrl(s[1]);
        trackedProductService.unsubscribe(productRequest, subscriber);

        return "Erledigt!";
    }
}
