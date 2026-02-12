package com.example.springcrud.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.CrossOrigin;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.PutMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RequestParam;
import org.springframework.web.bind.annotation.RestController;

import com.example.springcrud.model.Clinic;
import com.example.springcrud.repository.ClinicRepository;

@RestController
@RequestMapping("/api/clinics")
@CrossOrigin(origins = "*")
public class ClinicController {
    
    @Autowired
    private ClinicRepository clinicRepository;
    
    // CREATE - Add new clinic
    @PostMapping
    public ResponseEntity<Clinic> createClinic(@RequestBody Clinic clinic) {
        try {
            Clinic savedClinic = clinicRepository.save(clinic);
            return new ResponseEntity<>(savedClinic, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
    
    // READ - Get all clinics
    @GetMapping
    public ResponseEntity<List<Clinic>> getAllClinics() {
        List<Clinic> clinics = clinicRepository.findAll();
        return new ResponseEntity<>(clinics, HttpStatus.OK);
    }
    
    // READ - Get clinic by ID
    @GetMapping("/{id}")
    public ResponseEntity<Clinic> getClinicById(@PathVariable String id) {
        Optional<Clinic> clinic = clinicRepository.findById(id);
        return clinic.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * FILTER - Multi-criteria filter for Clinics
     * Example: /api/clinics/filter?clinicType=Dental&status=Active
     */
    @GetMapping("/filter")
    public ResponseEntity<List<Clinic>> filterClinics(
            @RequestParam(required = false) String clinicName,
            @RequestParam(required = false) String clinicType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String service) {
        
        List<Clinic> clinics = clinicRepository.findAll();

        // 1. Filter by Clinic Name (Partial match)
        if (clinicName != null && !clinicName.isEmpty()) {
            clinics = clinics.stream()
                .filter(c -> c.getClinicName() != null && 
                        c.getClinicName().toLowerCase().contains(clinicName.toLowerCase()))
                .collect(Collectors.toList());
        }

        // 2. Filter by Clinic Type (Exact match)
        if (clinicType != null && !clinicType.isEmpty()) {
            clinics = clinics.stream()
                .filter(c -> c.getClinicType() != null && 
                        c.getClinicType().equalsIgnoreCase(clinicType))
                .collect(Collectors.toList());
        }

        // 3. Filter by Status (Active/Inactive)
        if (status != null && !status.isEmpty()) {
            clinics = clinics.stream()
                .filter(c -> c.getStatus() != null && 
                        c.getStatus().equalsIgnoreCase(status))
                .collect(Collectors.toList());
        }

        // 4. Filter by Service (Searching inside a List<String>)
        if (service != null && !service.isEmpty()) {
            String searchService = service.toLowerCase();
            clinics = clinics.stream()
                .filter(c -> c.getServices() != null && 
                        c.getServices().stream().anyMatch(s -> s.toLowerCase().contains(searchService)))
                .collect(Collectors.toList());
        }

        return new ResponseEntity<>(clinics, HttpStatus.OK);
    }
    
    // UPDATE - Update clinic by ID
    @PutMapping("/{id}")
    public ResponseEntity<Clinic> updateClinic(@PathVariable String id, @RequestBody Clinic clinicDetails) {
        Optional<Clinic> clinicOptional = clinicRepository.findById(id);
        
        if (clinicOptional.isPresent()) {
            Clinic clinic = clinicOptional.get();
            clinic.setClinicName(clinicDetails.getClinicName());
            clinic.setClinicType(clinicDetails.getClinicType());
            clinic.setRegistrationNumber(clinicDetails.getRegistrationNumber());
            clinic.setStatus(clinicDetails.getStatus());
            clinic.setAddress(clinicDetails.getAddress());
            clinic.setContact(clinicDetails.getContact());
            clinic.setDepartments(clinicDetails.getDepartments());
            clinic.setServices(clinicDetails.getServices());
            clinic.setDoctors(clinicDetails.getDoctors());
            clinic.setOpeningTime(clinicDetails.getOpeningTime());
            clinic.setClosingTime(clinicDetails.getClosingTime());
            clinic.setAppointmentRequired(clinicDetails.getAppointmentRequired());
            
            Clinic updatedClinic = clinicRepository.save(clinic);
            return new ResponseEntity<>(updatedClinic, HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }
    
    // DELETE - Delete clinic by ID
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteClinic(@PathVariable String id) {
        try {
            if (clinicRepository.existsById(id)) {
                clinicRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}