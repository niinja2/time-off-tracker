package com.example.timeoff;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatCode;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import com.example.timeoff.domain.Team;
import com.example.timeoff.domain.TeamRepository;
import com.example.timeoff.domain.TimeOffRequestRepository;
import com.example.timeoff.domain.User;
import com.example.timeoff.domain.UserRepository;
import java.time.LocalDate;
import java.util.List;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Import;
import org.springframework.dao.DataIntegrityViolationException;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.transaction.annotation.Transactional;

@SpringBootTest
@Import(TestcontainersConfiguration.class)
@Transactional
class SeedAndSchemaTest {

    private static final BCryptPasswordEncoder ENCODER = new BCryptPasswordEncoder();

    @Autowired
    private UserRepository userRepository;

    @Autowired
    private TeamRepository teamRepository;

    @Autowired
    private TimeOffRequestRepository requestRepository;

    @Autowired
    private JdbcTemplate jdbc;

    @Test
    void seedHasEightUsers_T1() {
        assertThat(userRepository.findAll()).hasSize(8);
    }

    @Test
    void seedHasTwoTeams_T1() {
        assertThat(teamRepository.findAll()).hasSize(2);
    }

    @Test
    void seedHasNoRequests_Q55() {
        assertThat(requestRepository.findAll()).isEmpty();
    }

    @Test
    void anaLeadsPlatformTeam_Q6() {
        assertThat(team("Platform").getLead().getUsername()).isEqualTo("ana");
    }

    @Test
    void lukaLeadsMobileTeam_Q6() {
        assertThat(team("Mobile").getLead().getUsername()).isEqualTo("luka");
    }

    @Test
    void platformMembersAreAnaIvanMarkoPetra_Q3() {
        assertThat(memberNames("Platform")).containsExactlyInAnyOrder("ana", "ivan", "marko", "petra");
    }

    @Test
    void mobileMembersAreLukaSaraTom_Q3() {
        assertThat(memberNames("Mobile")).containsExactlyInAnyOrder("luka", "sara", "tom");
    }

    @Test
    void evaIsAdminWithoutTeam_Q4() {
        assertThat(user("eva").isAdmin()).isTrue();
        assertThat(user("eva").getTeams()).isEmpty();
    }

    @Test
    void onlyEvaIsAdmin_T1() {
        assertThat(userRepository.findAll().stream()
            .filter(User::isAdmin)
            .map(User::getUsername))
            .containsExactly("eva");
    }

    @Test
    void hireDatesMatchSeed_D16() {
        assertThat(user("tom").getHireDate()).isEqualTo(LocalDate.of(2026, 6, 1));
        assertThat(userRepository.findAll().stream()
            .filter(u -> !u.getUsername().equals("tom"))
            .map(User::getHireDate))
            .allMatch(LocalDate.of(2020, 1, 1)::equals);
    }

    @Test
    void allUsersHavePasswordHashOfPassword_D16() {
        assertThat(userRepository.findAll())
            .allSatisfy(u -> assertThat(ENCODER.matches("password", u.getPasswordHash())).isTrue());
    }

    @Test
    void dbRejectsEndDateBeforeStartDate_R8() {
        assertThatThrownBy(() -> insertRequest(
            "ivan",
            "VACATION",
            "PENDING",
            LocalDate.of(2026, 11, 6),
            LocalDate.of(2026, 11, 2)))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void dbAcceptsOneDayRequest_R8() {
        assertThatCode(() -> insertRequest(
            "ivan",
            "VACATION",
            "PENDING",
            LocalDate.of(2026, 11, 2),
            LocalDate.of(2026, 11, 2)))
            .doesNotThrowAnyException();
    }

    @Test
    void dbRejectsUnknownType_T1() {
        assertThatThrownBy(() -> insertRequest(
            "ivan",
            "BIRTHDAY",
            "PENDING",
            LocalDate.of(2026, 11, 2),
            LocalDate.of(2026, 11, 3)))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void dbRejectsUnknownStatus_T1() {
        assertThatThrownBy(() -> insertRequest(
            "ivan",
            "VACATION",
            "DONE",
            LocalDate.of(2026, 11, 2),
            LocalDate.of(2026, 11, 3)))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void dbRejectsRequestWithoutOwner_T1() {
        assertThatThrownBy(() -> jdbc.update(
            "INSERT INTO time_off_requests (owner_id, type, start_date, end_date, status, created_at)"
                + " VALUES (NULL, 'VACATION', ?, ?, 'PENDING', now())",
            LocalDate.of(2026, 11, 2), LocalDate.of(2026, 11, 3)))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void dbRejectsDuplicateUsername_T1() {
        assertThatThrownBy(() -> jdbc.update(
            "INSERT INTO users (username, name, password_hash, admin, hire_date)"
                + " VALUES ('ana', 'Ana 2', 'x', false, '2020-01-01')"))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void dbRejectsDuplicateTeamName_T1() {
        assertThatThrownBy(() -> jdbc.update(
            "INSERT INTO teams (name, lead_id) SELECT 'Platform', id FROM users WHERE username = 'ana'"))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void dbRejectsTeamWithoutLead_Q7() {
        assertThatThrownBy(() -> jdbc.update("INSERT INTO teams (name, lead_id) VALUES ('Design', NULL)"))
            .isInstanceOf(DataIntegrityViolationException.class);
    }

    @Test
    void dbDefaultsAdminToFalse_T1() {
        jdbc.update("INSERT INTO users (username, name, password_hash, hire_date)"
            + " VALUES ('marta', 'Marta', 'x', '2020-01-01')");

        Boolean admin = jdbc.queryForObject("SELECT admin FROM users WHERE username = 'marta'", Boolean.class);

        assertThat(admin).isFalse();
    }

    private void insertRequest(
        String owner,
        String type,
        String status,
        LocalDate start,
        LocalDate end) {
        jdbc.update(
            "INSERT INTO time_off_requests (owner_id, type, start_date, end_date, status, created_at)"
                + " SELECT id, ?, ?, ?, ?, now() FROM users WHERE username = ?",
            type,
            start,
            end,
            status,
            owner);
    }

    private User user(String username) {
        return userRepository.findByUsername(username).orElseThrow();
    }

    private Team team(String name) {
        return teamRepository.findAll().stream()
            .filter(t -> t.getName().equals(name))
            .findFirst()
            .orElseThrow();
    }

    private List<String> memberNames(String teamName) {
        return team(teamName).getMembers().stream()
            .map(User::getUsername)
            .toList();
    }
}
