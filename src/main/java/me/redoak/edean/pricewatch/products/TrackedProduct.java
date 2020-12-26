package me.redoak.edean.pricewatch.products;

import com.fasterxml.jackson.annotation.JsonIgnore;
import lombok.extern.slf4j.Slf4j;
import me.redoak.edean.pricewatch.logic.Shop;
import me.redoak.edean.pricewatch.subscribers.Subscriber;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
import lombok.NoArgsConstructor;
import lombok.ToString;
import org.hibernate.annotations.GenericGenerator;

import javax.persistence.CascadeType;
import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EnumType;
import javax.persistence.Enumerated;
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.net.URL;
import java.time.ZonedDateTime;
import java.util.HashSet;
import java.util.Set;

/**
 * Represents a product to be tracked on some web shop site.
 */
@Slf4j
@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString(exclude = "subscribers")
@Table(name = "tracked_product")
@Entity
public class TrackedProduct {

    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", unique = true, length = 36)
    private String id;

    @Column(name = "name")
    private String name;

    @Column(name = "price", scale = 2)
    private BigDecimal price;

    @Column(name = "old_price", scale = 2)
    private BigDecimal oldPrice;

    @Column(name = "url")
    private URL url;

    @Builder.Default
    @Column(name = "tracked")
    private boolean tracked = true;

    @Column(name = "shop", length = 50)
    @Enumerated(EnumType.STRING)
    private Shop shop;

    @Column(name = "updated_at")
    private ZonedDateTime updatedAt;

    @JsonIgnore
    @ManyToMany(mappedBy = "trackedProducts", fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    private Set<Subscriber> subscribers;

    public void addSubscriber(Subscriber subscriber) {
        if(subscribers == null)
            subscribers = new HashSet<>();
        subscribers.add(subscriber);
        if(subscriber.getTrackedProducts() == null || !subscriber.getTrackedProducts().contains(this))
            subscriber.addProduct(this);
    }

    public void removeSubscriber(Subscriber subscriber) {
        if(subscribers != null && subscribers.contains(subscriber)) {
            log.debug("removing {} from {}", subscriber.getName(), this.getUrl());
            subscribers.remove(subscriber);
            subscriber.removeTrackedProduct(this);
        }
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;

        if (!(o instanceof TrackedProduct))
            return false;

        TrackedProduct other = (TrackedProduct) o;

        return id != null &&
                id.equals(other.getId());
    }

    @Override
    public int hashCode() {
        return getClass().hashCode();
    }
}
