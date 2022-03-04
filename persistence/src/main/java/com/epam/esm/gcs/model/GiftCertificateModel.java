package com.epam.esm.gcs.model;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Data;
import lombok.NoArgsConstructor;
import lombok.ToString.Exclude;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.EntityListeners;
import javax.persistence.FetchType;
import javax.persistence.JoinColumn;
import javax.persistence.JoinTable;
import javax.persistence.ManyToMany;
import javax.persistence.Table;
import java.math.BigDecimal;
import java.time.LocalDateTime;
import java.util.Set;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = GiftCertificateModel_.TABLE)
public class GiftCertificateModel extends NamedModel {

    @Column(name = GiftCertificateModel_.DESCRIPTION, nullable = false)
    private String description;

    @Column(name = GiftCertificateModel_.PRICE, nullable = false)
    private BigDecimal price;

    @Column(name = GiftCertificateModel_.DURATION, nullable = false)
    private Integer duration;

    @CreatedDate
    @Column(name = GiftCertificateModel_.CREATE_DATE, nullable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(name = GiftCertificateModel_.LAST_UPDATE_DATE, nullable = false)
    private LocalDateTime lastUpdateDate;

    @Exclude
    @ManyToMany(fetch = FetchType.LAZY)
    @JoinTable(
            name = "gift_certificates_by_tags",
            joinColumns = @JoinColumn(name = "gift_certificate_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<TagModel> tags;

    @Builder
    public GiftCertificateModel(Long id, String name, String description,
                                BigDecimal price, Integer duration, LocalDateTime createDate,
                                LocalDateTime lastUpdateDate, Set<TagModel> tags) {
        super(id, name);
        this.description = description;
        this.price = price;
        this.duration = duration;
        this.createDate = createDate;
        this.lastUpdateDate = lastUpdateDate;
        this.tags = tags;
    }

}
