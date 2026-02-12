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

import com.example.springcrud.model.Doctor;
import com.example.springcrud.repository.DoctorRepository;

@RestController
@RequestMapping("/api/doctors")
@CrossOrigin(origins = "*")
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

    @PostMapping
    public ResponseEntity<Doctor> create(@RequestBody Doctor doctor) {
        try {
            Doctor savedDoctor = doctorRepository.save(doctor);
            return new ResponseEntity<>(savedDoctor, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    @GetMapping
    public List<Doctor> getAll() {
        return doctorRepository.findAll();
    }

    @GetMapping("/{id}")
    public ResponseEntity<Doctor> getById(@PathVariable String id) {
        return doctorRepository.findById(id)
                .map(d -> new ResponseEntity<>(d, HttpStatus.OK))
                .orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // =====================================================
    // UPDATE (Updated with NEW fields)
    // =====================================================
    @PutMapping("/{id}")
    public ResponseEntity<Doctor> update(@PathVariable String id, @RequestBody Doctor newData) {
        return doctorRepository.findById(id).map(existingDoctor -> {
            if (newData.getName() != null) existingDoctor.setName(newData.getName());
            if (newData.getSpecialization() != null) existingDoctor.setSpecialization(newData.getSpecialization());
            if (newData.getExperience() != null) existingDoctor.setExperience(newData.getExperience());
            if (newData.getQualification() != null) existingDoctor.setQualification(newData.getQualification());
            if (newData.getGender() != null) existingDoctor.setGender(newData.getGender());
            if (newData.getPhone() != null) existingDoctor.setPhone(newData.getPhone());
            
            // New fields update logic
            if (newData.getEmail() != null) existingDoctor.setEmail(newData.getEmail());
            if (newData.getConsultationFee() != null) existingDoctor.setConsultationFee(newData.getConsultationFee());
            if (newData.getAvailability() != null) existingDoctor.setAvailability(newData.getAvailability());
            if (newData.getHospitalName() != null) existingDoctor.setHospitalName(newData.getHospitalName());
            if (newData.getAddress() != null) existingDoctor.setAddress(newData.getAddress());

            Doctor updated = doctorRepository.save(existingDoctor);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    @DeleteMapping("/{id}")
    public ResponseEntity<?> delete(@PathVariable String id) {
        if (!doctorRepository.existsById(id)) {
            return new ResponseEntity<>("Doctor not found", HttpStatus.NOT_FOUND);
        }
        doctorRepository.deleteById(id);
        return new ResponseEntity<>(HttpStatus.NO_CONTENT);
    }

    // =====================================================
    // 🔍 ADVANCED FILTER API (Updated for NEW fields)
    // =====================================================
    @GetMapping("/search")
    public List<Doctor> searchDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) String hospitalName,
            @RequestParam(required = false) Double maxFee, // New filter
            @RequestParam(required = false) String availability,
            @RequestParam(required = false) Integer minExp,
            @RequestParam(required = false) String sortBy,
            @RequestParam(defaultValue = "asc") String order
    ) {
        List<Doctor> list = doctorRepository.findAll();

        if (name != null && !name.isEmpty()) {
            list = list.stream().filter(d -> d.getName() != null && d.getName().toLowerCase().contains(name.toLowerCase())).collect(Collectors.toList());
        }

        if (specialization != null && !specialization.isEmpty()) {
            list = list.stream().filter(d -> d.getSpecialization() != null && d.getSpecialization().equalsIgnoreCase(specialization)).collect(Collectors.toList());
        }

        if (hospitalName != null && !hospitalName.isEmpty()) {
            list = list.stream().filter(d -> d.getHospitalName() != null && d.getHospitalName().toLowerCase().contains(hospitalName.toLowerCase())).collect(Collectors.toList());
        }

        if (availability != null) {
            list = list.stream().filter(d -> d.getAvailability() != null && d.getAvailability().equalsIgnoreCase(availability)).collect(Collectors.toList());
        }

        if (maxFee != null) {
            list = list.stream().filter(d -> d.getConsultationFee() != null && d.getConsultationFee() <= maxFee).collect(Collectors.toList());
        }

        if (minExp != null) {
            list = list.stream().filter(d -> d.getExperience() != null && d.getExperience() >= minExp).collect(Collectors.toList());
        }

        // Updated Sorting Logic
        if (sortBy != null) {
            if (sortBy.equalsIgnoreCase("name")) {
                list.sort((a, b) -> a.getName().compareToIgnoreCase(b.getName()));
            } else if (sortBy.equalsIgnoreCase("experience")) {
                list.sort((a, b) -> Integer.compare(a.getExperience(), b.getExperience()));
            } else if (sortBy.equalsIgnoreCase("fee")) {
                list.sort((a, b) -> Double.compare(a.getConsultationFee(), b.getConsultationFee()));
            }
            
            if (order.equalsIgnoreCase("desc")) {
                java.util.Collections.reverse(list);
            }
        }

        return list;
    }

    @GetMapping("/count")
    public long getCount() {
        return doctorRepository.count();
    }
}