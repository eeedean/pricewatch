package me.redoak.edean.pricewatch.notification.telegram.bot.commands;

import lombok.Builder;
import lombok.Data;
import lombok.extern.slf4j.Slf4j;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.ArrayList;
import java.util.List;

@Slf4j
public abstract class AbstractCommand implements PricewatchTelegramBotCommand {

    private final List<Argument> argumentList = new ArrayList<>();

    public AbstractCommand() {
        this.initializeArguments(this.argumentList);
        log.info("Initialized Command '{}' for {}", this.argumentList.get(0).getValue(), this.argumentList.get(0).getName());
    }

    @Override
    public boolean appliesTo(Message message) {
        String[] parts = message.getText().split(" ");
        Argument firstArg = this.argumentList.stream().findFirst().orElseThrow(this::notInitializedCorrectly);
        return parts.length > 0 && parts[0].toLowerCase().startsWith(firstArg.getValue());
    }

    private RuntimeException notInitializedCorrectly() {
        return new RuntimeException(this.getClass().getName() + " is not initialized correctly!");
    }

    @Override
    public String execute(Message message) {
        String[] parts = message.getText().split(" ");
        if (parts.length != this.argumentList.size())
            return this.wrongNumberArgs();
        for (int i = 0; i < this.argumentList.size(); i++)
            this.argumentList.get(i).setValue(parts[i]);
        return this.execute(message, this.argumentList);
    }

    private String wrongNumberArgs() {
        StringBuilder sb = new StringBuilder("Falsche Menge an Argumenten!\n\n");
        sb.append(this.explainArguments());
        return sb.toString();
    }

    private String explainArguments() {
        StringBuilder sb = new StringBuilder();
        sb.append(this.argumentList.get(0).getValue());
        for (int i = 1; i < this.argumentList.size(); i++)
            sb.append(" »").append(this.argumentList.get(i).getName()).append("«");
        sb.append('\n');
        for (int i = 1; i < this.argumentList.size(); i++)
            sb.append(" - ")
                    .append(this.argumentList.get(i).getName())
                    .append(": ")
                    .append(this.argumentList.get(i).getDescription())
                    .append('\n');

        return sb.toString();
    }

    @Override
    public String getHelpText() {
        return new StringBuilder().append(this.argumentList.get(0).getName())
                .append('\n')
                .append(this.argumentList.get(0).getDescription())
                .append('\n')
                .append(this.explainArguments())
                .toString();
    }

    @Override
    public String getDescription() {
        Argument call = this.argumentList.get(0);
        return call.getValue() + " - " + call.getName() + ": " + call.getDescription();
    }

    protected abstract void initializeArguments(List<Argument> argumentList);

    protected abstract String execute(Message message, List<Argument> argumentList);

    @Data
    @Builder
    public static class Argument {
        private final String name;
        private final String description;
        private String value;
    }

    public List<Argument> getArgumentList(){
        return this.argumentList;
    }
}
