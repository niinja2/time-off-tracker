package com.example.timeoff.domain;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.time.LocalDate;
import java.util.HashSet;
import java.util.Set;

@Entity
@Table(name = "users")
public class User {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    private String username;

    private String name;

    private String passwordHash;

    private boolean admin;

    private LocalDate hireDate;

    @ManyToMany(mappedBy = "members")
    private Set<Team> teams = new HashSet<>();

    // Team lead is not a stored role (Q2): a user is a lead if they lead at least one team.
    @OneToMany(mappedBy = "lead")
    private Set<Team> ledTeams = new HashSet<>();

    protected User() {
    }

    public User(
            String username,
            String name,
            String passwordHash,
            boolean admin,
            LocalDate hireDate
    ) {
        this.username = username;
        this.name = name;
        this.passwordHash = passwordHash;
        this.admin = admin;
        this.hireDate = hireDate;
    }

    public boolean isAdmin() {
        return admin;
    }

    public boolean leads(Team team) {
        return team.getLead() == this;
    }

    public boolean isMemberOf(Team team) {
        return teams.contains(team);
    }

    public boolean isTeamLead() {
        return !ledTeams.isEmpty();
    }

    public Long getId() {
        return id;
    }

    public String getUsername() {
        return username;
    }

    public String getName() {
        return name;
    }

    public String getPasswordHash() {
        return passwordHash;
    }

    public LocalDate getHireDate() {
        return hireDate;
    }

    public Set<Team> getTeams() {
        return teams;
    }
}
