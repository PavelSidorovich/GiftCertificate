package com.epam.esm.gcs.model;

import lombok.Builder;
import lombok.Data;
import lombok.EqualsAndHashCode;
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
@EqualsAndHashCode(callSuper = true)
@NoArgsConstructor(force = true)
@Entity
@EntityListeners(AuditingEntityListener.class)
@Table(name = CertificateModel_.TABLE)
public class CertificateModel extends NamedModel {

    @Column(name = CertificateModel_.DESCRIPTION, nullable = false)
    private String description;

    @Column(name = CertificateModel_.PRICE, nullable = false)
    private BigDecimal price;

    @Column(name = CertificateModel_.DURATION, nullable = false)
    private Integer duration;

    @CreatedDate
    @Column(name = CertificateModel_.CREATE_DATE, nullable = false)
    private LocalDateTime createDate;

    @LastModifiedDate
    @Column(name = CertificateModel_.LAST_UPDATE_DATE, nullable = false)
    private LocalDateTime lastUpdateDate;

    @Exclude
    @ManyToMany
    @JoinTable(
            name = "certificates_by_tags",
            joinColumns = @JoinColumn(name = "certificate_id"),
            inverseJoinColumns = @JoinColumn(name = "tag_id")
    )
    private Set<TagModel> tags;

    @Builder
    private CertificateModel(Long id, String name, String description,
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
