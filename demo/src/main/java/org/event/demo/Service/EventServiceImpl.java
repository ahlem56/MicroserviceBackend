//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.event.demo.Service;

import java.util.List;
import org.event.demo.Dto.EventRequest;
import org.event.demo.Dto.EventResponse;
import org.event.demo.Model.Event;
import org.event.demo.Repository.EventRepository;
import org.springframework.stereotype.Service;

@Service
public class EventServiceImpl implements EventService {
    private final EventRepository repo;

    public EventServiceImpl(EventRepository repo) {
        this.repo = repo;
    }

    public EventResponse create(EventRequest req) {
        Event event = new Event();
        event.setTitle(req.getTitle());
        event.setDescription(req.getDescription());
        event.setLocation(req.getLocation());
        event.setStartDate(req.getStartDate());
        event.setEndDate(req.getEndDate());
        event.setPublished(req.isPublished());
        event.setPhoto(req.getPhoto());
        Event saved = (Event)this.repo.save(event);
        return this.mapToResponse(saved);
    }

    public List<EventResponse> listAll() {
        return this.repo.findAll().stream().map(this::mapToResponse).toList();
    }

    public EventResponse getById(Long id) {
        Event event = (Event)this.repo.findById(id).orElseThrow(() -> {
            return new RuntimeException("Event not found with id: " + id);
        });
        return this.mapToResponse(event);
    }

    public EventResponse update(Long id, EventRequest req) {
        Event existing = (Event)this.repo.findById(id).orElseThrow(() -> {
            return new RuntimeException("Event not found with id: " + id);
        });
        existing.setTitle(req.getTitle());
        existing.setDescription(req.getDescription());
        existing.setLocation(req.getLocation());
        existing.setStartDate(req.getStartDate());
        existing.setEndDate(req.getEndDate());
        existing.setPublished(req.isPublished());
        existing.setPhoto(req.getPhoto());
        Event updated = (Event)this.repo.save(existing);
        return this.mapToResponse(updated);
    }

    public void delete(Long id) {
        if (!this.repo.existsById(id)) {
            throw new RuntimeException("Event not found with id: " + id);
        } else {
            this.repo.deleteById(id);
        }
    }

    private EventResponse mapToResponse(Event e) {
        return new EventResponse(e.getId(), e.getTitle(), e.getDescription(), e.getLocation(), e.getStartDate(), e.getEndDate(), e.isPublished(), e.getPhoto());
    }
}
