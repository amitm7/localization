package com.solv.localization.Service;

import com.solv.localization.OpenAi.OpenAiApi;
import com.solv.localization.Response.ChatCompletion;
import com.solv.localization.repository.model.Translation;
import com.solv.localization.utils.HashingUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;
import redis.clients.jedis.Jedis;
import redis.clients.jedis.exceptions.JedisException;

import java.net.URLEncoder;
import java.nio.charset.StandardCharsets;
import java.util.Arrays;
import java.util.Date;
import java.util.List;
import java.util.Map;

@Service
public class TranslationService {

    private final OpenAiApi openAiApi;
    private final Jedis jedis;
    private final DBTranslationService dbTranslationService;

    @Autowired
    public TranslationService(OpenAiApi openAiApi, Jedis jedis, DBTranslationService dbTranslationService) {
        this.openAiApi = openAiApi;
        this.jedis = jedis;
        this.dbTranslationService = dbTranslationService;
    }

    public String translate(String text, String sourceLang, String targetLang) {
        String hashText = HashingUtils.hashText(text);
        String translation = getFromRedis(hashText, targetLang);
        if (translation != null) {
            return translation;
        }

        translation = getTranslationFromDB(hashText, targetLang);
        if (translation != null) {
            return translation;
        }

        translation = getTranslationFromTranslationService(text, sourceLang, targetLang);
        if (translation != null) {
            jedis.hset(hashText, targetLang, translation);
            saveDataToDB(text, sourceLang, targetLang, hashText, translation);
            return translation;
        }

        return text;
    }

    public String getFromRedis(String hashText, String language)  {
        try {
            return jedis.hget(hashText, language);
        } catch (JedisException e) {
            System.err.println("Error accessing Redis: " + e.getMessage());
            return null;
        }
    }

    public String getTranslationFromDB(String hashText, String language) {
        Translation document = dbTranslationService.getTranslationById(hashText);
        if (document != null && document.getTranslations() != null) {
            String translation = document.getTranslations().get(language.trim());
            if (translation != null) {
                jedis.hset(hashText, language, translation);
            }
            return translation;
        }
        return null;
    }

    private String getTranslationFromTranslationService(String text, String sourceLang, String targetLang) {
        int maxTokens = 1000;
        double temperature = 0.5;
        String encoded = URLEncoder.encode(text, StandardCharsets.UTF_8).replaceAll("\\+", "%20");
        String prompt = createPrompt(encoded, sourceLang, targetLang);
        List<Map<String, String>> messages = Arrays.asList(
                Map.of("role", "system", "content", "You are a Language Translator assistant, skilled in doing Translation from." + sourceLang + " to " + targetLang + " And also you are sentiment-aware and culturally appropriate while performing translation "),
                Map.of("role", "user", "content", prompt)
        );
        ChatCompletion chatCompletion = openAiApi.generateTranslation(messages, maxTokens, temperature);
        return chatCompletion.getChoices().get(0).getMessage().getContent();
    }

    private String createPrompt(String sourceLang, String language, String text) {
        return "Translate the following sentences from " + sourceLang + " to " + language + ", ensuring that the translation is sentiment-aware and culturally appropriate. For terms that are commonly used in their " + sourceLang + " form within the" + " " + language + "-speaking demographic, such as 'computer,' retain the " + sourceLang + " word but write it using the " + language + " script." +
                " For other phrases or instructions, translate them in a way that is naturally understandable and commonly used by the general" +
                " " + language + "-speaking population. For example, if the phrase is 'press the button,' it should be translated to its commonly " +
                "used " + language + " equivalent, maintaining the sentiment and instructive tone of the original " + sourceLang + " phrase. :: " + text;
    }

     public void saveDataToDB(String text, String sourceLang, String targetLang, String hashText, String result) {
        Translation existingTranslation = dbTranslationService.getTranslationById(hashText);
        if (existingTranslation != null) {
            existingTranslation.getTranslations().put(targetLang, result);
            existingTranslation.setLastUpdated(new Date());
            existingTranslation.setUpdatedBy("System");
            dbTranslationService.createOrUpdateTranslation(hashText, existingTranslation);
        } else {
            Translation translation = new Translation();
            translation.setText(text);
            translation.getTranslations().put(sourceLang, text);
            translation.getTranslations().put(targetLang, result);
            translation.setLastUpdated(new Date());
            translation.setUpdatedBy("System");
            dbTranslationService.createOrUpdateTranslation(hashText, translation);
            System.out.println("Saved Data" + translation);
        }
    }


}
