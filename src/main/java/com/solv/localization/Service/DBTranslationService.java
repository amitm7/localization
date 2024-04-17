package com.solv.localization.Service;

import com.mongodb.client.result.DeleteResult;
import com.solv.localization.repository.model.Translation;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.data.mongodb.core.MongoTemplate;
import org.springframework.data.mongodb.core.query.Criteria;
import org.springframework.data.mongodb.core.query.Query;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;
import org.springframework.dao.DataAccessException;

import java.util.List;

@Service
public class DBTranslationService {

    private final MongoTemplate mongoTemplate;

    @Autowired
    public DBTranslationService(MongoTemplate mongoTemplate) {
        this.mongoTemplate = mongoTemplate;
    }

    public List<Translation> getAllTranslations() {
        return mongoTemplate.findAll(Translation.class);
    }

    public Translation getTranslationById(String id) {
        Query query = Query.query(Criteria.where("id").is(id));
        return mongoTemplate.findOne(query, Translation.class);
    }

    public DeleteResult removeTranslationById(String id) {
        Query query = Query.query(Criteria.where("id").is(id));
        return mongoTemplate.remove(query,Translation.class);
    }

    public Translation getTranslationByText(String text) {
        Query query = Query.query(Criteria.where("text").is(text));
        return mongoTemplate.findOne(query, Translation.class);
    }

    @Transactional
    public Translation createOrUpdateTranslation(String id, Translation translation) {
        try {
            translation.setId(id);
            mongoTemplate.save(translation);
            return translation;
        } catch (DataAccessException e) {
            throw new RuntimeException("Failed to create/update translation", e);
        }
    }
}
