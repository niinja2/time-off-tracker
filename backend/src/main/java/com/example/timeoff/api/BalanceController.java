package com.example.timeoff.api;

import com.example.timeoff.security.CurrentUser;
import com.example.timeoff.service.Balance;
import com.example.timeoff.service.BalanceService;
import java.time.Clock;
import java.time.LocalDate;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/api")
class BalanceController {

    private final BalanceService balanceService;
    private final CurrentUser currentUser;
    private final Clock clock;

    BalanceController(
            BalanceService balanceService,
            CurrentUser currentUser,
            Clock clock
    ) {
        this.balanceService = balanceService;
        this.currentUser = currentUser;
        this.clock = clock;
    }

    // Own balance for the current year (R12); the date is taken in the clock's zone (D8, L18).
    @GetMapping("/me/balance")
    Balance myBalance() {
        Long userId = currentUser.id();
        LocalDate today = LocalDate.now(clock);

        return balanceService.balance(userId, today.getYear());
    }
}
