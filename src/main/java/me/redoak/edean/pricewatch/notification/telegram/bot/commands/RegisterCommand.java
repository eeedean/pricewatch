package me.redoak.edean.pricewatch.notification.telegram.bot.commands;

import me.redoak.edean.pricewatch.subscribers.Subscriber;
import me.redoak.edean.pricewatch.subscribers.SubscriberService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class RegisterCommand extends AbstractCommand {

    private static final int USER_INDEX = 1;
    private static final int PASSWORD_INDEX = 2;

    private final SubscriberService subscriberService;

    public RegisterCommand(SubscriberService subscriberService) {
        super();
        this.subscriberService = subscriberService;
    }

    @Override
    protected String execute(Message message, List<Argument> argumentList) {
        try {
            String username = argumentList.get(USER_INDEX).getValue();
            String password = argumentList.get(PASSWORD_INDEX).getValue();

            Subscriber subscriberRequest = Subscriber.builder().name(username).build();
            subscriberService.register(subscriberRequest, password);
        } catch (SecurityException e) {
            return "Name schon belegt!";
        }
        return "Super, Du kannst Dich jetzt mit `/auth` authentifizieren!";
    }

    @Override
    protected void initializeArguments(List<Argument> argumentList) {
        argumentList.add(Argument.builder()
                .name("Konto erstellen")
                .description("Registriere Dich, damit ein Benutzerkonto für Dich erstellt wird. " +
                        "Das musst Du tun, damit Du mich verwenden kannst. Ohne Authentifizierung kann " +
                        "ich Deine Abonnements nicht von denen anderer Benutzer unterscheiden.")
                .value("/register")
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
