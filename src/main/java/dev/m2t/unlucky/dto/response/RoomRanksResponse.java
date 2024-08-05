package dev.m2t.unlucky.dto.response;

import dev.m2t.unlucky.model.RoomUser;

import java.util.List;

public class RoomRanksResponse {
    private Long roomId;
    private String roomName;
    private int userCount;
    private List<RoomUser> rankedByCorrectPredictions;
    private List<RoomUser> rankedByBalance;

    public RoomRanksResponse() {
    }

    public RoomRanksResponse(Long roomId, String roomName, int userCount, List<RoomUser> rankedByCorrectPredictions, List<RoomUser> rankedByBalance) {
        this.roomId = roomId;
        this.roomName = roomName;
        this.userCount = userCount;
        this.rankedByCorrectPredictions = rankedByCorrectPredictions;
        this.rankedByBalance = rankedByBalance;
    }

    public Long getRoomId() {
        return roomId;
    }

    public void setRoomId(Long roomId) {
        this.roomId = roomId;
    }

    public String getRoomName() {
        return roomName;
    }

    public void setRoomName(String roomName) {
        this.roomName = roomName;
    }

    public int getUserCount() {
        return userCount;
    }

    public void setUserCount(int userCount) {
        this.userCount = userCount;
    }

    public List<RoomUser> getRankedByCorrectPredictions() {
        return rankedByCorrectPredictions;
    }

    public void setRankedByCorrectPredictions(List<RoomUser> rankedByCorrectPredictions) {
        this.rankedByCorrectPredictions = rankedByCorrectPredictions;
    }

    public List<RoomUser> getRankedByBalance() {
        return rankedByBalance;
    }

    public void setRankedByBalance(List<RoomUser> rankedByBalance) {
        this.rankedByBalance = rankedByBalance;
    }
}
