package com.solv.localization.repository.model;

import lombok.Getter;
import lombok.Setter;
import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;
@Getter
@Document(collection = "translations")
public class Translation {
    public Translation() {
        // Initialize the translations map
        this.translations = new HashMap<>();
    }

    @Id
    @Setter
    private String id;

    @Setter
    private String text;

    @Setter
    private Map<String, String> translations;
    @Setter
    private Date lastUpdated;
    @Setter
    private String updatedBy;

    public Translation orElse(Object o) {
        return null;
    }
}

