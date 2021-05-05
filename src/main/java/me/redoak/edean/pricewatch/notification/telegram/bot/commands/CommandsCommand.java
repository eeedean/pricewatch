package me.redoak.edean.pricewatch.notification.telegram.bot.commands;

import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@ConditionalOnProperty(name = "me.redoak.edean.pricewatch.telegram.enabled", havingValue = "true")
@Component
public class CommandsCommand extends AbstractCommand {

    private final List<PricewatchTelegramBotCommand> commandList;

    public CommandsCommand(List<PricewatchTelegramBotCommand> commandList) {
        super();
        this.commandList = commandList;
    }

    @Override
    protected String execute(Message message, List<Argument> argumentList) {

        //TODO refactor this method

        StringBuilder sb = new StringBuilder("Salut! Ich zeige dir eine Übersicht aller Befehle in Kurzform an.");

        commandList.forEach(c -> {
            sb.append("\n\n");
            if (c instanceof AuthCommand) {
                sb.append(((AuthCommand) c).getArgumentList().get(0).getValue());
                sb.append(" - ");
                sb.append(((AuthCommand) c).getArgumentList().get(0).getName());
            } else if (c instanceof RegisterCommand) {
                sb.append(((RegisterCommand) c).getArgumentList().get(0).getValue());
                sb.append(" - ");
                sb.append(((RegisterCommand) c).getArgumentList().get(0).getName());
            } else if (c instanceof StartCommand) {
                sb.append(((StartCommand) c).getArgumentList().get(0).getValue());
                sb.append(" - ");
                sb.append(((StartCommand) c).getArgumentList().get(0).getName());
            } else if (c instanceof SubscribeCommand) {
                sb.append(((SubscribeCommand) c).getArgumentList().get(0).getValue());
                sb.append(" - ");
                sb.append(((SubscribeCommand) c).getArgumentList().get(0).getName());
            } else if (c instanceof SubscriptionsCommand) {
                sb.append(((SubscriptionsCommand) c).getArgumentList().get(0).getValue());
                sb.append(" - ");
                sb.append(((SubscriptionsCommand) c).getArgumentList().get(0).getName());
            } else if (c instanceof UnregisterCommand) {
                sb.append(((UnregisterCommand) c).getArgumentList().get(0).getValue());
                sb.append(" - ");
                sb.append(((UnregisterCommand) c).getArgumentList().get(0).getName());
            } else if (c instanceof UnsubscribeCommand) {
                sb.append(((UnsubscribeCommand) c).getArgumentList().get(0).getValue());
                sb.append(" - ");
                sb.append(((UnsubscribeCommand) c).getArgumentList().get(0).getName());
            }
        });
        sb.append("/help");
        sb.append(" - ");
        sb.append("Hilfe anzeigen");

        return sb.toString();
    }

    @Override
    protected void initializeArguments(List<Argument> argumentList) {
        argumentList.add(Argument.builder()
                .name("Commands")
                .description("Ich zeige Dir, welche Befehle vorhanden sind!")
                .value("/commands")
                .build());
    }
}
