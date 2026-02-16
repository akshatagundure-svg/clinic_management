package com.example.springcrud.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat; // Added for field mapping
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document(collection = "patients")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Patient {

    @Id
    private String id; 

    private String patientId; 
    private String fullName;
    
    // NEW FIELD
    private String password;

    @JsonFormat(pattern = "yyyy-MM-dd")
    private LocalDate dateOfBirth;

    private String gender;

    // RENAMED FIELD
    // Use @Field if you want to keep the existing data in MongoDB named "phoneNumber"
    // @Field("phoneNumber") 
    private String phone;

    private String emailAddress;
    private String residentialAddress;
    private String emergencyContact;
    private String bloodGroup;

    private List<String> allergies = new ArrayList<>();
    private List<String> chronicDiseases = new ArrayList<>();
    private List<String> currentMedications = new ArrayList<>();

    private Double height;
    private Double weight;

    public Patient() {}

    // Dynamic Age
    public Integer getAge() {
        if (this.dateOfBirth != null) {
            return Period.between(this.dateOfBirth, LocalDate.now()).getYears();
        }
        return null;
    }

    // -------- Getters & Setters --------

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }

    public String getPatientId() { return patientId; }
    public void setPatientId(String patientId) { this.patientId = patientId; }

    public String getFullName() { 
        return fullName != null ? fullName : ""; 
    }
    public void setFullName(String fullName) { this.fullName = fullName; }

    // Getter & Setter for Password
    public String getPassword() { return password; }
    public void setPassword(String password) { this.password = password; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    // Updated Getter & Setter for Phone
    public String getPhone() { return phone; }
    public void setPhone(String phone) { this.phone = phone; }

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    public String getResidentialAddress() { return residentialAddress; }
    public void setResidentialAddress(String residentialAddress) { this.residentialAddress = residentialAddress; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    public List<String> getAllergies() { 
        return allergies != null ? allergies : new ArrayList<>(); 
    }
    public void setAllergies(List<String> allergies) { this.allergies = allergies; }

    public List<String> getChronicDiseases() { 
        return chronicDiseases != null ? chronicDiseases : new ArrayList<>(); 
    }
    public void setChronicDiseases(List<String> chronicDiseases) { this.chronicDiseases = chronicDiseases; }

    public List<String> getCurrentMedications() { 
        return currentMedications != null ? currentMedications : new ArrayList<>(); 
    }
    public void setCurrentMedications(List<String> currentMedications) { this.currentMedications = currentMedications; }

    public Double getHeight() { return height; }
    public void setHeight(Double height) { this.height = height; }

    public Double getWeight() { return weight; }
    public void setWeight(Double weight) { this.weight = weight; }
}