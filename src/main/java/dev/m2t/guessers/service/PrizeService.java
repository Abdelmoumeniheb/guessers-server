package dev.m2t.guessers.service;

import dev.m2t.guessers.dto.BaseResponse;
import dev.m2t.guessers.dto.request.CreatePrizeRequest;
import dev.m2t.guessers.exception.ResourceNotFoundException;
import dev.m2t.guessers.model.Prize;
import dev.m2t.guessers.model.Room;
import dev.m2t.guessers.model.RoomUser;
import dev.m2t.guessers.model.User;
import dev.m2t.guessers.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class PrizeService {
    private final PrizeRepository prizeRepository;
    private final PrizePagingRepository prizePagingRepository;
    private final RoomUserRepository roomUserRepository;
    private final RoomRepository roomRepository;
    private final UserRepository userRepository;
    private static final Logger logger = LoggerFactory.getLogger(PrizeService.class);

    public PrizeService(PrizeRepository prizeRepository, PrizePagingRepository prizePagingRepository, RoomUserRepository roomUserRepository, RoomRepository roomRepository, UserRepository userRepository) {
        this.prizeRepository = prizeRepository;
        this.prizePagingRepository = prizePagingRepository;
        this.roomUserRepository = roomUserRepository;
        this.roomRepository = roomRepository;
        this.userRepository = userRepository;
    }

    public BaseResponse createPrize(CreatePrizeRequest createPrizeRequest, String username, Long roomId) {
        logger.info("Creating prize for room {}, for amount {}", roomId, createPrizeRequest.getValue());
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));
        RoomUser roomUser = roomUserRepository.findByRoomAndUser(room, user).orElseThrow(() -> new ResourceNotFoundException("RoomUser", "room", room.getId().toString()));

        if(!roomUser.getOwner()) {
            return new BaseResponse("You are not the owner of the room", false, true);
        }

        Prize prize = new Prize();
        prize.setDescription(createPrizeRequest.getDescription());
        prize.setName(createPrizeRequest.getName());
        prize.setValue(createPrizeRequest.getValue());
        prize.setRoom(room);

        prizeRepository.save(prize);
        logger.info("Prize created successfully for room {}", roomId);

        return new BaseResponse("Prize created successfully", true, true);
    }

    public BaseResponse listPrizes(Pageable pageable, boolean active, Long roomId) {
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));

        Page<Prize> prizes = prizePagingRepository.findByRoomAndActive(room, active, pageable);

        return new BaseResponse("Prizes listed successfully", true, false, prizes);
    }

    public BaseResponse buyPrize(Long prizeId, String username) {
        logger.info("Buying prize with id {}", prizeId);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Prize prize = prizeRepository.findById(prizeId).orElseThrow(() -> new ResourceNotFoundException("Prize", "id", prizeId));
        Room room = prize.getRoom();
        RoomUser buyer = roomUserRepository.findByRoomAndUser(room, user).orElseThrow(() -> new ResourceNotFoundException("RoomUser", "room", room.getId().toString()));

        if(!prize.isActive()) {
            return new BaseResponse("Prize is not active. Thus cannot be bought.", false, true);
        }

        if(buyer.getScore() < prize.getValue()) {
            return new BaseResponse("You do not have enough balance.", false, true);
        }

        userRepository.save(user);

        prize.setActive(false);
        prize.setSoldTo(buyer);
        prizeRepository.save(prize);

        logger.info("Prize bought successfully with id {}", prizeId);

        return new BaseResponse("Prize bought successfully", true, false);
    }
}
