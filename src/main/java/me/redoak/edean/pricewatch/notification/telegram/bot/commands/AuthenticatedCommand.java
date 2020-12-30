package me.redoak.edean.pricewatch.notification.telegram.bot.commands;

import me.redoak.edean.pricewatch.subscribers.Subscriber;
import me.redoak.edean.pricewatch.subscribers.SubscriberRepository;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;
import java.util.Optional;

public abstract class AuthenticatedCommand extends AbstractCommand {

    private final SubscriberRepository subscriberRepository;

    protected AuthenticatedCommand(SubscriberRepository subscriberRepository) {
        super();
        this.subscriberRepository = subscriberRepository;
    }

    @Override
    public String execute(Message message, List<Argument> argumentList) {
        Optional<Subscriber> subscriberOpt = subscriberRepository.findByTelegramChatId(String.valueOf(message.getChatId()));
        if (!subscriberOpt.isPresent()) {
            return "Authentifiziere Dich zuerst mit `/auth`. Wenn Du noch nicht registriert bist, verwende `/register`.";
        }
        Subscriber subscriber = subscriberOpt.get();
        return this.execute(message, subscriber, argumentList);
    }

    protected abstract String execute(Message message, Subscriber subscriber, List<Argument> argumentList);
}
