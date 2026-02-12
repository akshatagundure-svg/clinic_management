package com.example.springcrud.model;

import java.time.LocalDate;
import java.time.Period;
import java.util.ArrayList; // Added
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document(collection = "patients")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Patient {

    @Id
    private String id; 

    private String patientId; 
    private String fullName;

   // Keep the JSON format for output, but Jackson will now try to parse input intelligently
@JsonFormat(pattern = "yyyy-MM-dd")
private LocalDate dateOfBirth;

    private String gender;
    private String phoneNumber;
    private String emailAddress;
    private String residentialAddress;
    private String emergencyContact;
    private String bloodGroup;

    // Initialize lists as empty ArrayLists to prevent NullPointerExceptions during filtering
    private List<String> allergies = new ArrayList<>();
    private List<String> chronicDiseases = new ArrayList<>();
    private List<String> currentMedications = new ArrayList<>();

    private Double height;
    private Double weight;

    public Patient() {}

    // ✅ Dynamic Age
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

    // Guard against null names during search
    public String getFullName() { 
        return fullName != null ? fullName : ""; 
    }
    public void setFullName(String fullName) { this.fullName = fullName; }

    public LocalDate getDateOfBirth() { return dateOfBirth; }
    public void setDateOfBirth(LocalDate dateOfBirth) { this.dateOfBirth = dateOfBirth; }

    public String getGender() { return gender; }
    public void setGender(String gender) { this.gender = gender; }

    public String getPhoneNumber() { return phoneNumber; }
    public void setPhoneNumber(String phoneNumber) { this.phoneNumber = phoneNumber; }

    public String getEmailAddress() { return emailAddress; }
    public void setEmailAddress(String emailAddress) { this.emailAddress = emailAddress; }

    public String getResidentialAddress() { return residentialAddress; }
    public void setResidentialAddress(String residentialAddress) { this.residentialAddress = residentialAddress; }

    public String getEmergencyContact() { return emergencyContact; }
    public void setEmergencyContact(String emergencyContact) { this.emergencyContact = emergencyContact; }

    public String getBloodGroup() { return bloodGroup; }
    public void setBloodGroup(String bloodGroup) { this.bloodGroup = bloodGroup; }

    // Ensures we never return a null list
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