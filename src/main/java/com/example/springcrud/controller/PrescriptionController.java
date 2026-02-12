package com.example.springcrud.controller;

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

import com.example.springcrud.model.Prescription;
import com.example.springcrud.repository.PrescriptionRepository;

@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = "*")
public class PrescriptionController {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    // ✅ 1. SEARCH/FILTER (Specific path MUST come before /{id})
    @GetMapping("/search")
    public ResponseEntity<List<Prescription>> searchPrescriptions(
            @RequestParam(required = false) String patientId,
            @RequestParam(required = false) String diagnosis,
            @RequestParam(required = false) String status) {
        
        List<Prescription> prescriptions = prescriptionRepository.findAll();

        List<Prescription> filtered = prescriptions.stream()
            .filter(p -> patientId == null || patientId.isEmpty() || 
                    (p.getPatient() != null && p.getPatient().getPatientId().equalsIgnoreCase(patientId)))
            .filter(p -> diagnosis == null || diagnosis.isEmpty() || 
                    (p.getDiagnosis() != null && p.getDiagnosis().getConfirmedDiagnosis().toLowerCase().contains(diagnosis.toLowerCase())))
            .filter(p -> status == null || status.isEmpty() || 
                    (p.getRecordStatus() != null && p.getRecordStatus().equalsIgnoreCase(status)))
            .collect(Collectors.toList());

        return new ResponseEntity<>(filtered, HttpStatus.OK);
    }

    // ✅ 2. READ ALL
    @GetMapping
    public ResponseEntity<List<Prescription>> getAllPrescriptions() {
        return new ResponseEntity<>(prescriptionRepository.findAll(), HttpStatus.OK);
    }

    // ✅ 3. READ BY ID (Dynamic path)
    @GetMapping("/{id}")
    public ResponseEntity<Prescription> getPrescriptionById(@PathVariable String id) {
        return prescriptionRepository.findById(id)
                .map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ✅ 4. CREATE
    @PostMapping
    public ResponseEntity<Prescription> createPrescription(@RequestBody Prescription prescription) {
        try {
            return new ResponseEntity<>(prescriptionRepository.save(prescription), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ 5. UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<Prescription> updatePrescription(@PathVariable String id, @RequestBody Prescription newData) {
        return prescriptionRepository.findById(id).map(p -> {
            p.setPatient(newData.getPatient());
            p.setCurrentDoctor(newData.getCurrentDoctor());
            p.setDiagnosis(newData.getDiagnosis());
            p.setMedications(newData.getMedications());
            p.setRecordStatus(newData.getRecordStatus());
            return new ResponseEntity<>(prescriptionRepository.save(p), HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ✅ 6. DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePrescription(@PathVariable String id) {
        if (prescriptionRepository.existsById(id)) {
            prescriptionRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}