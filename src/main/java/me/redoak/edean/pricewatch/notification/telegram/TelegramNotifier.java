package me.redoak.edean.pricewatch.notification.telegram;

import me.redoak.edean.pricewatch.notification.telegram.bot.PricewatchTelegramBot;
import me.redoak.edean.pricewatch.products.TrackedProduct;
import me.redoak.edean.pricewatch.subscribers.Subscriber;
import me.redoak.edean.pricewatch.logic.update.Notifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.methods.ParseMode;
import org.telegram.telegrambots.meta.api.methods.send.SendMessage;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;

@Slf4j
@ConditionalOnProperty(name = "me.redoak.edean.pricewatch.telegram.enabled", havingValue = "true")
@Component
public class TelegramNotifier implements Notifier {

    private final PricewatchTelegramBot pricewatchTelegramBot;

    public TelegramNotifier(PricewatchTelegramBot pricewatchTelegramBot) {
        this.pricewatchTelegramBot = pricewatchTelegramBot;
    }

    @Override
    public boolean inform(TrackedProduct trackedProduct, Subscriber subscriber) {
        var message = new SendMessage();
        message.setChatId(subscriber.getTelegramChatId());
        String text = String.join("\n",
                "See this, friend: ",
                productDetails(trackedProduct),
                "GL HF");
        message.setText(text);
        message.setParseMode(ParseMode.HTML);
        try {
            log.debug("Sending Telegram message: {}", message);
            pricewatchTelegramBot.execute(message);
            return true;
        } catch (TelegramApiException e) {
            log.error("Failed to send message to ChatId {}", subscriber.getTelegramChatId(), e);
            return false;
        }
    }

    private String productDetails(TrackedProduct trackedProduct) {
        return String.join("\n",
                String.format("<b>%s</b>", trackedProduct.getName()),
                String.format("💸 %s € ➡️ %s €", trackedProduct.getOldPrice(), trackedProduct.getPrice()),
                String.format("🌍<a href=\"%s\">Hier kriegst Du's!</a>",trackedProduct.getUrl())
        );
    }
}
