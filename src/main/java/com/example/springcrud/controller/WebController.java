package com.example.springcrud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;

import com.example.springcrud.model.Doctor;

import jakarta.servlet.http.HttpSession;

@Controller
public class WebController{


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
@GetMapping("/clinic-register")
public String showClinicRegisterPage() {
    return "clinic-register"; // This must match the filename in src/main/resources/templates
}
@GetMapping("/clinic-list")
public String showClinicListPage() {
    return "clinic-list";
}

@GetMapping("/doctordb")
    public String showDoctorDB(HttpSession session, Model model) {
        // Retrieve name from session
        String doctorName = (String) session.getAttribute("doctorName");

        // Security: Redirect to login if they haven't logged in yet
        if (doctorName == null) {
            return "redirect:/login";
        }

        // Pass name to index.html (Thymeleaf)
        model.addAttribute("doctorName", doctorName);
        return "index"; // renders index.html
    }
    @GetMapping("/add-patient")
    public String showAddPatient() {
        return "add-patient"; 
    }
@GetMapping("/patientinfo")
    public String showPatientInfo() {
        return "patientinfo"; // Points to patientinfo.html
    }
    @GetMapping("/add-doctor")
public String showAddDoctor() {
    return "add-doctor"; // Serves src/main/resources/templates/add-doctor.html
}
@GetMapping("/doctorinfo")
public String showDoctorInfo() {
    return "doctorinfo"; // Points to templates/doctorinfo.html
}
@GetMapping("/login")
    public String loginPage() {
        return "login"; // Returns login.html from templates folder
    }
@GetMapping("/my-profile")
public String showMyProfile(HttpSession session, Model model) {
    // Retrieve the doctor object saved during login
    Doctor doctor = (Doctor) session.getAttribute("loggedInDoctor");

    if (doctor == null) {
        return "redirect:/login";
    }

    // Pass the full doctor object (which contains the ID) to Thymeleaf
    model.addAttribute("doctor", doctor);
    model.addAttribute("doctorName", doctor.getName());
    return "my-profile"; 
}
    

}