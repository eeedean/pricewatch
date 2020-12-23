package me.redoak.edean.pricewatch.notification.telegram.bot;

import me.redoak.edean.pricewatch.notification.telegram.bot.commands.PricewatchTelegramBotCommand;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.bots.TelegramLongPollingBot;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.api.objects.Update;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

import java.util.List;

@Slf4j
@ConditionalOnProperty(name = "me.redoak.edean.pricewatch.telegram.enabled", havingValue = "true")
@Component
public class PricewatchTelegramBot extends TelegramLongPollingBot {

    private final List<PricewatchTelegramBotCommand> commands;

    public PricewatchTelegramBot(List<PricewatchTelegramBotCommand> commands) {
        this.commands = commands;
    }

    @Value("${me.redoak.edean.pricewatch.telegram.bot.username}")
    private String botUsername;

    @Value("${me.redoak.edean.pricewatch.telegram.bot.token}")
    private String botToken;

    @Override
    public void onUpdateReceived(Update update) {
        // We check if the update has a message and the message has text
        if (update.hasMessage() && update.getMessage().hasText()) {
            var message = new SendMessage(); // Create a SendMessage object with mandatory fields
            message.setChatId(String.valueOf(update.getMessage().getChatId()));
            var input = update.getMessage();
            var optCmd = commands.stream().filter(command -> command.appliesTo(input)).findFirst();
            if (optCmd.isPresent()) {
                message.setText(optCmd.get().execute(input));
            } else {
                message.setText(update.getMessage().getText() + "? Versteh ich nich. ");
            }
            try {
                execute(message); // Call method to send the message
            } catch (TelegramApiException e) {
                log.error("Error replying", e);
            }
        }
    }

    @Override
    public String getBotUsername() {
        return this.botUsername;
    }

    @Override
    public String getBotToken() {
        return this.botToken;
    }
}
