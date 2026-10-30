package com.example.timeoff.service;

import com.example.timeoff.domain.User;
import java.util.List;
import java.util.Objects;
import java.util.function.BiPredicate;
import org.springframework.stereotype.Component;

// How much a viewer sees of an owner's requests (interface T7, Vidljivost). Rules are read in order; the first that applies decides.
// The only place for the visibility rule: requests, balances and the calendar all use it.
@Component
public class VisibilityPolicy {

    private record Rule(BiPredicate<User, User> applies, Access access) {
    }

    private static final List<Rule> RULES = List.of(
            new Rule(VisibilityPolicy::isSelf, Access.FULL),                 // brief, Q41, Q44-Q46
            new Rule((viewer, owner) -> viewer.isAdmin(), Access.FULL),      // brief, Q17, Q43
            new Rule(VisibilityPolicy::leadsTeamOfOwner, Access.FULL),       // brief, Q16, Q42
            new Rule(VisibilityPolicy::sharesTeam, Access.LIMITED)           // brief, Q47
    );

    // Anything no rule allows is hidden, e.g. an owner without a team (specification 7).
    public Access access(User viewer, User owner) {
        return RULES.stream()
                .filter(rule -> rule.applies().test(viewer, owner))
                .findFirst()
                .map(Rule::access)
                .orElse(Access.NONE);
    }

    private static boolean isSelf(User viewer, User owner) {
        return Objects.equals(viewer.getId(), owner.getId());
    }

    private static boolean leadsTeamOfOwner(User viewer, User owner) {
        return owner.getTeams()
                .stream()
                .anyMatch(viewer::leads);
    }

    private static boolean sharesTeam(User viewer, User owner) {
        return owner.getTeams()
                .stream()
                .anyMatch(viewer::isMemberOf);
    }
}
