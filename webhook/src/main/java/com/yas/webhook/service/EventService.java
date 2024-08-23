package com.yas.webhook.service;

import com.yas.webhook.model.Event;
import com.yas.webhook.model.mapper.EventMapper;
import com.yas.webhook.model.viewmodel.webhook.EventVm;
import com.yas.webhook.repository.EventRepository;
import java.util.List;
import lombok.RequiredArgsConstructor;
import org.springframework.data.domain.Sort;
import org.springframework.stereotype.Service;

@Service
@RequiredArgsConstructor
public class EventService {

    private final EventRepository eventRepository;
    private final EventMapper eventMapper;

    public List<EventVm> findAllEvents() {
        List<Event> events = eventRepository.findAll(Sort.by(Sort.Direction.DESC, "id"));
        return events.stream().map(eventMapper::toEventVm).toList();
    }

}
