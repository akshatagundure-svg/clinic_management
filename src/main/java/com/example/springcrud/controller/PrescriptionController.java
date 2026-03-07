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

    // =====================================================
    // 📊 SEARCH & FILTERING
    // =====================================================

    /**
     * ✅ 1. SEARCH/FILTER
     * Allows filtering by patientId, diagnosis keyword, or record status.
     */
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
                    (p.getDiagnosis() != null && p.getDiagnosis().getConfirmedDiagnosis() != null &&
                     p.getDiagnosis().getConfirmedDiagnosis().toLowerCase().contains(diagnosis.toLowerCase())))
            .filter(p -> status == null || status.isEmpty() || 
                    (p.getRecordStatus() != null && p.getRecordStatus().equalsIgnoreCase(status)))
            .collect(Collectors.toList());

        return new ResponseEntity<>(filtered, HttpStatus.OK);
    }

    // =====================================================
    // 📂 FETCH BY RELATIONS (Linking Doctor & Patient)
    // =====================================================

    /**
     * ✅ 2. FETCH BY DOCTOR 
     * Retrieves all prescriptions issued by a specific doctor.
     */
    @GetMapping("/doctor/{doctorId}")
    public ResponseEntity<List<Prescription>> getByDoctor(@PathVariable String doctorId) {
        // Matches Repository method findByCurrentDoctor_DoctorId for nested objects
        return ResponseEntity.ok(prescriptionRepository.findByCurrentDoctor_DoctorId(doctorId));
    }

    /**
     * ✅ 3. FETCH BY PATIENT
     * Retrieves the clinical history for a specific patient.
     */
    // ✅ KEEP THIS ONE (Around Line 78)
@GetMapping("/patient/{patientId}")
public ResponseEntity<List<Prescription>> getByPatient(@PathVariable String patientId) {
    // Ensure findByPatient_PatientId is defined in your Repository
    return ResponseEntity.ok(prescriptionRepository.findByPatient_PatientId(patientId));
}

// ❌ DELETE THE OTHER ONE (Around Line 115)
// Remove the method 'getPatientPrescriptions' entirely!

    // =====================================================
    // 📝 STANDARD CRUD OPERATIONS
    // =====================================================

    /**
     * ✅ 4. READ ALL
     */
    @GetMapping
    public ResponseEntity<List<Prescription>> getAllPrescriptions() {
        return new ResponseEntity<>(prescriptionRepository.findAll(), HttpStatus.OK);
    }

    /**
     * ✅ 5. READ BY ID
     */
    @GetMapping("/{id}")
    public ResponseEntity<Prescription> getPrescriptionById(@PathVariable String id) {
        return prescriptionRepository.findById(id)
                .map(p -> new ResponseEntity<>(p, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * ✅ 6. CREATE
     * Finalizes the record and assigns a unique RX ID if not provided.
     */
   @PostMapping
public ResponseEntity<?> createPrescription(@RequestBody Prescription prescription) {
    try {
        String uniqueRxId = "RX-" + java.util.UUID.randomUUID().toString().substring(0, 8).toUpperCase();
        
        prescription.setId(uniqueRxId);
        prescription.setPrescriptionId(uniqueRxId); // ✅ Populates the indexed field

        Prescription saved = prescriptionRepository.save(prescription);
        return new ResponseEntity<>(saved, HttpStatus.CREATED);
    } catch (Exception e) {
        e.printStackTrace();
        return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR)
                             .body("Backend Error: " + e.getMessage());
    }
}
    /**
     * ✅ 7. UPDATE
     */
    @PutMapping("/{id}")
    public ResponseEntity<Prescription> updatePrescription(@PathVariable String id, @RequestBody Prescription newData) {
        return prescriptionRepository.findById(id).map(p -> {
            p.setPatient(newData.getPatient());
            p.setCurrentDoctor(newData.getCurrentDoctor());
            p.setDiagnosis(newData.getDiagnosis());
            p.setMedications(newData.getMedications());
            p.setRecordStatus(newData.getRecordStatus());
            p.setTreatmentTimeline(newData.getTreatmentTimeline());
            p.setAudit(newData.getAudit());
            
            return new ResponseEntity<>(prescriptionRepository.save(p), HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * ✅ 8. DELETE
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deletePrescription(@PathVariable String id) {
        if (prescriptionRepository.existsById(id)) {
            prescriptionRepository.deleteById(id);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        }
        return new ResponseEntity<>(HttpStatus.NOT_FOUND);
    }
}