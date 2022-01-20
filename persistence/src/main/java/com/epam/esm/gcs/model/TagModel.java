package com.epam.esm.gcs.model;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Data;
import lombok.NoArgsConstructor;

@Data
@NoArgsConstructor(force = true, access = AccessLevel.PROTECTED)
@AllArgsConstructor
public class TagModel {

    private Long id;
    private String name;

    public TagModel(String name) {
        this.name = name;
    }

}
