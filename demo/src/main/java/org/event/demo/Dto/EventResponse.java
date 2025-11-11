//
// Source code recreated from a .class file by IntelliJ IDEA
// (powered by FernFlower decompiler)
//

package org.event.demo.Dto;

import java.time.LocalDateTime;

public class EventResponse {
    private Long id;
    private String title;
    private String description;
    private String location;
    private LocalDateTime startDate;
    private LocalDateTime endDate;
    private boolean published;
    private String photo;

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

    public boolean equals(final Object o) {
        if (o == this) {
            return true;
        } else if (!(o instanceof EventResponse)) {
            return false;
        } else {
            EventResponse other = (EventResponse)o;
            if (!other.canEqual(this)) {
                return false;
            } else if (this.isPublished() != other.isPublished()) {
                return false;
            } else {
                label97: {
                    Object this$id = this.getId();
                    Object other$id = other.getId();
                    if (this$id == null) {
                        if (other$id == null) {
                            break label97;
                        }
                    } else if (this$id.equals(other$id)) {
                        break label97;
                    }

                    return false;
                }

                Object this$title = this.getTitle();
                Object other$title = other.getTitle();
                if (this$title == null) {
                    if (other$title != null) {
                        return false;
                    }
                } else if (!this$title.equals(other$title)) {
                    return false;
                }

                Object this$description = this.getDescription();
                Object other$description = other.getDescription();
                if (this$description == null) {
                    if (other$description != null) {
                        return false;
                    }
                } else if (!this$description.equals(other$description)) {
                    return false;
                }

                label76: {
                    Object this$location = this.getLocation();
                    Object other$location = other.getLocation();
                    if (this$location == null) {
                        if (other$location == null) {
                            break label76;
                        }
                    } else if (this$location.equals(other$location)) {
                        break label76;
                    }

                    return false;
                }

                Object this$startDate = this.getStartDate();
                Object other$startDate = other.getStartDate();
                if (this$startDate == null) {
                    if (other$startDate != null) {
                        return false;
                    }
                } else if (!this$startDate.equals(other$startDate)) {
                    return false;
                }

                Object this$endDate = this.getEndDate();
                Object other$endDate = other.getEndDate();
                if (this$endDate == null) {
                    if (other$endDate != null) {
                        return false;
                    }
                } else if (!this$endDate.equals(other$endDate)) {
                    return false;
                }

                Object this$photo = this.getPhoto();
                Object other$photo = other.getPhoto();
                if (this$photo == null) {
                    if (other$photo != null) {
                        return false;
                    }
                } else if (!this$photo.equals(other$photo)) {
                    return false;
                }

                return true;
            }
        }
    }

    protected boolean canEqual(final Object other) {
        return other instanceof EventResponse;
    }

    public int hashCode() {
        boolean PRIME = true;
        int result = 1;
        result = result * 59 + (this.isPublished() ? 79 : 97);
        Object $id = this.getId();
        result = result * 59 + ($id == null ? 43 : $id.hashCode());
        Object $title = this.getTitle();
        result = result * 59 + ($title == null ? 43 : $title.hashCode());
        Object $description = this.getDescription();
        result = result * 59 + ($description == null ? 43 : $description.hashCode());
        Object $location = this.getLocation();
        result = result * 59 + ($location == null ? 43 : $location.hashCode());
        Object $startDate = this.getStartDate();
        result = result * 59 + ($startDate == null ? 43 : $startDate.hashCode());
        Object $endDate = this.getEndDate();
        result = result * 59 + ($endDate == null ? 43 : $endDate.hashCode());
        Object $photo = this.getPhoto();
        result = result * 59 + ($photo == null ? 43 : $photo.hashCode());
        return result;
    }

    public String toString() {
        Long var10000 = this.getId();
        return "EventResponse(id=" + var10000 + ", title=" + this.getTitle() + ", description=" + this.getDescription() + ", location=" + this.getLocation() + ", startDate=" + this.getStartDate() + ", endDate=" + this.getEndDate() + ", published=" + this.isPublished() + ", photo=" + this.getPhoto() + ")";
    }

    public EventResponse(final Long id, final String title, final String description, final String location, final LocalDateTime startDate, final LocalDateTime endDate, final boolean published, final String photo) {
        this.id = id;
        this.title = title;
        this.description = description;
        this.location = location;
        this.startDate = startDate;
        this.endDate = endDate;
        this.published = published;
        this.photo = photo;
    }

    public EventResponse() {
    }
}
