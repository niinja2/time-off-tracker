package com.example.timeoff.service;

import com.example.timeoff.domain.TimeOffRequest;
import com.example.timeoff.domain.User;
import java.util.List;
import java.util.Optional;
import java.util.function.BiPredicate;
import org.springframework.stereotype.Component;

// Who may cancel a request and whether a reason is needed (interface T6, Ovlasti). Rules are read in order;
// the first that applies decides.
@Component
public class CancelPolicy {

    private record Rule(BiPredicate<User, TimeOffRequest> applies, boolean reasonRequired) {
    }

    private final List<Rule> rules;

    CancelPolicy(ApprovalPolicy approvalPolicy) {
        this.rules = List.of(
                new Rule((actor, request) -> request.isSubmittedBy(actor), false),  // R6, R7
                new Rule(approvalPolicy::canDecide, true)                         // Q30, Q31, Q32, D3, D14
        );
    }

    // Anything no rule allows is forbidden (D14).
    public boolean canCancel(User actor, TimeOffRequest request) {
        return firstRule(actor, request).isPresent();
    }

    public boolean reasonRequired(User actor, TimeOffRequest request) {
        return firstRule(actor, request)
                .map(Rule::reasonRequired)
                .orElse(false);
    }

    private Optional<Rule> firstRule(User actor, TimeOffRequest request) {
        return rules.stream()
                .filter(rule -> rule.applies().test(actor, request))
                .findFirst();
    }
}
