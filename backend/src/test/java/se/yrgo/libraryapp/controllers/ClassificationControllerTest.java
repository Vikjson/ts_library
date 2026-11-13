package se.yrgo.libraryapp.controllers;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;

import se.yrgo.libraryapp.dao.ClassificationDao;
import se.yrgo.libraryapp.entities.DdsClassification;

import java.util.List;

import static org.assertj.core.api.Assertions.*;
import static org.mockito.Mockito.*;

@MockitoSettings(strictness = Strictness.STRICT_STUBS)
class ClassificationControllerTest {

    @Mock
    private ClassificationDao classificationDao;

    private ClassificationController controller;

    @BeforeEach
    void setUp() {
        controller = new ClassificationController(classificationDao);
    }

    @Test
    void shouldReturnAvailableClassifications() {
        List<DdsClassification> classifications = List.of(
                new DdsClassification(100, "Philosophy"),
                new DdsClassification(200, "Religion")
        );

        when(classificationDao.getAvailable())
                .thenReturn(classifications);

        List<DdsClassification> result = controller.getAvailable();

        assertThat(result).containsExactlyElementsOf(classifications);

        verify(classificationDao).getAvailable();
    }
}