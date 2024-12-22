package dev.m2t.guessers.dto.request;

import jakarta.validation.constraints.NotNull;

public class CreateRoomRequest {
    @NotNull(message = "Name cannot be null")
    private String name;

    private Boolean publico;

    public CreateRoomRequest() {
    }

    public CreateRoomRequest(String name, Boolean publico) {
        this.name = name;
        this.publico = publico;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public Boolean getPublico() {
        return publico;
    }

    public void setPublico(Boolean publico) {
        this.publico = publico;
    }
}
