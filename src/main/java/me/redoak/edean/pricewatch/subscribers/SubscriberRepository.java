package me.redoak.edean.pricewatch.subscribers;

import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.Optional;

public interface SubscriberRepository extends JpaRepository<Subscriber, String> {

    @Query("select s from Subscriber s left join fetch s.trackedProducts where s.name = :name")
    Optional<Subscriber> findByName(@Param("name") String name);

    @Query("select s from Subscriber s left join fetch s.trackedProducts where s.telegramChatId = :chatId")
    Optional<Subscriber> findByTelegramChatId(@Param("chatId") String chatId);
}
