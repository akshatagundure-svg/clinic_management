package com.example.springcrud.controller;

import org.springframework.stereotype.Controller;
import org.springframework.ui.Model;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestParam;

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
public String showAddPatient(HttpSession session, Model model) {

    Doctor doctor = (Doctor) session.getAttribute("loggedInDoctor");

    if (doctor == null) {
        return "redirect:/login";
    }

    model.addAttribute("doctorId", doctor.getDoctorId());
    model.addAttribute("doctorName", doctor.getName());

    return "add-patient";
}
// Inside WebController.java
@GetMapping("/patientinfo")
public String showPatientInfo(HttpSession session, Model model) {
    // This looks for the object we just added in Step 1
    Doctor doctor = (Doctor) session.getAttribute("loggedInDoctor");
    
    if (doctor == null) {
        System.out.println("Session check failed: loggedInDoctor is null");
        return "redirect:/login";
    }

    // Pass the ID string to the HTML
    model.addAttribute("doctorId", doctor.getDoctorId()); 
    return "patientinfo";
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
    String doctorId = (String) session.getAttribute("doctorId");
    if (doctorId == null) {
        return "redirect:/login"; 
    }
    model.addAttribute("doctorId", doctorId);
    model.addAttribute("doctorName", session.getAttribute("doctorName"));
    return "my-profile"; // Points to my-profile.html
}

@GetMapping("/add-clinic")
public String showAddClinicPage(HttpSession session, Model model) {
    String doctorId = (String) session.getAttribute("doctorId");
    if (doctorId == null) {
        return "redirect:/login"; // Redirect to login if session is missing
    }
    model.addAttribute("doctorId", doctorId);
    model.addAttribute("doctorName", session.getAttribute("doctorName"));
    return "add-clinic";
}
@GetMapping("/clinicinfo")
    public String showClinicInfoPage(HttpSession session, Model model) {
        String doctorId = (String) session.getAttribute("doctorId");
        
        // Safety check: if doctor is not logged in, send them to login
        if (doctorId == null) {
            return "redirect:/login"; 
        }

        model.addAttribute("doctorId", doctorId);
        model.addAttribute("doctorName", session.getAttribute("doctorName"));
        return "clinicinfo"; // Must match clinicinfo.html
    }
// Renamed the mapping to match the filename 'add-prescription'
// Inside WebController.java
// WebController.java
 @GetMapping("/add-prescription")
public String showPrescriptionPage(@RequestParam String patientId, Model model, HttpSession session) {
    // Note: If you have different session keys, adjust them here
    model.addAttribute("doctorId", session.getAttribute("doctorId"));
    model.addAttribute("doctorName", session.getAttribute("doctorName"));
    model.addAttribute("patientId", patientId);
    return "add-prescription"; 
}
@GetMapping("/patient-prescriptions")
public String getPatientHistory(@RequestParam String patientId, 
                                @RequestParam String doctorId, 
                                Model model) {
    // These must be added to the model so the HTML can use them
    model.addAttribute("patientId", patientId);
    model.addAttribute("doctorId", doctorId);
    return "patient-prescriptions"; 
}

@GetMapping("/view-prescription/{id}")
public String viewSinglePrescription(@PathVariable String id, Model model) {
    model.addAttribute("prescriptionId", id);
    return "view-prescription"; 
}

@GetMapping("/clinic-details")
public String showClinicDetailsPage(@RequestParam String id, HttpSession session, Model model) {
    String doctorId = (String) session.getAttribute("doctorId");
    if (doctorId == null) {
        return "redirect:/login"; 
    }
    // We pass the clinic ID from the URL to the page
    model.addAttribute("targetClinicId", id);
    model.addAttribute("doctorId", doctorId);
    model.addAttribute("doctorName", session.getAttribute("doctorName"));
    return "clinic-details";
}

@GetMapping("/medical-tests-dashboard")
    public String showMedicalTestPatientList(HttpSession session, Model model) {
        String doctorId = (String) session.getAttribute("doctorId");
        if (doctorId == null) return "redirect:/login";
        
        model.addAttribute("doctorId", doctorId);
        return "medical-test-patients"; // The new HTML we will create
    }

@GetMapping("/add-medical-test")
    public String showAddTestForm(@RequestParam String patientId, HttpSession session, Model model) {
        String doctorId = (String) session.getAttribute("doctorId");
        if (doctorId == null) return "redirect:/login";

        model.addAttribute("doctorId", doctorId);
        model.addAttribute("targetPatientId", patientId);
        return "add-medical-test"; 
    }

