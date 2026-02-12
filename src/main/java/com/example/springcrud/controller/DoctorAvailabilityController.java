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

import com.example.springcrud.model.DoctorAvailability;
import com.example.springcrud.repository.DoctorAvailabilityRepository;

@RestController
@RequestMapping("/api/doctor-availability")
@CrossOrigin(origins = "*")
public class DoctorAvailabilityController {

    @Autowired
    private DoctorAvailabilityRepository availabilityRepository;

    // READ - Get all availability records
    @GetMapping
    public ResponseEntity<List<DoctorAvailability>> getAllAvailability() {
        List<DoctorAvailability> availabilities = availabilityRepository.findAll();
        return new ResponseEntity<>(availabilities, HttpStatus.OK);
    }

    // READ - Get specific slot by ID
    @GetMapping("/{id}")
    public ResponseEntity<DoctorAvailability> getAvailabilityById(@PathVariable String id) {
        Optional<DoctorAvailability> availability = availabilityRepository.findById(id);
        return availability.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                           .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * FILTER - Combined search for Doctor Availability
     * Example: /api/doctor-availability/filter?day=Monday&isAvailable=true
     */
    @GetMapping("/filter")
    public ResponseEntity<List<DoctorAvailability>> filterAvailability(
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) String day,
            @RequestParam(required = false) Boolean isAvailable) {
        
        List<DoctorAvailability> list = availabilityRepository.findAll();

        // 1. Filter by Doctor ID
        if (doctorId != null && !doctorId.isEmpty()) {
            list = list.stream()
                .filter(a -> a.getDoctorId() != null && a.getDoctorId().equalsIgnoreCase(doctorId))
                .collect(Collectors.toList());
        }

        // 2. Filter by Day (Monday, Tuesday, etc.)
        if (day != null && !day.isEmpty()) {
            list = list.stream()
                .filter(a -> a.getDay() != null && a.getDay().equalsIgnoreCase(day))
                .collect(Collectors.toList());
        }

        // 3. Filter by Availability Status
        if (isAvailable != null) {
            list = list.stream()
                .filter(a -> a.isAvailable() == isAvailable)
                .collect(Collectors.toList());
        }

        return new ResponseEntity<>(list, HttpStatus.OK);
    }

    // CREATE
    @PostMapping
    public ResponseEntity<DoctorAvailability> createAvailability(@RequestBody DoctorAvailability availability) {
        DoctorAvailability savedAvailability = availabilityRepository.save(availability);
        return new ResponseEntity<>(savedAvailability, HttpStatus.CREATED);
    }

    // UPDATE
    @PutMapping("/{id}")
    public ResponseEntity<DoctorAvailability> updateAvailability(@PathVariable String id, @RequestBody DoctorAvailability details) {
        Optional<DoctorAvailability> existingOptional = availabilityRepository.findById(id);

        if (existingOptional.isPresent()) {
            DoctorAvailability availability = existingOptional.get();
            availability.setDoctorId(details.getDoctorId());
            availability.setDay(details.getDay());
            availability.setStartTime(details.getStartTime());
            availability.setEndTime(details.getEndTime());
            availability.setAvailable(details.isAvailable());
            
            return new ResponseEntity<>(availabilityRepository.save(availability), HttpStatus.OK);
        } else {
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        }
    }

    // DELETE
    @DeleteMapping("/{id}")
    public ResponseEntity<HttpStatus> deleteAvailability(@PathVariable String id) {
        try {
            if (availabilityRepository.existsById(id)) {
                availabilityRepository.deleteById(id);
                return new ResponseEntity<>(HttpStatus.NO_CONTENT);
            }
            return new ResponseEntity<>(HttpStatus.NOT_FOUND);
        } catch (Exception e) {
            return new ResponseEntity<>(HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }
}