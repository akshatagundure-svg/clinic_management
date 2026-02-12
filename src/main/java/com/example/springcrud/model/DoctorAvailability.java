package com.example.springcrud.model;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document(collection = "doctor_availability")
@JsonIgnoreProperties(ignoreUnknown = true)
public class DoctorAvailability {

    @Id
    private String id; // MongoDB internal ID

    private String doctorId; // Business ID (e.g., "DOC102")
    private String day;      // e.g., "Thursday"
    private String startTime; // e.g., "15:00"
    private String endTime;   // e.g., "19:00"
    private boolean available;

    public DoctorAvailability() {}

    // -------- Getters & Setters --------

    public String getId() {
        return id;
    }

    public void setId(String id) {
        this.id = id;
    }

    public String getDoctorId() {
        return doctorId;
    }

    public void setDoctorId(String doctorId) {
        this.doctorId = doctorId;
    }

    public String getDay() {
        return day;
    }

    public void setDay(String day) {
        this.day = day;
    }

    public String getStartTime() {
        return startTime;
    }

    public void setStartTime(String startTime) {
        this.startTime = startTime;
    }

    public String getEndTime() {
        return endTime;
    }

    public void setEndTime(String endTime) {
        this.endTime = endTime;
    }

    public boolean isAvailable() {
        return available;
    }

    public void setAvailable(boolean available) {
        this.available = available;
    }
}