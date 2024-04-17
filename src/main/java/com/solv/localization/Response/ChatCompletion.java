package com.solv.localization.Response;

import lombok.Getter;
import lombok.Setter;

import java.util.List;
@Setter
@Getter
public class ChatCompletion {
    private String id;
    private String object;
    private long created;
    private String model;
    private List<Choice> choices;
    private Usage usage;
    private String systemFingerprint;

    // Constructors, getters, and setters
}

