package org.event.demo.Service;

import org.event.demo.Dto.EventRequest;
import org.event.demo.Dto.EventResponse;

import java.util.List;

public interface EventService {
    EventResponse create(EventRequest req);
    List<EventResponse> listAll();
    EventResponse getById(Long id);
    EventResponse update(Long id, EventRequest req);

    void delete(Long id);


}
