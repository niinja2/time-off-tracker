package com.example.timeoff;

import static org.assertj.core.api.Assertions.assertThat;

import com.example.timeoff.domain.TimeOffRequest;
import com.example.timeoff.domain.TimeOffRequestRepository;
import com.example.timeoff.domain.TimeOffType;
import com.example.timeoff.domain.User;
import com.example.timeoff.domain.UserRepository;
import com.example.timeoff.service.Balance;
import com.example.timeoff.service.BalanceService;
import jakarta.persistence.EntityManager;
import java.time.Instant;
import java.time.LocalDate;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class BalanceServiceTest {

    private static final Instant NOW = Instant.parse("2026-10-30T10:00:00Z");

    @Autowired
    private BalanceService balanceService;

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TimeOffRequestRepository requestRepository;

    @Autowired
    private EntityManager entityManager;

    @ParameterizedTest(name = "hired {0}, year {1}: {2}")
    @CsvSource({
        "2020-01-01, 2026, 20",
        "2026-06-01, 2027, 20"
    })
    void entitlementIsTwentyAfterHireYear_R11(LocalDate hireDate, int year, int expected) {
        assertThat(balanceService.entitlement(hireDate, year)).isEqualTo(expected);
    }

    @ParameterizedTest(name = "hired {0}: {1}")
    @CsvSource({
        "2026-01-15, 20",
        "2026-02-01, 19",
        "2026-06-01, 12",
        "2026-06-30, 12",
        "2026-07-01, 10",
        "2026-11-01, 4",
        "2026-12-01, 2"
    })
    void entitlementIsProportionalInHireYear_Q15(LocalDate hireDate, int expected) {
        assertThat(balanceService.entitlement(hireDate, 2026)).isEqualTo(expected);
    }

    @Test
    void entitlementIsZeroBeforeHireYear_T3() {
        LocalDate hireDate = LocalDate.of(2026, 6, 1);

        assertThat(balanceService.entitlement(hireDate, 2025)).isZero();
    }

    @Test
    void tomHasProportionalBalanceInHireYear_Q15() {
        assertThat(balanceOf("tom", 2026)).isEqualTo(new Balance(
            2026,
            12,
            0,
            0,
            12));
    }

    @Test
    void approvedVacationCountsAsUsedWorkingDays_R12() {
        request(
            "ivan",
            TimeOffType.VACATION,
            LocalDate.of(2026, 11, 6),
            LocalDate.of(2026, 11, 9))
            .approve(lead(), null, NOW);

        assertThat(balanceOf("ivan", 2026)).isEqualTo(new Balance(
            2026,
            20,
            2,
            0,
            18));
    }

    @Test
    void pendingVacationReducesRemaining_R13() {
        request(
            "ivan",
            TimeOffType.VACATION,
            LocalDate.of(2026, 11, 2),
            LocalDate.of(2026, 11, 6));

        assertThat(balanceOf("ivan", 2026)).isEqualTo(new Balance(
            2026,
            20,
            0,
            5,
            15));
    }

    @Test
    void approvedAndPendingVacationAddUp_R13() {
        request(
            "ivan",
            TimeOffType.VACATION,
            LocalDate.of(2026, 11, 2),
            LocalDate.of(2026, 11, 6))
            .approve(lead(), null, NOW);
        request(
            "ivan",
            TimeOffType.VACATION,
            LocalDate.of(2026, 11, 9),
            LocalDate.of(2026, 11, 10));

        assertThat(balanceOf("ivan", 2026)).isEqualTo(new Balance(
            2026,
            20,
            5,
            2,
            13));
    }

    @Test
    void weekendOnlyVacationUsesNoDays_R10() {
        request(
            "ivan",
            TimeOffType.VACATION,
            LocalDate.of(2026, 11, 7),
            LocalDate.of(2026, 11, 8))
            .approve(lead(), null, NOW);

        assertThat(balanceOf("ivan", 2026)).isEqualTo(new Balance(
            2026,
            20,
            0,
            0,
            20));
    }

    @Test
    void sickLeaveAndDayOffDoNotUseBalance_R14() {
        request(
            "ivan",
            TimeOffType.SICK_LEAVE,
            LocalDate.of(2026, 11, 2),
            LocalDate.of(2026, 11, 6))
            .approve(lead(), null, NOW);
        request(
            "ivan",
            TimeOffType.SICK_LEAVE,
            LocalDate.of(2026, 11, 9),
            LocalDate.of(2026, 11, 9));
        request(
            "ivan",
            TimeOffType.DAY_OFF,
            LocalDate.of(2026, 11, 10),
            LocalDate.of(2026, 11, 10))
            .approve(lead(), null, NOW);
        request(
            "ivan",
            TimeOffType.DAY_OFF,
            LocalDate.of(2026, 11, 11),
            LocalDate.of(2026, 11, 11));

        assertThat(balanceOf("ivan", 2026)).isEqualTo(new Balance(
            2026,
            20,
            0,
            0,
            20));
    }

    @Test
    void rejectedAndCancelledVacationDoNotUseBalance_R12() {
        request(
            "ivan",
            TimeOffType.VACATION,
            LocalDate.of(2026, 11, 2),
            LocalDate.of(2026, 11, 6))
            .reject(lead(), "Team is short", NOW);
        request(
            "ivan",
            TimeOffType.VACATION,
            LocalDate.of(2026, 11, 9),
            LocalDate.of(2026, 11, 13))
            .cancel(user("ivan"), null, NOW);
        TimeOffRequest approvedThenCancelled = request(
            "ivan",
            TimeOffType.VACATION,
            LocalDate.of(2026, 11, 16),
            LocalDate.of(2026, 11, 20));
        approvedThenCancelled.approve(lead(), null, NOW);
        approvedThenCancelled.cancel(user("ivan"), null, NOW);

        assertThat(balanceOf("ivan", 2026)).isEqualTo(new Balance(
            2026,
            20,
            0,
            0,
            20));
    }

    @Test
    void vacationCountsOnlyInYearOfItsStart_T3() {
        request(
            "ivan",
            TimeOffType.VACATION,
            LocalDate.of(2025, 12, 1),
            LocalDate.of(2025, 12, 5))
            .approve(lead(), null, NOW);

        assertThat(balanceOf("ivan", 2026)).isEqualTo(new Balance(
            2026,
            20,
            0,
            0,
            20));
        assertThat(balanceOf("ivan", 2025)).isEqualTo(new Balance(
            2025,
            20,
            5,
            0,
            15));
    }

    @Test
    void otherUsersVacationDoesNotCount_R12() {
        request(
            "marko",
            TimeOffType.VACATION,
            LocalDate.of(2026, 11, 2),
            LocalDate.of(2026, 11, 6))
            .approve(lead(), null, NOW);

        assertThat(balanceOf("ivan", 2026)).isEqualTo(new Balance(
            2026,
            20,
            0,
            0,
            20));
    }

    @Test
    void remainingCanBeNegative_D6() {
        request(
            "ivan",
            TimeOffType.VACATION,
            LocalDate.of(2026, 11, 2),
            LocalDate.of(2026, 12, 4))
            .approve(lead(), null, NOW);

        assertThat(balanceOf("ivan", 2026)).isEqualTo(new Balance(
            2026,
            20,
            25,
            0,
            -5));
    }

    private TimeOffRequest request(
        String username,
        TimeOffType type,
        LocalDate start,
        LocalDate end) {
        TimeOffRequest request = new TimeOffRequest(
            user(username),
            type,
            start,
            end,
            null,
            NOW);
        return requestRepository.save(request);
    }

    private Balance balanceOf(String username, int year) {
        entityManager.flush();
        Long userId = user(username).getId();
        return balanceService.balance(userId, year);
    }

    private User lead() {
        return user("ana");
    }

    private User user(String username) {
        return userRepository.findByUsername(username)
            .orElseThrow();
    }
}
