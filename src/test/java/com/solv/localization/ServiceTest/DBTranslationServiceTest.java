package com.solv.localization.ServiceTest;


import com.solv.localization.Service.DBTranslationService;
import com.solv.localization.repository.model.Translation;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.MockitoAnnotations;
import org.springframework.dao.DataAccessException;
import org.springframework.data.mongodb.core.MongoTemplate;

import java.util.ArrayList;
import java.util.List;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.when;

class DBTranslationServiceTest {

    @Mock
    private MongoTemplate mongoTemplate;

    @InjectMocks
    private DBTranslationService translationService;

    @BeforeEach
    void setUp() {
        MockitoAnnotations.openMocks(this);
    }

    @Test
    void testGetAllTranslations() {
        // Arrange
        List<Translation> expectedTranslations = new ArrayList<>();
        when(mongoTemplate.findAll(Translation.class)).thenReturn(expectedTranslations);

        // Act
        List<Translation> actualTranslations = translationService.getAllTranslations();

        // Assert
        assertEquals(expectedTranslations, actualTranslations);
    }

    @Test
    void testGetTranslationById() {
        // Arrange
        String id = "123";
        Translation expectedTranslation = new Translation();
        when(mongoTemplate.findById(id, Translation.class)).thenReturn(expectedTranslation);

        // Act
        Translation actualTranslation = translationService.getTranslationById(id);

        // Assert
        assertEquals(expectedTranslation, actualTranslation);
    }

    @Test
    void testCreateTranslation_Success() {
        // Arrange
        String id = "123";
        Translation translation = new Translation();
        when(mongoTemplate.save(translation)).thenReturn(translation);

        // Act
        Translation createdTranslation = translationService.createOrUpdateTranslation(id, translation);

        // Assert
        assertNotNull(createdTranslation);
        assertEquals(id, createdTranslation.getId());
    }

    @Test
    void testCreateTranslation_Failure() {
        // Arrange
        String id = "123";
        Translation translation = new Translation();
        when(mongoTemplate.save(translation)).thenThrow(DataAccessException.class);

        // Act & Assert
        assertThrows(RuntimeException.class, () -> translationService.createOrUpdateTranslation(id, translation));
    }

    @Test
    void testUpdateTranslation() {
        // Arrange
        String id = "123";
        Translation existingTranslation = new Translation();
        when(translationService.getTranslationById(id)).thenReturn(existingTranslation);

        Translation updatedTranslation = new Translation();
        updatedTranslation.setId(id);
        when(mongoTemplate.save(updatedTranslation)).thenReturn(updatedTranslation);

        // Act
        Translation result = translationService.createOrUpdateTranslation(id, updatedTranslation);

        // Assert
        assertNotNull(result);
        assertEquals(id, result.getId());
    }
}

