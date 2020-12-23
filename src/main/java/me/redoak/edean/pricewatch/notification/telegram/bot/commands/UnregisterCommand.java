package me.redoak.edean.pricewatch.notification.telegram.bot.commands;

import me.redoak.edean.pricewatch.subscribers.Subscriber;
import me.redoak.edean.pricewatch.subscribers.SubscriberService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class UnregisterCommand  implements  PricewatchTelegramBotCommand{

    private final SubscriberService subscriberService;

    public UnregisterCommand(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @Override
    public boolean appliesTo(Message message) {
        return message.getText().split(" ")[0].toLowerCase().startsWith("/unregister");
    }

    @Override
    public String execute(Message message) {
        var s = message.getText().split(" ");
        if (s.length != 3)
            return "Falsche Menge an Argumenten! `/unregister »user« »password«`";
        try {
            Subscriber subscriberRequest = subscriberService.auth(s[1], s[2]);
            subscriberService.unregister(subscriberRequest);
        } catch (SecurityException e) {
            return "Da stimmte was an den Daten nicht!";
        }
        return "OK byyyeee!";
    }
}
