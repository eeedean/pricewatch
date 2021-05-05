package me.redoak.edean.pricewatch.notification.telegram.bot.commands;

import me.redoak.edean.pricewatch.products.TrackedProductService;
import org.quartz.SchedulerException;
import org.springframework.boot.autoconfigure.condition.ConditionalOnProperty;
import org.springframework.stereotype.Component;
import org.telegram.telegrambots.meta.api.objects.Message;

import java.util.List;

@ConditionalOnProperty(name = "me.redoak.edean.pricewatch.telegram.enabled", havingValue = "true")
@Component
public class UpdateCommand extends AbstractCommand {

    private final TrackedProductService trackedProductService;

    public UpdateCommand(TrackedProductService trackedProductService){
        super();
        this.trackedProductService = trackedProductService;
    }

    @Override
    protected String execute(Message message, List<Argument> argumentList) {
        try {
            trackedProductService.triggerUpdate();
        } catch (SchedulerException e) {
            e.printStackTrace();
        }
        return "Wenn es Neuigkeiten gibt melde ich mich gleich bei dir.";
    }

    @Override
    protected void initializeArguments(List<Argument> argumentList) {
        argumentList.add(Argument.builder()
                .name("Update")
                .description("Mit diesem Befehl lädst du die neuen Preise für deine Produkte.")
                .value("/update")
                .build());
    }
}
