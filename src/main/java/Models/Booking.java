package Models;

import java.time.LocalDateTime;

public class Booking {
    private int Booking_id;
    private int event_id;
    private int location_id;
    private LocalDateTime start_date;
    private LocalDateTime end_date;

    public Booking() {}

    public Booking(int Booking_id, int event_id, int location_id, 
                  LocalDateTime start_date, LocalDateTime end_date) {
        this.Booking_id = Booking_id;
        this.event_id = event_id;
        this.location_id = location_id;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public Booking(int event_id, int location_id, 
                  LocalDateTime start_date, LocalDateTime end_date) {
        this.event_id = event_id;
        this.location_id = location_id;
        this.start_date = start_date;
        this.end_date = end_date;
    }

    public int getBooking_id() {
        return Booking_id;
    }

    public void setBooking_id(int Booking_id) {
        this.Booking_id = Booking_id;
    }

    public int getEvent_id() {
        return event_id;
    }

    public void setEvent_id(int event_id) {
        this.event_id = event_id;
    }

    public int getLocation_id() {
        return location_id;
    }

    public void setLocation_id(int location_id) {
        this.location_id = location_id;
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
                "Booking_id=" + Booking_id +
                ", event_id=" + event_id +
                ", location_id=" + location_id +
                ", start_date=" + start_date +
                ", end_date=" + end_date +
                '}';
    }
} 