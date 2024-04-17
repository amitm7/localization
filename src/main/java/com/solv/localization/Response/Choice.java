package com.solv.localization.Response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Choice {
    private int index;
    private Message message;
    private Object logprobs;
    private String finishReason;

}
