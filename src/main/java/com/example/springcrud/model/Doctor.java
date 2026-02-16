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
    private String email;
    
    // NEW FIELD
    private String password;
    
    private Double consultationFee;
    private String availability;
    private String hospitalName;
    private String address;

    // Default Constructor
    public Doctor() {}

    // Updated Parameterized Constructor (Includes password)
    public Doctor(String name, String specialization, Integer experience, List<String> qualification, 
                  String gender, String phone, String email, String password, Double consultationFee, 
                  String availability, String hospitalName, String address) {
        this.name = name;
        this.specialization = specialization;
        this.experience = experience;
        this.qualification = qualification;
        this.gender = gender;
        this.phone = phone;
        this.email = email;
        this.password = password;
        this.consultationFee = consultationFee;
        this.availability = availability;
        this.hospitalName = hospitalName;
        this.address = address;
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

    public String getEmail() { return email; }
    public void setEmail(String email) { this.email = email; }

    // Getter & Setter for Password
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public Double getConsultationFee() { return consultationFee; }
    public void setConsultationFee(Double consultationFee) { this.consultationFee = consultationFee; }

    public String getAvailability() { return availability; }
    public void setAvailability(String availability) { this.availability = availability; }

    public String getHospitalName() { return hospitalName; }
    public void setHospitalName(String hospitalName) { this.hospitalName = hospitalName; }

    public String getAddress() { return address; }
    public void setAddress(String address) { this.address = address; }

    @Override
    public String toString() {
        return "Doctor{" +
                "id='" + id + '\'' +
                ", name='" + name + '\'' +
                ", specialization='" + specialization + '\'' +
                ", hospitalName='" + hospitalName + '\'' +
                ", email='" + email + '\'' +
                '}';
    }
}