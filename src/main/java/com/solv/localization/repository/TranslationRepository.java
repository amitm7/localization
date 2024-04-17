package com.solv.localization.repository;

import com.solv.localization.repository.model.Translation;
import org.springframework.data.mongodb.repository.MongoRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TranslationRepository extends MongoRepository<Translation,String> {
}
