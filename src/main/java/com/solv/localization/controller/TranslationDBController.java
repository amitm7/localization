package com.solv.localization.controller;


import com.solv.localization.Service.DBTranslationService;
import com.solv.localization.repository.model.Translation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.web.bind.annotation.*;

import java.util.List;

@RestController
@RequestMapping("/translations")
@CrossOrigin(origins = "http://localhost:4200")
public class TranslationDBController {

    @Autowired
    private final DBTranslationService dbTranslationService;;

    public TranslationDBController(DBTranslationService dbTranslationService) {
        this.dbTranslationService = dbTranslationService;
    }
    @GetMapping
    public List<Translation> getAllTranslations() {
        return dbTranslationService.getAllTranslations();
    }

    @RequestMapping(value = "/getTranslationByID",method = RequestMethod.GET)
    public Translation getTranslationById(@RequestParam String id) {
        return dbTranslationService.getTranslationById(id);
    }

    @RequestMapping(value = "/updateTranslationByID",method = RequestMethod.PUT)
    public Translation updateTranslation(@RequestParam String id, @RequestBody Translation translation) {
        translation.setId(id);
        return dbTranslationService.createOrUpdateTranslation(id,translation);
    }
    @RequestMapping(value = "/deleteTranslationByID",method = RequestMethod.DELETE)
    public void deleteTranslation(@RequestParam String id) {
        dbTranslationService.removeTranslationById(id);
    }

    @RequestMapping(value = "/getTranslationByText",method = RequestMethod.GET)
    public Translation getTranslationByText(@RequestParam String text) {
       return dbTranslationService.getTranslationByText(text);
    }
}
