package dev.m2t.guessers.service;

import dev.m2t.guessers.dto.BaseResponse;
import dev.m2t.guessers.dto.request.CreateGuessPaperRequest;
import dev.m2t.guessers.exception.ResourceNotFoundException;
import dev.m2t.guessers.exception.UnauthorizedException;
import dev.m2t.guessers.model.*;
import dev.m2t.guessers.model.enums.EventGuessOptionCaseStatusEnum;
import dev.m2t.guessers.model.enums.EventStatusEnum;
import dev.m2t.guessers.model.enums.GuessPaperStatusEnum;
import dev.m2t.guessers.repository.*;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.stream.Collectors;

@Service
public class GuessPaperService {
    private final GuessPaperRepository guessPaperRepository;
    private final GuessPaperPagingRepository guessPaperPagingRepository;
    private final UserRepository userRepository;
    private final RoomRepository roomRepository;
    private final EventRepository eventRepository;
    private final RoomUserRepository roomUserRepository;

    private static final Logger logger = LoggerFactory.getLogger(GuessPaperService.class);

    public GuessPaperService(GuessPaperRepository guessPaperRepository, GuessPaperPagingRepository guessPaperPagingRepository, UserRepository userRepository, RoomRepository roomRepository, EventRepository eventRepository, RoomUserRepository roomUserRepository) {
        this.guessPaperRepository = guessPaperRepository;
        this.guessPaperPagingRepository = guessPaperPagingRepository;
        this.userRepository = userRepository;
        this.roomRepository = roomRepository;
        this.eventRepository = eventRepository;
        this.roomUserRepository = roomUserRepository;
    }

    @Scheduled(fixedRate = 60000)
    @Transactional
    public void checkGuessPapers() {
        logger.info("Periodic guess paper check starting.");
        List<GuessPaper> guessPapers = guessPaperRepository.findAllByStatus(GuessPaperStatusEnum.IN_PROGRESS);
        guessPapers.forEach(guessPaper -> {
            if(guessPaper.getGuesses().stream().anyMatch(singleGuess -> singleGuess.getEventGuessOptionCase().getStatus().equals(EventGuessOptionCaseStatusEnum.LOST))) {
                guessPaper.setStatus(GuessPaperStatusEnum.LOST);
            } else if(guessPaper.getGuesses().stream().anyMatch(singleGuess -> singleGuess.getEventGuessOptionCase().getStatus().equals(EventGuessOptionCaseStatusEnum.CANCELLED))) {
                // If any of the guesses is cancelled, the whole guess paper is cancelled
                // This part may change in the future.
                guessPaper.setStatus(GuessPaperStatusEnum.CANCELLED);
            } else if(guessPaper.getGuesses().stream().anyMatch(singleGuess -> singleGuess.getEventGuessOptionCase().getStatus().equals(EventGuessOptionCaseStatusEnum.IN_PROGRESS))) {
                guessPaper.setStatus(GuessPaperStatusEnum.IN_PROGRESS);
            } else if(guessPaper.getGuesses().stream().allMatch(singleGuess -> singleGuess.getEventGuessOptionCase().getStatus().equals(EventGuessOptionCaseStatusEnum.WON))) {
                guessPaper.setStatus(GuessPaperStatusEnum.WON);
                roomUserRepository.findByRoomAndUser(guessPaper.getRoom(), guessPaper.getUser()).ifPresent(roomUser -> {
                    roomUserRepository.save(roomUser);
                });
            }
        });
        guessPaperRepository.saveAll(guessPapers);
        logger.info("Periodic guess paper check completed.");
    }


    synchronized public BaseResponse createGuessPaper(CreateGuessPaperRequest createGuessPaperRequest, String username) {
        logger.info("Creating guess paper for user {}.", username);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Room room = roomRepository.findById(createGuessPaperRequest.getRoomId()).orElseThrow(() -> new ResourceNotFoundException("Room", "id", createGuessPaperRequest.getRoomId()));

        RoomUser ru = roomUserRepository.findByRoomAndUser(room, user).orElseThrow(() -> new UnauthorizedException("You are not one of the members of this room. Only the members can create guess papers."));

        GuessPaper guessPaper = new GuessPaper();
        guessPaper.setUser(user);
        guessPaper.setRoom(room);
        guessPaper.setStatus(GuessPaperStatusEnum.IN_PROGRESS);

        // Map SingleGuessDto to SingleGuess and add to GuessPaper
        List<SingleGuess> singleGuesses = createGuessPaperRequest.getGuesses().stream().map(singleGuessDto -> {
            SingleGuess singleGuess = new SingleGuess();
            Event event = eventRepository.findById(singleGuessDto.getEventId()).orElseThrow(() -> new ResourceNotFoundException("Event", "id", singleGuessDto.getEventId()));
            if(event.getStatus() != EventStatusEnum.IN_PROGRESS) {
                throw new ResourceNotFoundException("Event", "status", EventStatusEnum.IN_PROGRESS);
            }
            EventGuessOption ego = event.getEventGuessOptions().stream().filter(eventGuessOption -> eventGuessOption.getId().equals(singleGuessDto.getEventGuessOptionId())).findFirst().orElseThrow(() -> new ResourceNotFoundException("Event Guess Option", "id", singleGuessDto.getEventGuessOptionId()));
            EventGuessOptionCase egoc = ego.getEventGuessOptionCases().stream().filter(eventGuessOptionCase -> eventGuessOptionCase.getId().equals(singleGuessDto.getEventGuessOptionCaseId())).findFirst().orElseThrow(() -> new ResourceNotFoundException("Event Guess Option", "id", singleGuessDto.getEventGuessOptionCaseId()));

            singleGuess.setEvent(event);
            singleGuess.setEventGuessOption(ego);
            singleGuess.setEventGuessOptionCase(egoc);
            singleGuess.setGuessPaper(guessPaper); // Associate guess with guess paper
            return singleGuess;
        }).collect(Collectors.toList());
        guessPaper.getGuesses().addAll(singleGuesses);

        logger.info("Saving guess paper for user {}.", username);
        GuessPaper savedGuessPaper = guessPaperRepository.save(guessPaper);

        roomUserRepository.save(ru);
        logger.info("Guess paper with id {} created successfully.", savedGuessPaper.getId());

        return new BaseResponse("Guess paper created successfully.", true, true, savedGuessPaper);
    }

    public BaseResponse listSelfGuessPapers(String username, Pageable pageable) {
        logger.info("Listing guess papers for user {}.", username);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        return new BaseResponse("Guess papers retrieved successfully.", true, false, guessPaperPagingRepository.findAllByUser(user, pageable));
    }

    public BaseResponse listRoomGuessPapers(String username, Long roomId, Pageable pageable) {
        logger.info("Listing room guess papers for user {}.", username);
        User user = userRepository.findByUsername(username).orElseThrow(() -> new ResourceNotFoundException("User", "username", username));
        Room room = roomRepository.findById(roomId).orElseThrow(() -> new ResourceNotFoundException("Room", "id", roomId));
        if (room.getRoomUsers().stream().noneMatch(roomUser -> roomUser.getUser().equals(user))) {
            throw new UnauthorizedException("You are not one of the members of this room. Only the members can list events.");
        }

        Page<GuessPaper> roomGuessPapers = guessPaperPagingRepository.findAllByRoom(room, pageable);
        return new BaseResponse("Guess papers retrieved successfully.", true, false, roomGuessPapers);
    }
}
