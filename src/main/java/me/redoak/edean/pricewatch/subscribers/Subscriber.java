package me.redoak.edean.pricewatch.subscribers;

import com.fasterxml.jackson.annotation.JsonIgnore;
import me.redoak.edean.pricewatch.products.TrackedProduct;
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
import javax.persistence.FetchType;
import javax.persistence.GeneratedValue;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.util.HashSet;
import java.util.Set;

@Builder
@NoArgsConstructor
@AllArgsConstructor
@Data
@ToString(exclude = "trackedProducts")
@EqualsAndHashCode(exclude = "trackedProducts")
@Table(name = "subscriber")
@Entity
public class Subscriber {

    @JsonIgnore
    @Id
    @GeneratedValue(generator = "uuid")
    @GenericGenerator(name = "uuid", strategy = "uuid2")
    @Column(name = "id", unique = true, length = 36)
    private String id;

    @Column(name = "name")
    private String name;

    @JsonIgnore
    @Column(name = "password_hash")
    private String passwordHash;

    @Column(name = "email")
    private String email;

    @JsonIgnore
    @Column(name = "telegram_chat_id")
    private String telegramChatId;

    @JsonIgnore
    @ManyToMany(fetch = FetchType.LAZY, cascade = CascadeType.ALL)
    @JoinTable(
            name = "subscriptions",
            joinColumns = @JoinColumn(name = "subscriber_id"),
            inverseJoinColumns = @JoinColumn(name = "tracked_product_id"))
    private Set<TrackedProduct> trackedProducts;

    public void addProduct(TrackedProduct trackedProduct) {
        if(this.trackedProducts == null)
            this.trackedProducts = new HashSet<>();
        this.trackedProducts.add(trackedProduct);
        if(!trackedProduct.getSubscribers().contains(this))
            trackedProduct.addSubscriber(this);
    }
}
