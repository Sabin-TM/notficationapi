package gov.legmt.notifications.service;

import lombok.Builder;
import lombok.Data;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.junit.jupiter.MockitoExtension;

import java.time.LocalDate;
import java.time.LocalDateTime;
import java.time.LocalTime;
import java.util.Map;

import static org.assertj.core.api.Assertions.*;

/**
 * Tests for object to map converter.
 */
@ExtendWith(MockitoExtension.class)
class ObjectToMapConverterTests {

    @Data
    @Builder
    static class TestEvent {

        private String subject;

        private LocalDateTime date;

        public void get() {

            // Converter should skip methods named get as they are not property accessor methods
            throw new AssertionError("Method named \"get()\" erroneously included");
        }

        private String getSecret() {

            // Converted should skip private methods are they are not property accessor methods
            throw new AssertionError("Private method erroneously included");
        }
    }

    @InjectMocks
    private ObjectToMapConverter converter;

    /**
     * Tests that given object supplied to converter has properties then properties are added to resulting map.
     */
    @Test
    void testConvertGivenObjectHasProperties() {

        // Given
        TestEvent event = TestEvent.builder().subject("Mail subject").date(
                LocalDateTime.of(
                        LocalDate.of(2023, 12, 15),
                        LocalTime.of(12, 0))).build();

        // When
        Map<String, Object> result = this.converter.convert(event);

        // Then
        assertThat(result).hasSize(2)
                .hasFieldOrPropertyWithValue("subject", event.getSubject())
                .hasFieldOrPropertyWithValue("date", event.getDate());
    }
    /**
     * Tests that given object supplied to converted has no properties then no properties are added to resulting
     * map.
     */
    @Test
    void testConvertGivenDoesNotHaveProperties() {
        assertThat(this.converter.convert(new Object())).isEmpty();
    }
}
