package com.example.springcrud.controller;

import java.util.List;
import java.util.Optional;
import java.util.stream.Collectors;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
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
import com.example.springcrud.model.LoginRequest;
import com.example.springcrud.repository.DoctorRepository;

import jakarta.servlet.http.HttpSession;

@RestController
@RequestMapping("/api/doctors")
public class DoctorController {

    @Autowired
    private DoctorRepository doctorRepository;

    // ================= CREATE =================
    @PostMapping
    public ResponseEntity<Doctor> createDoctor(@RequestBody Doctor doctor) {
        Doctor savedDoctor = doctorRepository.save(doctor);
        return new ResponseEntity<>(savedDoctor, HttpStatus.CREATED);
    }

    // ================= READ WITH FILTERS =================
    @GetMapping
    public ResponseEntity<List<Doctor>> getAllDoctors(
            @RequestParam(required = false) String name,
            @RequestParam(required = false) String specialization,
            @RequestParam(required = false) Integer minExperience,
            @RequestParam(required = false) Integer maxExperience,
            @RequestParam(required = false) String qualification,
            @RequestParam(required = false) String gender,
            @RequestParam(required = false) String hospitalName,
            @RequestParam(required = false) Double minFee,
            @RequestParam(required = false) Double maxFee,
            @RequestParam(required = false) String availability,
            @RequestParam(required = false) String address) {

        List<Doctor> doctors = doctorRepository.findAll();

        List<Doctor> filteredDoctors = doctors.stream()
                .filter(d -> name == null || (d.getName() != null && d.getName().toLowerCase().contains(name.toLowerCase())))
                .filter(d -> specialization == null || (d.getSpecialization() != null && d.getSpecialization().toLowerCase().contains(specialization.toLowerCase())))
                .filter(d -> minExperience == null || (d.getExperience() != null && d.getExperience() >= minExperience))
                .filter(d -> maxExperience == null || (d.getExperience() != null && d.getExperience() <= maxExperience))
                .filter(d -> qualification == null || (d.getQualification() != null && d.getQualification().stream().anyMatch(q -> q.equalsIgnoreCase(qualification))))
                .filter(d -> gender == null || (d.getGender() != null && d.getGender().equalsIgnoreCase(gender)))
                .filter(d -> hospitalName == null || (d.getHospitalName() != null && d.getHospitalName().toLowerCase().contains(hospitalName.toLowerCase())))
                .filter(d -> minFee == null || (d.getConsultationFee() != null && d.getConsultationFee() >= minFee))
                .filter(d -> maxFee == null || (d.getConsultationFee() != null && d.getConsultationFee() <= maxFee))
                .filter(d -> availability == null || (d.getAvailability() != null && d.getAvailability().equalsIgnoreCase(availability)))
                .filter(d -> address == null || (d.getAddress() != null && d.getAddress().toLowerCase().contains(address.toLowerCase())))
                .collect(Collectors.toList());

        return new ResponseEntity<>(filteredDoctors, HttpStatus.OK);
    }

    // ================= READ BY ID =================
    @GetMapping("/{doctorId}")
    public ResponseEntity<Doctor> getDoctorById(@PathVariable String doctorId) {
        Optional<Doctor> doctor = doctorRepository.findById(doctorId);
        return doctor.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    // ================= AUTHENTICATION =================
    @PostMapping("/login")
    public ResponseEntity<?> authDoctor(@RequestBody LoginRequest request, HttpSession session) {
        Optional<Doctor> doctor = doctorRepository.findByPhoneAndPassword(
                request.getPhone(),
                request.getPassword());

        if (doctor.isPresent()) {
            // CRITICAL: Save the full object and ID for profile management
            session.setAttribute("doctorId", doctor.get().getId());
            session.setAttribute("doctorName", doctor.get().getName());
            session.setAttribute("loggedInDoctor", doctor.get());
            return new ResponseEntity<>(doctor.get(), HttpStatus.OK);
        } else {
            return new ResponseEntity<>("Invalid phone or password", HttpStatus.UNAUTHORIZED);
        }
    }

    // ================= UPDATE PROFILE =================
   // ================= UPDATE PROFILE =================
    // ================= UPDATE PROFILE =================
    @PutMapping("/{doctorId}")
public ResponseEntity<?> updateDoctor(@PathVariable String doctorId, @RequestBody Doctor details, HttpSession session) {
    Optional<Doctor> doctorOptional = doctorRepository.findById(doctorId);

    if (doctorOptional.isPresent()) {
        Doctor existing = doctorOptional.get();

        // 1. Update all fields from the details object
        existing.setName(details.getName());
        existing.setSpecialization(details.getSpecialization());
        existing.setExperience(details.getExperience());
        existing.setQualification(details.getQualification());
        existing.setGender(details.getGender());
        existing.setEmail(details.getEmail());
        existing.setPassword(details.getPassword());
        existing.setConsultationFee(details.getConsultationFee());
        existing.setAvailability(details.getAvailability());
        existing.setHospitalName(details.getHospitalName());
        existing.setAddress(details.getAddress());

        // 2. Save to MongoDB
        Doctor saved = doctorRepository.save(existing);
        
        // 3. REFRESH SESSION - This is why your page wasn't showing changes!
        session.setAttribute("doctorName", saved.getName());
        session.setAttribute("loggedInDoctor", saved); 

        return new ResponseEntity<>(saved, HttpStatus.OK);
    } else {
        return new ResponseEntity<>("Doctor not found with ID: " + doctorId, HttpStatus.NOT_FOUND);
    }
}
    // ================= DELETE =================
    @DeleteMapping("/{doctorId}")
    public ResponseEntity<HttpStatus> deleteDoctor(@PathVariable String doctorId) {
        try {
            doctorRepository.deleteById(doctorId);
            return new ResponseEntity<>(HttpStatus.NO_CONTENT);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}