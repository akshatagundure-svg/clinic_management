package com.example.springcrud.model;

import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document(collection = "doctors")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Doctor {

    @Id
    private String id; 

    private String name;
    private String specialization;
    private Integer experience;
    private List<String> qualification;
    private String gender;
    private String phone;

    // Default Constructor
    public Doctor() {}

    // Parameterized Constructor for convenience
    public Doctor(String name, String specialization, Integer experience, List<String> qualification, String gender, String phone) {
        this.name = name;
        this.specialization = specialization;
        this.experience = experience;
        this.qualification = qualification;
        this.gender = gender;
        this.phone = phone;
    }

    // -------- Getters & Setters --------

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getSpecialization() { return specialization; }
    public void setSpecialization(String specialization) { this.specialization = specialization; }

    public Integer getExperience() { return experience; }
    public void setExperience(Integer experience) { this.experience = experience; }

    public List<String> getQualification() { return qualification; }
    public void setQualification(List<String> qualification) { this.qualification = qualification; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    @Override
    public String toString() {
        return "Doctor{" + "id='" + id + '\'' + ", name='" + name + '\'' + ", spec='" + specialization + '\'' + '}';
    }
}