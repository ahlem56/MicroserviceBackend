//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.event.demo.Controller;

import java.util.List;
import org.event.demo.Dto.EventRequest;
import org.event.demo.Dto.EventResponse;
import org.event.demo.Service.EventService;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping({"/events"})
public class EventController {
    private final EventService service;
    @Value("${welcome.message}")
    private String welcomeMessage;

    public EventController(EventService service) {
        this.service = service;
    }

    @PostMapping
    public EventResponse create(@RequestBody EventRequest req) {
        return this.service.create(req);
    }

    @GetMapping
    public List<EventResponse> list() {
        return this.service.listAll();
    }

    @GetMapping({"/welcome"})
    public String welcome() {
        return this.welcomeMessage;
    }

    @GetMapping({"/{id}"})
    public EventResponse getById(@PathVariable Long id) {
        return this.service.getById(id);
    }

    @PutMapping({"/{id}"})
    public EventResponse update(@PathVariable Long id, @RequestBody EventRequest req) {
        return this.service.update(id, req);
    }

    @DeleteMapping({"/{id}"})
    public void delete(@PathVariable Long id) {
        this.service.delete(id);
    }
}
