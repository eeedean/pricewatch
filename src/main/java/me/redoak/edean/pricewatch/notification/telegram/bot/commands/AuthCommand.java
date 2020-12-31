package me.redoak.edean.pricewatch.notification.telegram.bot.commands;

import me.redoak.edean.pricewatch.subscribers.SubscriberRepository;
import me.redoak.edean.pricewatch.subscribers.SubscriberService;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@ConditionalOnProperty(name = "me.redoak.edean.pricewatch.telegram.enabled", havingValue = "true")
@Component
public class AuthCommand extends AbstractCommand {

    private static final int USER_INDEX = 1;
    private static final int PASSWORD_INDEX = 2;

    private final SubscriberService subscriberService;
    private final SubscriberRepository subscriberRepository;

    public AuthCommand(SubscriberService subscriberService, SubscriberRepository subscriberRepository) {
        super();
        this.subscriberService = subscriberService;
        this.subscriberRepository = subscriberRepository;
    }

    @Override
    protected String execute(Message message, List<Argument> argumentList) {
        try {
            String username = argumentList.get(USER_INDEX).getValue();
            String password = argumentList.get(PASSWORD_INDEX).getValue();
            var subscriber = subscriberService.auth(username, password);
            subscriber.setTelegramChatId(String.valueOf(message.getChatId()));
            subscriberRepository.save(subscriber);
        } catch (SecurityException e) {
            return "Die Login-Daten stimmen nicht!";
        }
        return "Dieser Chat ist nun an Dein Konto gebunden.";
    }

    @Override
    protected void initializeArguments(List<Argument> argumentList) {
        argumentList.add(Argument.builder()
                .name("Authentifizierung")
                .description("Authentifiziere Dich, damit dieser Chat mit Deinem Konto verbunden ist. " +
                        "Das musst Du tun, damit Du mich verwenden kannst. Ohne Authentifizierung kann " +
                        "ich Deine Abonnements nicht von denen anderer Benutzer unterscheiden.")
                .value("/auth")
                .build());
        argumentList.add(Argument.builder()
                .name("Nutzname")
                .description("Dein Nutzername")
                .build());
        argumentList.add(Argument.builder()
                .name("Passwort")
                .description("Dein Passwort")
                .build());
    }
}
