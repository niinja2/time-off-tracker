package com.example.timeoff.service;

import com.example.timeoff.domain.TimeOffRequest;
import com.example.timeoff.domain.User;
import java.util.List;
import java.util.function.BiPredicate;
import org.springframework.stereotype.Component;

// Who may approve or reject a request (interface T5, Ovlasti). Rules are read in order; the first that applies decides.
// Cancelling by someone other than the owner uses the same rule (D14, T6).
@Component
public class ApprovalPolicy {

    private record Rule(BiPredicate<User, TimeOffRequest> applies, boolean allowed) {
    }

    private static final List<Rule> RULES = List.of(
            new Rule((actor, request) -> actor.isAdmin(), true),                   // R2, Q1.1, D15
            new Rule((actor, request) -> request.isSubmittedBy(actor), false),     // R3
            new Rule((actor, request) -> request.getOwner().isTeamLead(), false),  // R3, D2
            new Rule(ApprovalPolicy::leadsTeamOfOwner, true)                       // R1, Q3, Q5.1
    );

    // Anything no rule allows is forbidden, e.g. an owner without a team goes to an admin (Q4).
    public boolean canDecide(User actor, TimeOffRequest request) {
        return RULES.stream()
                .filter(rule -> rule.applies().test(actor, request))
                .findFirst()
                .map(Rule::allowed)
                .orElse(false);
    }

    private static boolean leadsTeamOfOwner(User actor, TimeOffRequest request) {
        return request.getOwner()
                .getTeams()
                .stream()
                .anyMatch(actor::leads);
    }
}
