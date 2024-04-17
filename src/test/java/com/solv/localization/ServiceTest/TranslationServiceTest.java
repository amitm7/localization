package com.solv.localization.ServiceTest;

import com.solv.localization.OpenAi.OpenAiApi;
import com.solv.localization.Service.DBTranslationService;
import com.solv.localization.Service.TranslationService;
import com.solv.localization.repository.model.Translation;
import com.solv.localization.utils.HashingUtils;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.util.Collections;
import java.util.HashMap;
import java.util.Map;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.*;
import static org.springframework.test.util.AssertionErrors.assertNull;

public class TranslationServiceTest {

    @Mock
    private Jedis jedis;

    @Mock
    private DBTranslationService dbTranslationService;

    @Mock
    private OpenAiApi openAiApi;

    @InjectMocks
    private TranslationService translationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void getFromRedis_ExceptionThrown_ReturnNull() {
        String hashText = "hashText";
        String language = "Hindi";

        when(jedis.hget(hashText, language)).thenThrow(JedisException.class);

        String result = translationService.getFromRedis(hashText, language);

        assertEquals(null, result);
    }

    @Test
    void getTranslationFromDB_NullDocument_ReturnNull() {
        String hashText = "hashText";
        String language = "fr";

        when(dbTranslationService.getTranslationById(hashText)).thenReturn(null);

        String result = translationService.getTranslationFromDB(hashText, language);

        assertNull(null, result);
    }

    @Test
    void getTranslationFromDB_NoTranslation_ReturnNull() {
        String hashText = "hashText";
        String language = "fr";

        Translation document = new Translation();
        when(dbTranslationService.getTranslationById(hashText)).thenReturn(document);

        String result = translationService.getTranslationFromDB(hashText, language);

        assertEquals(null, result);
    }

    @Test
    void getTranslationFromDB_NoTranslations_ReturnNull() {
        String hashText = "hashText";
        String language = "fr";

        Translation document = new Translation();
        document.setTranslations(null);
        when(dbTranslationService.getTranslationById(hashText)).thenReturn(document);

        String result = translationService.getTranslationFromDB(hashText, language);

        assertEquals(null, result);
    }

    @Test
    void getTranslationFromDB_TranslationFound_ReturnTranslation() {
        String hashText = "hashText";
        String language = "fr";
        String translation = "bonjour";

        Translation document = new Translation();
        Map<String, String> translations = new HashMap<>();
        translations.put(language, translation);
        document.setTranslations(translations);
        when(dbTranslationService.getTranslationById(hashText)).thenReturn(document);

        String result = translationService.getTranslationFromDB(hashText, language);

        assertEquals(translation, result);
    }




    @Test
    void saveDataToDB_TranslationExists_UpdateTranslation() {
        String text = "hello";
        String sourceLang = "en";
        String targetLang = "fr";
        String hashText = HashingUtils.hashText(text);
        String translation = "bonjour";

        Translation existingTranslation = new Translation();
        existingTranslation.setTranslations(Collections.singletonMap(sourceLang, text));

        when(dbTranslationService.getTranslationById(hashText)).thenReturn(existingTranslation);

        translationService.saveDataToDB(text, sourceLang, targetLang, hashText, translation);

        verify(dbTranslationService, times(1)).getTranslationById(hashText);
        verify(dbTranslationService, times(1)).createOrUpdateTranslation(hashText, existingTranslation);
    }

    @Test
    void saveDataToDB_TranslationNotExists_CreateTranslation() {
        String text = "hello";
        String sourceLang = "en";
        String targetLang = "fr";
        String hashText = HashingUtils.hashText(text);
        String translation = "bonjour";

        when(dbTranslationService.getTranslationById(hashText)).thenReturn(null);

        translationService.saveDataToDB(text, sourceLang, targetLang, hashText, translation);

        verify(dbTranslationService, times(1)).getTranslationById(hashText);
    }
}


