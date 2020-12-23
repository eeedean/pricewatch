package me.redoak.edean.pricewatch.notification.telegram.bot.commands;

import org.telegram.telegrambots.meta.api.objects.Message;

public interface PricewatchTelegramBotCommand {

    boolean appliesTo(Message message);

    String execute(Message message);
}
