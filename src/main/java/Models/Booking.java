package Models;

import java.time.LocalDateTime;

public class Booking {
    private int booking_id;
    private Event event;
    private Location location;
    private LocalDateTime start_date;
    private LocalDateTime end_date;

    public Booking() {}

    public Booking(int booking_id, Event event, Location location, 
                  LocalDateTime start_date, LocalDateTime end_date) {
        this.booking_id = booking_id;
        this.event = event;
        this.location = location;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public Booking(Event event, Location location, 
                  LocalDateTime start_date, LocalDateTime end_date) {
        this.event = event;
        this.location = location;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public int getBooking_id() {
        return booking_id;
    }

    public void setBooking_id(int booking_id) {
        this.booking_id = booking_id;
    }

    public Event getEvent() {
        return event;
    }

    public void setEvent(Event event) {
        this.event = event;
    }

    public Location getLocation() {
        return location;
    }

    public void setLocation(Location location) {
        this.location = location;
    }

    public int getEvent_id() {
        return event != null ? event.getId_event() : 0;
    }

    public void setEvent_id(int event_id) {
        if (this.event == null) {
            this.event = new Event();
        }
        this.event.setId_event(event_id);
    }

    public int getLocation_id() {
        return location != null ? location.getId_location() : 0;
    }

    public void setLocation_id(int location_id) {
        if (this.location == null) {
            this.location = new Location();
        }
        this.location.setId_location(location_id);
    }

    public LocalDateTime getStart_date() {
        return start_date;
    }

    public void setStart_date(LocalDateTime start_date) {
        this.start_date = start_date;
    }

    public LocalDateTime getEnd_date() {
        return end_date;
    }

    public void setEnd_date(LocalDateTime end_date) {
        this.end_date = end_date;
    }

    @Override
    public String toString() {
        return "Booking{" +
                "booking_id=" + booking_id +
                ", event=" + event +
                ", location=" + location +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                '}';
    }
} 