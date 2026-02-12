package com.example.springcrud.controller;

import java.util.List;

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
import org.springframework.web.bind.annotation.RestController;

import com.example.springcrud.model.Prescription;
import com.example.springcrud.repository.PrescriptionRepository;

@RestController
@RequestMapping("/api/prescriptions")
@CrossOrigin(origins = "*")
public class PrescriptionController {

    @Autowired
    private PrescriptionRepository prescriptionRepository;

    @GetMapping
    public ResponseEntity<List<Prescription>> getAllPrescriptions() {
        return new ResponseEntity<>(prescriptionRepository.findAll(), HttpStatus.OK);
    }

    @GetMapping("/{id}")
    public ResponseEntity<Prescription> getPrescriptionById(@PathVariable String id) {
        return prescriptionRepository.findById(id)
                .map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @PostMapping
    public ResponseEntity<Prescription> createPrescription(@RequestBody Prescription prescription) {
        try {
            return new ResponseEntity<>(prescriptionRepository.save(prescription), HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // ✅ ADDED UPDATE METHOD
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

    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePrescription(@PathVariable String id) {
        if (prescriptionRepository.existsById(id)) {
            prescriptionRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}