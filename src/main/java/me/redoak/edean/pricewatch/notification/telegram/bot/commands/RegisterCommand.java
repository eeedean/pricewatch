package me.redoak.edean.pricewatch.notification.telegram.bot.commands;

import me.redoak.edean.pricewatch.subscribers.Subscriber;
import me.redoak.edean.pricewatch.subscribers.SubscriberService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class RegisterCommand implements PricewatchTelegramBotCommand {

    private final SubscriberService subscriberService;

    public RegisterCommand(SubscriberService subscriberService) {
        this.subscriberService = subscriberService;
    }

    @Override
    public boolean appliesTo(Message message) {
        return message.getText().split(" ")[0].toLowerCase().startsWith("/register");
    }

    @Override
    public String execute(Message message) {
        var s = message.getText().split(" ");
        if (s.length != 3)
            return "Falsche Menge an Argumenten! `/register »user« »password«`";
        Subscriber subscriberRequest = Subscriber.builder().name(s[1]).build();
        try {
            subscriberService.register(subscriberRequest, s[2]);
        } catch (SecurityException e) {
            return "Name schon belegt!";
        }
        return "Super, Du kannst Dich jetzt mit `/auth` authentifizieren!";
    }
}
