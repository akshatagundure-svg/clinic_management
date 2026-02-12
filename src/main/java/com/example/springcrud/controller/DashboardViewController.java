package com.example.springcrud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.web.bind.annotation.GetMapping;

@Controller
public class DashboardViewController {

    @GetMapping("/dashboard")
    public String showSaaSDashboard() {
        return "dashboard"; 
    }
    @GetMapping("/doctor-register")
public String showDoctorRegister() {
    return "doctor-register"; // Points to templates/doctor-register.html
}
@GetMapping("/patient-register")
public String showPatientRegister() {
    return "patient-register"; // Points to templates/patient-register.html
}
@GetMapping("/prescription-register")
public String showPrescriptionRegister() {
    return "prescription-register"; // Points to templates/prescription-register.html
}
@GetMapping("/availability-register")
public String showAvailabilityRegister() {
    return "availability-register"; // Points to templates/availability-register.html
}
@GetMapping("/doctor-list")
public String showDoctorList() {
    return "doctor-list"; // Points to templates/doctor-list.html
}
@GetMapping("/patient-list")
public String showPatientList() {
    return "patient-list"; // This must match the name of your HTML file (without .html)
}
@GetMapping("/availability-list")
public String showAvailabilityList() {
    return "availability-list"; // Points to templates/availability-list.html
}
@GetMapping("/prescription-list")
public String showPrescriptionList() {
    return "prescription-list"; // Maps to templates/prescription-list.html
}
@GetMapping("/medicine-register")
public String showMedicineRegister() {
    return "medicine-register"; // Points to templates/medicine-register.html
}
@GetMapping("/medicine-list")
public String showMedicineList() {
    return "medicine-list"; // This points to templates/medicine-list.html
}
@GetMapping("/medical-test-register")
public String showMedicalTestRegister() {
    return "medical-test-register"; // Points to templates/medical-test-register.html
}
@GetMapping("/medical-test-list")
public String showMedicalTestList() {
    return "medical-test-list"; // Serves templates/medical-test-list.html
}

}