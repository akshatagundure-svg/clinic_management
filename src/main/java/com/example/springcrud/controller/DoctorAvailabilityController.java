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

    /**
     * ✅ 1. FILTER - Specific path MUST come before /{id}
     * Combined search for Doctor Availability
     * Example: /api/doctor-availability/filter?day=Monday&isAvailable=true
     */
    @GetMapping("/filter")
    public ResponseEntity<List<DoctorAvailability>> filterAvailability(
            @RequestParam(required = false) String doctorId,
            @RequestParam(required = false) String day,
            @RequestParam(required = false) Boolean isAvailable) {
        
        List<DoctorAvailability> list = availabilityRepository.findAll();

        // Optimized single stream chain
        List<DoctorAvailability> filteredList = list.stream()
            .filter(a -> doctorId == null || doctorId.isEmpty() || 
                    (a.getDoctorId() != null && a.getDoctorId().equalsIgnoreCase(doctorId)))
            .filter(a -> day == null || day.isEmpty() || 
                    (a.getDay() != null && a.getDay().equalsIgnoreCase(day)))
            .filter(a -> isAvailable == null || a.isAvailable() == isAvailable)
            .collect(Collectors.toList());

        return new ResponseEntity<>(filteredList, HttpStatus.OK);
    }

    /**
     * ✅ 2. READ ALL
     */
    @GetMapping
    public ResponseEntity<List<DoctorAvailability>> getAllAvailability() {
        List<DoctorAvailability> availabilities = availabilityRepository.findAll();
        return new ResponseEntity<>(availabilities, HttpStatus.OK);
    }

    /**
     * ✅ 3. READ BY ID - Generic dynamic path comes LAST
     */
    @GetMapping("/{id}")
    public ResponseEntity<DoctorAvailability> getAvailabilityById(@PathVariable String id) {
        Optional<DoctorAvailability> availability = availabilityRepository.findById(id);
        return availability.map(value -> new ResponseEntity<>(value, HttpStatus.OK))
                           .orElseGet(() -> new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * ✅ 4. CREATE
     */
    @PostMapping
    public ResponseEntity<DoctorAvailability> createAvailability(@RequestBody DoctorAvailability availability) {
        try {
            DoctorAvailability savedAvailability = availabilityRepository.save(availability);
            return new ResponseEntity<>(savedAvailability, HttpStatus.CREATED);
        } catch (Exception e) {
            return new ResponseEntity<>(null, HttpStatus.INTERNAL_SERVER_ERROR);
        }
    }

    /**
     * ✅ 5. UPDATE
     */
    @PutMapping("/{id}")
    public ResponseEntity<DoctorAvailability> updateAvailability(@PathVariable String id, @RequestBody DoctorAvailability details) {
        return availabilityRepository.findById(id).map(availability -> {
            availability.setDoctorId(details.getDoctorId());
            availability.setDay(details.getDay());
            availability.setStartTime(details.getStartTime());
            availability.setEndTime(details.getEndTime());
            availability.setAvailable(details.isAvailable());
            
            DoctorAvailability updated = availabilityRepository.save(availability);
            return new ResponseEntity<>(updated, HttpStatus.OK);
        }).orElse(new ResponseEntity<>(HttpStatus.NOT_FOUND));
    }

    /**
     * ✅ 6. DELETE
     */
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