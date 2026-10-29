package com.example.timeoff.service;

import java.time.Clock;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

// "Today" for every rule comes from this clock, in the zone set by TZ (D8); tests replace it with a fixed clock.
@Configuration
class ClockConfig {

    @Bean
    Clock clock() {
        return Clock.systemDefaultZone();
    }
}
