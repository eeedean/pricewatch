package me.redoak.edean.pricewatch.notification.telegram;

import me.redoak.edean.pricewatch.notification.telegram.bot.PricewatchTelegramBot;
import lombok.extern.slf4j.Slf4j;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.telegram.telegrambots.meta.TelegramBotsApi;
import org.telegram.telegrambots.meta.exceptions.TelegramApiException;
import org.telegram.telegrambots.updatesreceivers.DefaultBotSession;

@Slf4j
@Configuration
@ConditionalOnProperty(name = "me.redoak.edean.pricewatch.telegram.enabled", havingValue = "true")
public class TelegramConfiguration {

    private final TelegramBotsApi telegramBotsApi;

    public TelegramConfiguration(PricewatchTelegramBot pricewatchTelegramBot) throws TelegramApiException {
        this.telegramBotsApi = new TelegramBotsApi(DefaultBotSession.class);
        try {
            this.telegramBotsApi.registerBot(pricewatchTelegramBot);
        } catch (TelegramApiException e) {
            log.error("Something went wrong registering the Notification Bot.", e);
        }
    }

    @Bean
    protected TelegramBotsApi telegramBotsApi() {
        return this.telegramBotsApi;
    }
}
