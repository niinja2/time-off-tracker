package com.example.timeoff.domain;

import java.util.List;
import org.springframework.data.jpa.repository.JpaRepository;

public interface TimeOffRequestRepository extends JpaRepository<TimeOffRequest, Long> {

    List<TimeOffRequest> findByOwner(User owner);

    List<TimeOffRequest> findByStatus(Status status);
}
