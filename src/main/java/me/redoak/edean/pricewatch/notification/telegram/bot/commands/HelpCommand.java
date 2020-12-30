package me.redoak.edean.pricewatch.notification.telegram.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

public class HelpCommand extends AbstractCommand {

    private final List<PricewatchTelegramBotCommand> commandList;

    public HelpCommand(List<PricewatchTelegramBotCommand> commandList) {
        super();
        this.commandList = commandList;
    }

    @Override
    protected String execute(Message message, List<Argument> argumentList) {
        StringBuilder sb = new StringBuilder("Salut! Ich unterstütze Dich dabei, die Preise von Online-Shops im Auge zu beinhalten.\n");
        sb.append(commandList.stream()
                .map(PricewatchTelegramBotCommand::getHelpText)
                .reduce("", (s1, s2) -> s1 + "\n\n" + s2));
        return sb.toString();
    }

    @Override
    protected void initializeArguments(List<Argument> argumentList) {
        argumentList.add(Argument.builder()
                .name("Hilfe")
                .description("Ich erkläre Dir, welcher Befehl wie funktioniert!")
                .value("/help")
                .build());
    }
}
