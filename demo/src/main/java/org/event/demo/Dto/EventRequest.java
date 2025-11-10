//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.event.demo.Dto;

import java.time.LocalDateTime;
import java.util.Objects;

public class EventRequest {

    private String title;
    private String description;
    private String location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean published;
    private String photo;

    public EventRequest() {
    }

    public EventRequest(String title, String description, String location, LocalDateTime startDate, LocalDateTime endDate, boolean published, String photo) {
        this.title = title;
        this.description = description;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.published = published;
        this.photo = photo;
    }

    public String getTitle() {
        return title;
    }

    public void setTitle(String title) {
        this.title = title;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public String getLocation() {
        return location;
    }

    public void setLocation(String location) {
        this.location = location;
    }

    public LocalDateTime getStartDate() {
        return startDate;
    }

    public void setStartDate(LocalDateTime startDate) {
        this.startDate = startDate;
    }

    public LocalDateTime getEndDate() {
        return endDate;
    }

    public void setEndDate(LocalDateTime endDate) {
        this.endDate = endDate;
    }

    public boolean isPublished() {
        return published;
    }

    public void setPublished(boolean published) {
        this.published = published;
    }

    public String getPhoto() {
        return photo;
    }

    public void setPhoto(String photo) {
        this.photo = photo;
    }

    @Override
    public boolean equals(Object o) {
        if (this == o) {
            return true;
        }
        if (!(o instanceof EventRequest)) {
            return false;
        }
        EventRequest that = (EventRequest) o;
        return published == that.published
            && Objects.equals(title, that.title)
            && Objects.equals(description, that.description)
            && Objects.equals(location, that.location)
            && Objects.equals(startDate, that.startDate)
            && Objects.equals(endDate, that.endDate)
            && Objects.equals(photo, that.photo);
    }

    @Override
    public int hashCode() {
        return Objects.hash(title, description, location, startDate, endDate, published, photo);
    }

    @Override
    public String toString() {
        return "EventRequest{" +
            "title='" + title + '\'' +
            ", description='" + description + '\'' +
            ", location='" + location + '\'' +
            ", startDate=" + startDate +
            ", endDate=" + endDate +
            ", published=" + published +
            ", photo='" + photo + '\'' +
            '}';
    }
}
