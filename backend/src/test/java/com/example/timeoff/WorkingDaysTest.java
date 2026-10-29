package com.example.timeoff;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.timeoff.service.WorkingDays;
import java.time.DayOfWeek;
import java.time.LocalDate;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
class WorkingDaysTest {

    @Autowired
    private WorkingDays workingDays;

    @ParameterizedTest(name = "{0} {1}: {2}")
    @CsvSource({
        "2026-11-02, MONDAY, true",
        "2026-11-03, TUESDAY, true",
        "2026-11-04, WEDNESDAY, true",
        "2026-11-05, THURSDAY, true",
        "2026-11-06, FRIDAY, true",
        "2026-11-07, SATURDAY, false",
        "2026-11-08, SUNDAY, false"
    })
    void onlyMondayToFridayAreWorkingDays_R10(LocalDate date, DayOfWeek day, boolean expected) {
        assertThat(date.getDayOfWeek()).isEqualTo(day);
        assertThat(workingDays.isWorkingDay(date)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "{0}..{1}: {2}")
    @CsvSource({
        "2026-11-02, 2026-11-06, 5",
        "2026-11-06, 2026-11-09, 2",
        "2026-11-07, 2026-11-08, 0",
        "2026-11-02, 2026-11-02, 1",
        "2026-11-07, 2026-11-07, 0",
        "2026-11-07, 2026-11-09, 1",
        "2026-11-01, 2026-11-07, 5",
        "2026-11-02, 2026-11-15, 10",
        "2026-11-30, 2026-12-04, 5"
    })
    void countIncludesBothEndsAndSkipsWeekends_R10(LocalDate start, LocalDate end, int expected) {
        assertThat(workingDays.count(start, end)).isEqualTo(expected);
    }
}
