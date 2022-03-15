package com.epam.esm.gcs.model;

import lombok.Data;
import lombok.NoArgsConstructor;

import javax.persistence.Entity;
import javax.persistence.Table;
import java.util.Objects;

@Data
@NoArgsConstructor(force = true)
@Entity
@Table(name = TagModel_.TABLE)
public class TagModel extends NamedModel {

    public TagModel(Long id, String name) {
        super(id, name);
    }

    public TagModel(String name) {
        this(null, name);
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
        return Objects.equals(getId(), tagModel.getId()) && getName().equalsIgnoreCase(tagModel.getName());
    }

}