@GetMapping("/view-patient-tests")
public String showPatientTests(@RequestParam String patientId, HttpSession session, Model model) {
    String doctorId = (String) session.getAttribute("doctorId");
    if (doctorId == null) return "redirect:/login";

    model.addAttribute("doctorId", doctorId);
    model.addAttribute("targetPatientId", patientId);
    return "view-patient-tests"; // Points to the new HTML
}

@GetMapping("/inventory")
public String showInventoryPage(HttpSession session, Model model) {
    String doctorId = (String) session.getAttribute("doctorId");
    if (doctorId == null) {
        return "redirect:/login";
    }
    model.addAttribute("doctorId", doctorId);
    model.addAttribute("doctorName", session.getAttribute("doctorName"));
    return "inventory"; // This will point to inventory.html
}
@PostMapping("/api/doctors/logout")
public String logoutAndRedirect(HttpSession session) {
    session.invalidate(); //
    return "redirect:/login"; // Spring handles the redirect automatically
}
@GetMapping("/patient/dashboard")
public String showPatientDashboard(HttpSession session, Model model) {
    String patientId = (String) session.getAttribute("patientId");
    
    // Safety Check: If session is empty, send them back to login
    if (patientId == null) {
        return "redirect:/patient-login"; 
    }
    
    // Inject session data into the HTML
    model.addAttribute("patientId", patientId);
    model.addAttribute("patientName", session.getAttribute("patientName"));
    
    return "patient-dashboard"; // This must match your HTML filename
}

@GetMapping("/patient/clinics")
public String listClinicsPage(HttpSession session, Model model) {
    if (session.getAttribute("patientId") == null) return "redirect:/login";
    return "patient-clinics-list";
}

@GetMapping("/patient/clinics/{clinicId}")
public String clinicDetailsPage(@PathVariable String clinicId, HttpSession session, Model model) {
    if (session.getAttribute("patientId") == null) return "redirect:/login";
    model.addAttribute("clinicId", clinicId);
    return "patient-clinic-details";
}

@GetMapping("/patient/doctors")
public String listDoctorsPage(HttpSession session) {
    if (session.getAttribute("patientId") == null) return "redirect:/login";
    return "patient-doctors-list";
}

@GetMapping("/patient/doctor-details/{id}")
public String doctorDetailsPage(@PathVariable String id, HttpSession session, Model model) {
    if (session.getAttribute("patientId") == null) return "redirect:/login";
    model.addAttribute("doctorId", id);
    return "patient-doctor-details";
}
@GetMapping("/patient/my-prescriptions")
public String myPrescriptionsPage(HttpSession session, Model model) {
    String pId = (String) session.getAttribute("patientId");
    if (pId == null) return "redirect:/login"; 
    
    // ✅ Critical: This makes th:value="${patientId}" work in HTML
    model.addAttribute("patientId", pId);
    model.addAttribute("patientName", session.getAttribute("patientName"));
    return "patient-prescriptions-list"; 
}

@GetMapping("/patient/prescription-details")
    public String prescriptionDetailsPage(@RequestParam String id, 
                                          @RequestParam String patientId, 
                                          HttpSession session, 
                                          Model model) {
        // Security check: Ensure user is logged in
        if (session.getAttribute("patientId") == null) {
            return "redirect:/login";
        }

        // Pass parameters to the view so the JavaScript can use them for API calls
        model.addAttribute("prescriptionId", id);
        model.addAttribute("patientId", patientId);
        
        return "prescription-details"; // Must match prescription-details.html in /templates/
    }

@GetMapping("/patient/my-lab-tests")
    public String showPatientLabTests(@RequestParam(name = "patientId", required = false) String patientId, Model model) {
        
        // You can pass the patientId back to the view if you need it for data fetching
        model.addAttribute("patientId", patientId);
        
        // This tells Spring to load "patient-lab-tests.html" from the templates folder
        return "patient-lab-tests"; 
    }

@GetMapping("/")
public String homePage() {
    return "login"; 
}

@GetMapping("/akshata-ai")
    public String showAiChatPage() {
        // This tells Spring to look for "akshata-ai.html" in the templates folder
        return "akshata-ai"; 
    }

@GetMapping("/ai")
    public String showAiPage() {
        // This tells Spring to look for "akshata-ai.html" in the templates folder
        return "ai"; 
    }

}