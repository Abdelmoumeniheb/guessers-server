package dev.m2t.guessers.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import dev.m2t.guessers.model.enums.EventGuessOptionCaseStatusEnum;
import jakarta.persistence.*;

import java.time.LocalDateTime;

@Entity
public class EventGuessOptionCase {
    @Id
    @GeneratedValue
    private Long id;

    private String name;

    @ManyToOne
    @JoinColumn(name = "event_guess_option_id", nullable = false)
    @JsonIgnore
    private EventGuessOption eventGuessOption;

    @Enumerated(EnumType.STRING)
    private EventGuessOptionCaseStatusEnum status = EventGuessOptionCaseStatusEnum.IN_PROGRESS;

    private LocalDateTime createdOn = LocalDateTime.now();

    public EventGuessOptionCase() {

    }

    public EventGuessOptionCase(String name, EventGuessOption eventGuessOption) {
        this.name = name;
        this.eventGuessOption = eventGuessOption;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public EventGuessOption getEventGuessOption() {
        return eventGuessOption;
    }

    public void setEventGuessOption(EventGuessOption eventGuessOption) {
        this.eventGuessOption = eventGuessOption;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    public EventGuessOptionCaseStatusEnum getStatus() {
        return status;
    }

    public void setStatus(EventGuessOptionCaseStatusEnum status) {
        this.status = status;
    }

}
