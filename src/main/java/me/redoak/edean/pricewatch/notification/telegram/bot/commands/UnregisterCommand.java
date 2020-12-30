package me.redoak.edean.pricewatch.notification.telegram.bot.commands;

import me.redoak.edean.pricewatch.subscribers.Subscriber;
import me.redoak.edean.pricewatch.subscribers.SubscriberService;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class UnregisterCommand extends AbstractCommand {

    private static final int USER_INDEX = 1;
    private static final int PASSWORD_INDEX = 2;

    private final SubscriberService subscriberService;

    public UnregisterCommand(SubscriberService subscriberService) {
        super();
        this.subscriberService = subscriberService;
    }

    @Override
    protected String execute(Message message, List<Argument> argumentList) {
        try {
            String username = argumentList.get(USER_INDEX).getValue();
            String password = argumentList.get(PASSWORD_INDEX).getValue();

            Subscriber subscriberRequest = subscriberService.auth(username, password);
            subscriberService.unregister(subscriberRequest);
        } catch (SecurityException e) {
            return "Die Login-Daten stimmen nicht!";
        }
        return "OK byyyeee!";
    }

    @Override
    protected void initializeArguments(List<Argument> argumentList) {
        argumentList.add(Argument.builder()
                .name("Konto löschen")
                .description("Das würde mich traurig machen, aber möchtest Du keine mehr Benachrichtigungen erhalten, " +
                        "kannst Du mit diesem Befehl Dein Nutzerkonto löschen. Deine Daten werden damit restlos gelöscht.")
                .value("/unregister")
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
