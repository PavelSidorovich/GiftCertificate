package com.epam.esm.gcs.model;

import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.GeneratedValue;
import javax.persistence.GenerationType;
import javax.persistence.Id;
import javax.persistence.PrePersist;
import javax.persistence.PreUpdate;
import javax.persistence.Table;
import java.util.Locale;
import java.util.Objects;

@Data
@AllArgsConstructor
@NoArgsConstructor(force = true)
@Entity
@Table(name = "gift_certificate_tag")
public class TagModel {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @Column(unique = true, nullable = false)
    private String name;

    public TagModel(String name) {
        this.name = name;
    }

    @PrePersist
    @PreUpdate
    public void onPrePersistAndUpdate() {
        name = name.toLowerCase(Locale.ROOT);
    }

    // should contain equalsIgnoreCase on name field
    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (o == null || getClass() != o.getClass()) {
            return false;
        }
        TagModel tagModel = (TagModel) o;
        return Objects.equals(id, tagModel.id) && name.equalsIgnoreCase(tagModel.name);
    }

}
