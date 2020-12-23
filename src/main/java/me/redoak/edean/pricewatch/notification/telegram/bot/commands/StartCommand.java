package me.redoak.edean.pricewatch.notification.telegram.bot.commands;

import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

@Component
public class StartCommand implements PricewatchTelegramBotCommand {

    @Override
    public boolean appliesTo(Message message) {
        return message.getText().split(" ")[0].toLowerCase().startsWith("/start");
    }

    @Override
    public String execute(Message message) {
        return "Salut!";
    }
}
