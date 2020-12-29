package me.redoak.edean.pricewatch.notification.telegram.bot.commands;

import me.redoak.edean.pricewatch.subscribers.Subscriber;
import me.redoak.edean.pricewatch.subscribers.SubscriberRepository;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.Optional;

public abstract class AuthenticatedCommand implements PricewatchTelegramBotCommand {

    private final SubscriberRepository subscriberRepository;

    protected AuthenticatedCommand(SubscriberRepository subscriberRepository) {
        this.subscriberRepository = subscriberRepository;
    }

    @Override
    public String execute(Message message) {
        Optional<Subscriber> subscriberOpt = subscriberRepository.findByTelegramChatId(String.valueOf(message.getChatId()));
        if (!subscriberOpt.isPresent()) {
            return "Authentifiziere Dich zuerst mit `/auth`. Wenn Du noch nicht registriert bist, verwende `/register`.";
        }
        Subscriber subscriber = subscriberOpt.get();
        return this.executeInternal(message, subscriber);
    }

    protected abstract String executeInternal(Message message, Subscriber subscriber);
}
