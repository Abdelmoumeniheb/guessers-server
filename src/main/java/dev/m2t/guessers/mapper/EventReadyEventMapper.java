package dev.m2t.guessers.mapper;

import dev.m2t.guessers.model.*;
import dev.m2t.guessers.model.enums.EventStatusEnum;
import org.springframework.stereotype.Component;

@Component
public class EventReadyEventMapper {

    public Event toEvent(ReadyEvent readyEvent) {
        Event event = new Event();
        event.setName(readyEvent.getName());
        event.setStatus(EventStatusEnum.IN_PROGRESS);
        event.setEventTime(readyEvent.getCommenceTime());
        for(ReadyEventOption reo: readyEvent.getReadyEventOptions()) {
            EventGuessOption eventGuessOption = new EventGuessOption();
            eventGuessOption.setName(reo.getName());
            eventGuessOption.setPrecedence(reo.getPrecedence());

            for(ReadyEventOptionCase reoc: reo.getReadyEventOptionCases()) {
                EventGuessOptionCase eventGuessOptionCase = new EventGuessOptionCase();
                eventGuessOptionCase.setName(reoc.getName());
                eventGuessOption.addEventGuessOptionCase(eventGuessOptionCase);
            }

            event.addEventGuessOption(eventGuessOption);

        }

        return event;
    }
}
