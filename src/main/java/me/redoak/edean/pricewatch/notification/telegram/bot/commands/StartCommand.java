package me.redoak.edean.pricewatch.notification.telegram.bot.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@Component
public class StartCommand extends AbstractCommand {

    public StartCommand() {
        super();
    }

    @Override
    protected String execute(Message message, List<Argument> argumentList) {
        return "Salut! Ich unterstütze Dich dabei, die Preise von Online-Shops im Auge zu beinhalten. " +
                "Rufe für den Hilfsdialog /help auf. \nFür einen Quickstart reichen die Befehle " +
                "/register, /auth, /subscribe und /unsubscribe.";
    }

    @Override
    protected void initializeArguments(List<Argument> argumentList) {
        argumentList.add(Argument.builder()
                .name("Begrüßung")
                .description("Ganz ehrlich? Dieser Befehl begrüßt Dich nur. ")
                .value("/start")
                .build());
    }
}
