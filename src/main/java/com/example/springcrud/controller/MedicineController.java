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

    // READ - Get all medicines
    @GetMapping
    public ResponseEntity<List<Medicine>> getAllMedicines() {
        List<Medicine> medicines = medicineRepository.findAll();
        return new ResponseEntity<>(medicines, HttpStatus.OK);
    }

    // READ - Get medicine by ID
    @GetMapping("/{id}")
    public ResponseEntity<Medicine> getMedicineById(@PathVariable String id) {
        Optional<Medicine> medicine = medicineRepository.findById(id);
        return medicine.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                       .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * FILTER - Find medicines by combining criteria
     * Example: /api/medicines/filter?company=Pfizer&name=Advila
     */
    @GetMapping("/filter")
    public ResponseEntity<List<Medicine>> filterMedicines(
            @RequestParam(required = false) String medId,
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String company) {
        
        List<Medicine> medicines = medicineRepository.findAll();

        // 1. Filter by Business ID (medId)
        if (medId != null && !medId.isEmpty()) {
            medicines = medicines.stream()
                .filter(m -> m.getMedId() != null && m.getMedId().equalsIgnoreCase(medId))
                .collect(Collectors.toList());
        }

        // 2. Filter by Name (Partial match)
        if (name != null && !name.isEmpty()) {
            medicines = medicines.stream()
                .filter(m -> m.getName() != null && 
                        m.getName().toLowerCase().contains(name.toLowerCase()))
                .collect(Collectors.toList());
        }

        // 3. Filter by Company Name (Partial match)
        if (company != null && !company.isEmpty()) {
            medicines = medicines.stream()
                .filter(m -> m.getCompanyName() != null && 
                        m.getCompanyName().toLowerCase().contains(company.toLowerCase()))
                .collect(Collectors.toList());
        }

        return new ResponseEntity<>(medicines, HttpStatus.OK);
    }

    // CREATE - Add a new medicine
    @PostMapping
    public ResponseEntity<Medicine> createMedicine(@RequestBody Medicine medicine) {
        try {
            Medicine savedMedicine = medicineRepository.save(medicine);
            return new ResponseEntity<>(savedMedicine, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    // UPDATE - Update medicine details
    @PutMapping("/{id}")
    public ResponseEntity<Medicine> updateMedicine(@PathVariable String id, @RequestBody Medicine medicineDetails) {
        Optional<Medicine> medicineOptional = medicineRepository.findById(id);

        if (medicineOptional.isPresent()) {
            Medicine medicine = medicineOptional.get();
            
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
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // DELETE - Remove medicine
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