package com.yas.payment.model;

import jakarta.persistence.Entity;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import jakarta.persistence.Transient;
import jakarta.persistence.Version;
import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.NoArgsConstructor;
import org.hibernate.annotations.DynamicUpdate;
import org.springframework.data.domain.Persistable;

import com.yas.commonlibrary.model.AbstractAuditEntity;

@DynamicUpdate
@Entity
@Table(name = "payment_provider")
@lombok.Setter
@lombok.Getter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class PaymentProvider extends AbstractAuditEntity implements Persistable<String> {
    @Id
    private String id;
    private boolean enabled;
    private String name;
    private String configureUrl;
    private String landingViewComponentName;
    private String additionalSettings;
    private Long mediaId;

    @Version
    private int version;

    @Transient
    private boolean isNew;

    @Override
    public boolean isNew() {
        return isNew;
    }
}