package me.redoak.edean.pricewatch.notification.telegram.bot.commands;

import me.redoak.edean.pricewatch.subscribers.SubscriberRepository;
import me.redoak.edean.pricewatch.subscribers.SubscriberService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class AuthCommand  implements PricewatchTelegramBotCommand {

    private final SubscriberService subscriberService;
    private final SubscriberRepository subscriberRepository;

    public AuthCommand(SubscriberService subscriberService, SubscriberRepository subscriberRepository) {
        this.subscriberService = subscriberService;
        this.subscriberRepository = subscriberRepository;
    }

    @Override
    public boolean appliesTo(Message message) {
        return message.getText().split(" ")[0].toLowerCase().startsWith("/auth");
    }

    @Override
    public String execute(Message message) {
        var s = message.getText().split(" ");
        if (s.length != 3) {
            return "Falsche Menge an Argumenten! `/auth »user« »password«`";
        } else {
            try {
                var subscriber = subscriberService.auth(s[1], s[2]);
                subscriber.setTelegramChatId(String.valueOf(message.getChatId()));
                subscriberRepository.save(subscriber);
            } catch (SecurityException e) {
                return "Lügner!";
            }
        }
        return "Dieser Chat ist nun an Dein Konto gebunden.";
    }
}
