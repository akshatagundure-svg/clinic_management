package com.example.springcrud.controller;

import java.time.LocalDateTime;
import java.util.List;
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
import com.example.springcrud.service.SequenceGeneratorService;

@RestController
@RequestMapping("/api/clinics")
@CrossOrigin(origins = "*")
public class ClinicController {

    @Autowired
    private SequenceGeneratorService sequenceGeneratorService;

    @Autowired
    private ClinicRepository clinicRepository;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<Clinic> createClinic(@RequestBody Clinic clinic) {
        try {
            long seq = sequenceGeneratorService.generateSequence("clinic_sequence");
            clinic.setClinicId(String.format("CLINIC%03d", seq));

            if (clinic.getAudit() == null) {
                clinic.setAudit(new Clinic.Audit());
            }
            clinic.getAudit().setCreatedAt(LocalDateTime.now());
            clinic.getAudit().setUpdatedAt(LocalDateTime.now());

            if (clinic.getStatus() == null) {
                clinic.setStatus("ACTIVE");
            }

            Clinic savedClinic = clinicRepository.save(clinic);
            return new ResponseEntity<>(savedClinic, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ================= READ (LIST & FILTERS) =================
    /**
     * ✅ Consolidated: This single endpoint handles both "Get All" 
     * and filtered searches for the Patient Dashboard.
     */
    @GetMapping
    public ResponseEntity<List<Clinic>> getClinics(
            @RequestParam(required = false) String clinicType,
            @RequestParam(required = false) String status,
            @RequestParam(required = false) String city,
            @RequestParam(required = false) String department,
            @RequestParam(required = false) String service,
            @RequestParam(required = false) Boolean appointmentRequired) {

        List<Clinic> clinics = clinicRepository.findAll();

        // Apply filters only if parameters are provided
        if (clinicType != null) {
            clinics = clinics.stream()
                    .filter(c -> clinicType.equalsIgnoreCase(c.getClinicType()))
                    .collect(Collectors.toList());
        }
        if (status != null) {
            clinics = clinics.stream()
                    .filter(c -> status.equalsIgnoreCase(c.getStatus()))
                    .collect(Collectors.toList());
        }
        if (city != null) {
            clinics = clinics.stream()
                    .filter(c -> c.getAddress() != null && city.equalsIgnoreCase(c.getAddress().getCity()))
                    .collect(Collectors.toList());
        }
        if (department != null) {
            clinics = clinics.stream()
                    .filter(c -> c.getDepartments() != null && c.getDepartments().contains(department))
                    .collect(Collectors.toList());
        }
        if (service != null) {
            clinics = clinics.stream()
                    .filter(c -> c.getServices() != null && c.getServices().contains(service))
                    .collect(Collectors.toList());
        }
        if (appointmentRequired != null) {
            clinics = clinics.stream()
                    .filter(c -> appointmentRequired.equals(c.getAppointmentRequired()))
                    .collect(Collectors.toList());
        }

        return ResponseEntity.ok(clinics);
    }

    // ================= READ (SPECIFIC) =================
    /**
     * ✅ Detail View: Fixed Ambiguous Mapping. 
     * This is the only method mapped to /{clinicId}.
     */
    @GetMapping("/{clinicId}")
    public ResponseEntity<Clinic> getClinicById(@PathVariable String clinicId) {
        return clinicRepository.findByClinicId(clinicId)
                .map(ResponseEntity::ok)
                .orElse(ResponseEntity.notFound().build());
    }

    @GetMapping("/count/{doctorId}")
    public long getClinicCount(@PathVariable String doctorId) {
        return clinicRepository.countByDoctorId(doctorId); 
    }

    @GetMapping("/my-clinics/{doctorId}")
    public ResponseEntity<List<Clinic>> getMyClinics(@PathVariable String doctorId) {
        return ResponseEntity.ok(clinicRepository.findByDoctorId(doctorId));
    }

    // ================= UPDATE =================
    @PutMapping("/{clinicId}")
    public ResponseEntity<Clinic> updateClinic(
            @PathVariable String clinicId,
            @RequestBody Clinic updatedClinic) {

        return clinicRepository.findByClinicId(clinicId)
                .map(existing -> {
                    updatedClinic.setId(existing.getId()); 
                    updatedClinic.setClinicId(existing.getClinicId()); 

                    if (updatedClinic.getAudit() != null) {
                        updatedClinic.getAudit().setUpdatedAt(LocalDateTime.now());
                    }

                    return ResponseEntity.ok(clinicRepository.save(updatedClinic));
                })
                .orElse(ResponseEntity.notFound().build());
    }

    // ================= DELETE =================
    @DeleteMapping("/{clinicId}")
    public ResponseEntity<Void> deleteClinic(@PathVariable String clinicId) {
        if (!clinicRepository.existsByClinicId(clinicId)) {
            return ResponseEntity.notFound().build();
        }
        clinicRepository.deleteByClinicId(clinicId);
        return ResponseEntity.noContent().build();
    }
}