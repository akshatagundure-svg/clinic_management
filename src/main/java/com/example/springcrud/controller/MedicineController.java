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

import com.example.springcrud.model.Medicine;
import com.example.springcrud.repository.MedicineRepository;

@RestController
@RequestMapping("/api/medicines")
@CrossOrigin(origins = "*")
public class MedicineController {

    @Autowired
    private MedicineRepository medicineRepository;

    /**
     * ✅ 1. FILTER - Specific path MUST come before /{id}
     * Find medicines by combining criteria
     * Example: /api/medicines/filter?company=Pfizer&name=Advil
     */
    @GetMapping("/filter")
    public ResponseEntity<List<Medicine>> filterMedicines(
            @RequestParam(required = false) String medId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String company,
            @RequestParam(required = false) String status) {
        
        List<Medicine> medicines = medicineRepository.findAll();

        // Single Stream pipeline for better performance
        List<Medicine> filteredMedicines = medicines.stream()
            .filter(m -> medId == null || medId.isEmpty() || 
                    (m.getMedId() != null && m.getMedId().equalsIgnoreCase(medId)))
            .filter(m -> name == null || name.isEmpty() || 
                    (m.getName() != null && m.getName().toLowerCase().contains(name.toLowerCase())))
            .filter(m -> company == null || company.isEmpty() || 
                    (m.getCompanyName() != null && m.getCompanyName().toLowerCase().contains(company.toLowerCase())))
            .filter(m -> status == null || status.isEmpty() || 
                    (m.getRecordStatus() != null && m.getRecordStatus().equalsIgnoreCase(status)))
            .collect(Collectors.toList());

        return new ResponseEntity<>(filteredMedicines, HttpStatus.OK);
    }

    /**
     * ✅ 2. READ ALL
     */
    @GetMapping
    public ResponseEntity<List<Medicine>> getAllMedicines() {
        List<Medicine> medicines = medicineRepository.findAll();
        return new ResponseEntity<>(medicines, HttpStatus.OK);
    }

    /**
     * ✅ 3. READ BY ID - Generic dynamic path comes last
     */
    @GetMapping("/{id}")
    public ResponseEntity<Medicine> getMedicineById(@PathVariable String id) {
        Optional<Medicine> medicine = medicineRepository.findById(id);
        return medicine.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                       .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * ✅ 4. CREATE
     */
    @PostMapping
    public ResponseEntity<Medicine> createMedicine(@RequestBody Medicine medicine) {
        try {
            Medicine savedMedicine = medicineRepository.save(medicine);
            return new ResponseEntity<>(savedMedicine, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ✅ 5. UPDATE
     */
    @PutMapping("/{id}")
    public ResponseEntity<Medicine> updateMedicine(@PathVariable String id, @RequestBody Medicine medicineDetails) {
        return medicineRepository.findById(id).map(medicine -> {
            medicine.setMedId(medicineDetails.getMedId());
            medicine.setName(medicineDetails.getName());
            medicine.setCompanyName(medicineDetails.getCompanyName());
            medicine.setRecordStatus(medicineDetails.getRecordStatus());
            medicine.setDoctorChangeAllowed(medicineDetails.isDoctorChangeAllowed());
            medicine.setDosage(medicineDetails.getDosage());
            medicine.setRoute(medicineDetails.getRoute());
            medicine.setFrequency(medicineDetails.getFrequency());
            medicine.setDuration(medicineDetails.getDuration());
            medicine.setPrice(medicineDetails.getPrice());
            medicine.setExpiryDate(medicineDetails.getExpiryDate());
            medicine.setStartDate(medicineDetails.getStartDate());
            medicine.setEndDate(medicineDetails.getEndDate());
            medicine.setSpecialInstructions(medicineDetails.getSpecialInstructions());
            
            Medicine updatedMedicine = medicineRepository.save(medicine);
            return new ResponseEntity<>(updatedMedicine, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * ✅ 6. DELETE
     */
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteMedicine(@PathVariable String id) {
        try {
            if (medicineRepository.existsById(id)) {
                medicineRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            } else {
                return new ResponseEntity<>(HttpStatus.NOT_FOUND);
            }
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}