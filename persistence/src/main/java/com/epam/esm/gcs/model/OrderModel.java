package com.epam.esm.gcs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.JoinColumn;
import javax.persistence.ManyToOne;
import javax.persistence.OneToOne;
import javax.persistence.PrePersist;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = OrderModel_.TABLE)
public class OrderModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @OneToOne
    @JoinColumn(name = OrderModel_.CERTIFICATE_ID, referencedColumnName = GiftCertificateModel_.ID)
    private GiftCertificateModel certificate;

    @ManyToOne
    @JoinColumn(name = OrderModel_.USER_ID, referencedColumnName = UserModel_.ID)
    private UserModel user;

    @Column(name = OrderModel_.TOTAL_COST, nullable = false)
    private BigDecimal cost;

    @CreatedDate
    @Column(name = OrderModel_.PURCHASE_DATE, nullable = false)
    private LocalDateTime purchaseDate;

    @PrePersist
    public void onPrePersist() {
        cost = certificate.getPrice();
        user.setBalance(user.getBalance().subtract(cost));
    }

}
