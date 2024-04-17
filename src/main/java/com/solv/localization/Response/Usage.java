package com.solv.localization.Response;

import lombok.Getter;
import lombok.Setter;

@Setter
@Getter
public class Usage {
    private int promptTokens;
    private int completionTokens;
    private int totalTokens;

}
