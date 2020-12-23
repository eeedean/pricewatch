package me.redoak.edean.pricewatch.notification;

import me.redoak.edean.pricewatch.subscribers.Subscriber;
import me.redoak.edean.pricewatch.products.TrackedProduct;
import me.redoak.edean.pricewatch.logic.update.Notifier;
import lombok.extern.slf4j.Slf4j;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.mail.javamail.JavaMailSender;
import org.springframework.stereotype.Component;

import javax.annotation.PostConstruct;
import javax.mail.Message;
import javax.mail.MessagingException;
import javax.mail.internet.InternetAddress;
import javax.mail.internet.MimeMessage;
import java.io.UnsupportedEncodingException;

/**
 * {@link Notifier}, that uses E-Mail to notify clients.
 */
@Slf4j
@Component
@ConditionalOnProperty(name = "me.redoak.edean.pricewatch.email.enabled", havingValue = "true")
public class EmailNotifier implements Notifier {

    private final JavaMailSender sender;

    @Value("${me.redoak.edean.pricewatch.email.from.mail}")
    private String fromMail;

    @Value("${me.redoak.edean.pricewatch.email.from.name}")
    private String fromName;

    public EmailNotifier(JavaMailSender sender) {
        this.sender = sender;
    }

    @PostConstruct
    public void postConstruct() {
        log.debug("Created E-Mail Notifier!");
    }

    @Override
    public boolean inform(TrackedProduct trackedProduct, Subscriber subscriber) {
        if (subscriber.getEmail() != null) {
            log.debug("Sending mail for product {}.", trackedProduct.getId());
            MimeMessage message = sender.createMimeMessage();
            try {
                message.addRecipients(Message.RecipientType.TO, subscriber.getEmail());
                message.setSubject("Price has changed!");
                String text = String.format(String.join("\n",
                        String.format("A product you follow has changed its price at %s.", trackedProduct.getUpdatedAt()),
                        "See this, friend: ",
                        productDetails(trackedProduct),
                        "GL HF"));
                message.setText(text);
                message.setFrom(new InternetAddress(fromMail, fromName));
                sender.send(message);
                log.debug("Mail sent for product: {}", trackedProduct.getId());
                log.debug("Mail sent from [{}]({})", fromName, fromMail);
                log.debug("Mail sent at {}", subscriber.getEmail());
                log.debug("Text sent: {}", text);
            } catch (MessagingException | UnsupportedEncodingException e) {
                log.error("failed to send E-Mail for product: {}", trackedProduct, e);
            }
            return true;
        } else {
            log.debug("did not send message for subscriber '{}', because the mail is missing", subscriber.getName());
            return false;
        }
    }

    private String productDetails(TrackedProduct trackedProduct) {
        return String.join("\n",
                String.format("ID: %s", trackedProduct.getId()),
                String.format("Name: %s", trackedProduct.getName()),
                String.format("Price Change: %s->%s", trackedProduct.getOldPrice(), trackedProduct.getPrice()),
                String.format("URL: %s",trackedProduct.getUrl())
        );
    }
}
