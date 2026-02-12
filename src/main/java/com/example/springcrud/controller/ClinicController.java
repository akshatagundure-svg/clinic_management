package com.example.springcrud.controller;

import java.time.LocalDateTime;
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
            // FIX: Ensure clinicId is null so MongoDB generates a unique ID automatically.
            // This prevents the "duplicate key error { clinicId: null }"
            clinic.setClinicId(null);

            // Initialize Audit if missing
            if (clinic.getAudit() == null) {
                Clinic.Audit audit = new Clinic.Audit();
                audit.setCreatedAt(LocalDateTime.now());
                audit.setCreatedBy("SYSTEM_ADMIN");
                clinic.setAudit(audit);
            } else {
                clinic.getAudit().setCreatedAt(LocalDateTime.now());
            }

            // Ensure nested objects aren't null to prevent DB mapping issues
            if (clinic.getAddress() == null) clinic.setAddress(new Clinic.Address());
            if (clinic.getContact() == null) clinic.setContact(new Clinic.Contact());

            Clinic savedClinic = clinicRepository.save(clinic);
            return new ResponseEntity<>(savedClinic, HttpStatus.CREATED);
        } catch (Exception e) {
            e.printStackTrace(); 
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // READ - Get all clinics
    @GetMapping
    public ResponseEntity<List<Clinic>> getAllClinics() {
        try {
            List<Clinic> clinics = clinicRepository.findAll();
            return new ResponseEntity<>(clinics, HttpStatus.OK);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // READ - Get clinic by ID
    @GetMapping("/{id}")
    public ResponseEntity<Clinic> getClinicById(@PathVariable String id) {
        Optional<Clinic> clinic = clinicRepository.findById(id);
        return clinic.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                   .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // FILTER - Multi-criteria search
    @GetMapping("/filter")
    public ResponseEntity<List<Clinic>> filterClinics(
            @RequestParam(required = false) String clinicName,
            @RequestParam(required = false) String clinicType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String service) {
        
        List<Clinic> clinics = clinicRepository.findAll();

        List<Clinic> filteredClinics = clinics.stream()
            .filter(c -> clinicName == null || clinicName.isEmpty() || 
                    (c.getClinicName() != null && c.getClinicName().toLowerCase().contains(clinicName.toLowerCase())))
            .filter(c -> clinicType == null || clinicType.isEmpty() || 
                    (c.getClinicType() != null && c.getClinicType().equalsIgnoreCase(clinicType)))
            .filter(c -> status == null || status.isEmpty() || 
                    (c.getStatus() != null && c.getStatus().equalsIgnoreCase(status)))
            .filter(c -> service == null || service.isEmpty() || 
                    (c.getServices() != null && c.getServices().stream().anyMatch(s -> s.toLowerCase().contains(service.toLowerCase()))))
            .collect(Collectors.toList());

        return new ResponseEntity<>(filteredClinics, HttpStatus.OK);
    }

    // UPDATE - Update existing clinic
    @PutMapping("/{id}")
    public ResponseEntity<Clinic> updateClinic(@PathVariable String id, @RequestBody Clinic clinicDetails) {
        return clinicRepository.findById(id).map(clinic -> {
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
            
            if (clinic.getAudit() == null) {
                clinic.setAudit(new Clinic.Audit());
                clinic.getAudit().setCreatedAt(LocalDateTime.now());
            }
            clinic.getAudit().setUpdatedAt(LocalDateTime.now());
            
            Clinic updatedClinic = clinicRepository.save(clinic);
            return new ResponseEntity<>(updatedClinic, HttpStatus.OK);
        }).orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // DELETE - Remove clinic
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteClinic(@PathVariable String id) {
        try {
            if (clinicRepository.existsById(id)) {
                clinicRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            e.printStackTrace();
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}