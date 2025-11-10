//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.event.demo.Model;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Lob;
import jakarta.persistence.Table;
import java.time.LocalDateTime;

@Entity
@Table(
        name = "event"
)
public class Event {
    @Id
    @GeneratedValue(
            strategy = GenerationType.IDENTITY
    )
    private Long id;
    private String title;
    @Column(
            length = 500
    )
    private String description;
    private String location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean published;
    @Lob
    @Column(
            columnDefinition = "TEXT"
    )
    private String photo;

    public static EventBuilder builder() {
        return new EventBuilder();
    }

    public Long getId() {
        return this.id;
    }

    public String getTitle() {
        return this.title;
    }

    public String getDescription() {
        return this.description;
    }

    public String getLocation() {
        return this.location;
    }

    public LocalDateTime getStartDate() {
        return this.startDate;
    }

    public LocalDateTime getEndDate() {
        return this.endDate;
    }

    public boolean isPublished() {
        return this.published;
    }

    public String getPhoto() {
        return this.photo;
    }

    public void setId(final Long id) {
        this.id = id;
    }

    public void setTitle(final String title) {
        this.title = title;
    }

    public void setDescription(final String description) {
        this.description = description;
    }

    public void setLocation(final String location) {
        this.location = location;
    }

    public void setStartDate(final LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public void setEndDate(final LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public void setPublished(final boolean published) {
        this.published = published;
    }

    public void setPhoto(final String photo) {
        this.photo = photo;
    }

    public Event() {
    }

    public Event(final Long id, final String title, final String description, final String location, final LocalDateTime startDate, final LocalDateTime endDate, final boolean published, final String photo) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.published = published;
        this.photo = photo;
    }

    public static class EventBuilder {
        private Long id;
        private String title;
        private String description;
        private String location;
        private LocalDateTime startDate;
        private LocalDateTime endDate;
        private boolean published;
        private String photo;

        EventBuilder() {
        }

        public EventBuilder id(final Long id) {
            this.id = id;
            return this;
        }

        public EventBuilder title(final String title) {
            this.title = title;
            return this;
        }

        public EventBuilder description(final String description) {
            this.description = description;
            return this;
        }

        public EventBuilder location(final String location) {
            this.location = location;
            return this;
        }

        public EventBuilder startDate(final LocalDateTime startDate) {
            this.startDate = startDate;
            return this;
        }

        public EventBuilder endDate(final LocalDateTime endDate) {
            this.endDate = endDate;
            return this;
        }

        public EventBuilder published(final boolean published) {
            this.published = published;
            return this;
        }

        public EventBuilder photo(final String photo) {
            this.photo = photo;
            return this;
        }

        public Event build() {
            return new Event(this.id, this.title, this.description, this.location, this.startDate, this.endDate, this.published, this.photo);
        }

        public String toString() {
            return "Event.EventBuilder(id=" + this.id + ", title=" + this.title + ", description=" + this.description + ", location=" + this.location + ", startDate=" + this.startDate + ", endDate=" + this.endDate + ", published=" + this.published + ", photo=" + this.photo + ")";
        }
    }
}
