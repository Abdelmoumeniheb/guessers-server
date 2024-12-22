package dev.m2t.guessers.model;

import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.Objects;

@Entity
public class RoomUser {
    @Id
    @GeneratedValue
    private Long id;

    @ManyToOne
    @JoinColumn(name = "room_id", nullable = false)
    private Room room;

    @ManyToOne
    @JoinColumn(name = "user_id", nullable = false)
    private User user;

    private Boolean isOwner;
    private Integer score;

    @Transient
    private int memberCount;

    public int getMemberCount() {
        return room != null ? room.getRoomUsers().size() : 0;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    private LocalDateTime createdOn = LocalDateTime.now();

    public RoomUser() {

    }

    public RoomUser(Room room, User user, Boolean isOwner, Integer score) {
        this.room = room;
        this.user = user;
        this.isOwner = isOwner;
        this.score = score;
    }

    public Long getId() {
        return id;
    }

    public void setId(Long id) {
        this.id = id;
    }

    public Room getRoom() {
        return room;
    }

    public void setRoom(Room room) {
        this.room = room;
    }

    public User getUser() {
        return user;
    }

    public void setUser(User user) {
        this.user = user;
    }

    public Boolean getOwner() {
        return isOwner;
    }

    public void setOwner(Boolean owner) {
        isOwner = owner;
    }

    public Integer getScore() {
        return score;
    }

    public void setScore(Integer score) {
        this.score = score;
    }

    public LocalDateTime getCreatedOn() {
        return createdOn;
    }

    public void setCreatedOn(LocalDateTime createdOn) {
        this.createdOn = createdOn;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) return true;
        if (o == null || getClass() != o.getClass()) return false;
        RoomUser roomUser = (RoomUser) o;
        return Objects.equals(id, roomUser.id);
    }

    @Override
    public int hashCode() {
        return Objects.hash(id);
    }
}
