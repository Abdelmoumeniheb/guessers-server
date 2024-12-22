package dev.m2t.guessers.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

@Entity
public class Room {
    @Id
    @GeneratedValue
    private Long id;
    private String name;

    @ManyToOne
    @JoinColumn(name = "owner_id", nullable = false)
    private User owner;
    private boolean isPublic;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<RoomInvite> invites;

    @OneToMany(mappedBy = "room", cascade = CascadeType.ALL, orphanRemoval = true)
    @JsonIgnore
    private List<RoomUser> roomUsers = new ArrayList<>();

    @Transient
    private int memberCount;

    public int getMemberCount() {
        return roomUsers != null ? roomUsers.size() : 0;
    }

    public void setMemberCount(int memberCount) {
        this.memberCount = memberCount;
    }

    private LocalDateTime createdOn = LocalDateTime.now();

    public Room() {

    }

    public Room(String name, User owner, boolean isPublic) {
        this.name = name;
        this.owner = owner;
        this.isPublic = isPublic;
        this.invites = new ArrayList<>();
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

    public User getOwner() {
        return owner;
    }

    public void setOwner(User owner) {
        this.owner = owner;
    }

    public boolean isPublic() {
        return isPublic;
    }

    public void setPublic(boolean aPublic) {
        isPublic = aPublic;
    }

    public List<RoomInvite> getInvites() {
        return invites;
    }

    public void setInvites(List<RoomInvite> invites) {
        this.invites = invites;
    }

    public List<RoomUser> getRoomUsers() {
        return roomUsers;
    }

    public void setRoomUsers(List<RoomUser> roomUsers) {
        this.roomUsers = roomUsers;
    }
}
