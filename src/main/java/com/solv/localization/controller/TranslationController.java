package com.solv.localization.controller;

import com.solv.localization.Service.TranslationService;
import lombok.RequiredArgsConstructor;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class TranslationController {

    private final TranslationService translationService ;
    @CrossOrigin(origins = "http://localhost:4200")
    @RequestMapping(value = "/getTranslation",method = RequestMethod.GET)
    public String getTranslation(@RequestParam String text,
                                 @RequestParam(required = false) String id,
                                 @RequestParam(required = false) String sourceLanguage ,
                                 @RequestParam String targetLanguage){
        if(sourceLanguage == null || sourceLanguage.trim() == "" ) {
            sourceLanguage = "English";
        }
        if(targetLanguage == null || targetLanguage.trim() == "" ) {
            return text;
        }
        String translatedText = translationService.translate(text , sourceLanguage, targetLanguage);
        if(translatedText == null || translatedText.trim() == "" ) {
            return text;
        }
        return  translatedText;
    }


}
