package com.example.springcrud.model;

import java.time.OffsetDateTime;
import java.util.List;

import org.springframework.data.annotation.Id;
import org.springframework.data.mongodb.core.mapping.Document;

import com.fasterxml.jackson.annotation.JsonFormat;
import com.fasterxml.jackson.annotation.JsonIgnoreProperties;

@Document(collection = "prescriptions")
@JsonIgnoreProperties(ignoreUnknown = true)
public class Prescription {

    @Id
    private String id;

    private String recordStatus; 
    private boolean doctorChangeAllowed;

    private PatientInfo patient; 
    private CurrentDoctor currentDoctor;
    private Diagnosis diagnosis;
    private TreatmentTimeline treatmentTimeline;
    private List<MedicationItem> medications;
    private List<ProcedureItem> procedures;
    private List<InvestigationItem> investigations;
    private List<PreviousDoctorRecord> previousDoctors;
    private FollowUp followUp;
    private AuditInfo audit;

    public Prescription() {}

    // --- Inner Classes for Nested Objects ---

    public static class PatientInfo {
        private String patientId;
        public PatientInfo() {} // Necessary for Jackson
        public String getPatientId() { return patientId; }
        public void setPatientId(String patientId) { this.patientId = patientId; }
    }

    public static class CurrentDoctor {
        private String doctorId;
        private String fullName;
        private String specialization;
        public CurrentDoctor() {} 
        public String getDoctorId() { return doctorId; }
        public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
        public String getFullName() { return fullName; }
        public void setFullName(String fullName) { this.fullName = fullName; }
        public String getSpecialization() { return specialization; }
        public void setSpecialization(String specialization) { this.specialization = specialization; }
    }

    public static class Diagnosis {
        private String provisionalDiagnosis;
        private String confirmedDiagnosis;
        private String icdCode;
        private String severity;
        public Diagnosis() {}
        public String getProvisionalDiagnosis() { return provisionalDiagnosis; }
        public void setProvisionalDiagnosis(String provisionalDiagnosis) { this.provisionalDiagnosis = provisionalDiagnosis; }
        public String getConfirmedDiagnosis() { return confirmedDiagnosis; }
        public void setConfirmedDiagnosis(String confirmedDiagnosis) { this.confirmedDiagnosis = confirmedDiagnosis; }
        public String getIcdCode() { return icdCode; }
        public void setIcdCode(String icdCode) { this.icdCode = icdCode; }
        public String getSeverity() { return severity; }
        public void setSeverity(String severity) { this.severity = severity; }
    }

    public static class TreatmentTimeline {
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        private OffsetDateTime treatmentStartDate;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        private OffsetDateTime expectedCompletionDate;
        private String treatmentStatus;
        private String remarks;
        public TreatmentTimeline() {}
        public OffsetDateTime getTreatmentStartDate() { return treatmentStartDate; }
        public void setTreatmentStartDate(OffsetDateTime treatmentStartDate) { this.treatmentStartDate = treatmentStartDate; }
        public OffsetDateTime getExpectedCompletionDate() { return expectedCompletionDate; }
        public void setExpectedCompletionDate(OffsetDateTime expectedCompletionDate) { this.expectedCompletionDate = expectedCompletionDate; }
        public String getTreatmentStatus() { return treatmentStatus; }
        public void setTreatmentStatus(String treatmentStatus) { this.treatmentStatus = treatmentStatus; }
        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
    }

    public static class MedicationItem {
        private String medicineId;
        private String medicineName;
        private String dosage;
        private String route;
        private String frequency;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        private OffsetDateTime startDate;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        private OffsetDateTime endDate;
        private String instructions;
        public MedicationItem() {}
        public String getMedicineId() { return medicineId; }
        public void setMedicineId(String medicineId) { this.medicineId = medicineId; }
        public String getMedicineName() { return medicineName; }
        public void setMedicineName(String medicineName) { this.medicineName = medicineName; }
        public String getDosage() { return dosage; }
        public void setDosage(String dosage) { this.dosage = dosage; }
        public String getRoute() { return route; }
        public void setRoute(String route) { this.route = route; }
        public String getFrequency() { return frequency; }
        public void setFrequency(String frequency) { this.frequency = frequency; }
        public OffsetDateTime getStartDate() { return startDate; }
        public void setStartDate(OffsetDateTime startDate) { this.startDate = startDate; }
        public OffsetDateTime getEndDate() { return endDate; }
        public void setEndDate(OffsetDateTime endDate) { this.endDate = endDate; }
        public String getInstructions() { return instructions; }
        public void setInstructions(String instructions) { this.instructions = instructions; }
    }

    public static class ProcedureItem {
        private String procedureName;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        private OffsetDateTime startDate;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        private OffsetDateTime endDate;
        private Integer sessions;
        private String outcome;
        public ProcedureItem() {}
        public String getProcedureName() { return procedureName; }
        public void setProcedureName(String procedureName) { this.procedureName = procedureName; }
        public OffsetDateTime getStartDate() { return startDate; }
        public void setStartDate(OffsetDateTime startDate) { this.startDate = startDate; }
        public OffsetDateTime getEndDate() { return endDate; }
        public void setEndDate(OffsetDateTime endDate) { this.endDate = endDate; }
        public Integer getSessions() { return sessions; }
        public void setSessions(Integer sessions) { this.sessions = sessions; }
        public String getOutcome() { return outcome; }
        public void setOutcome(String outcome) { this.outcome = outcome; }
    }

    public static class InvestigationItem {
        private String testName;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        private OffsetDateTime advisedDate;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        private OffsetDateTime performedDate;
        private String resultSummary;
        private String remarks;
        public InvestigationItem() {}
        public String getTestName() { return testName; }
        public void setTestName(String testName) { this.testName = testName; }
        public OffsetDateTime getAdvisedDate() { return advisedDate; }
        public void setAdvisedDate(OffsetDateTime advisedDate) { this.advisedDate = advisedDate; }
        public OffsetDateTime getPerformedDate() { return performedDate; }
        public void setPerformedDate(OffsetDateTime performedDate) { this.performedDate = performedDate; }
        public String getResultSummary() { return resultSummary; }
        public void setResultSummary(String resultSummary) { this.resultSummary = resultSummary; }
        public String getRemarks() { return remarks; }
        public void setRemarks(String remarks) { this.remarks = remarks; }
    }

    public static class PreviousDoctorRecord {
        private DoctorDetail doctor;
        private String changeReason;
        private String changedBy;
        private boolean active;
        public PreviousDoctorRecord() {}

        public static class DoctorDetail {
            private String doctorId;
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            private OffsetDateTime assignedFrom;
            @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
            private OffsetDateTime assignedTo;
            public DoctorDetail() {}
            public String getDoctorId() { return doctorId; }
            public void setDoctorId(String doctorId) { this.doctorId = doctorId; }
            public OffsetDateTime getAssignedFrom() { return assignedFrom; }
            public void setAssignedFrom(OffsetDateTime assignedFrom) { this.assignedFrom = assignedFrom; }
            public OffsetDateTime getAssignedTo() { return assignedTo; }
            public void setAssignedTo(OffsetDateTime assignedTo) { this.assignedTo = assignedTo; }
        }
        public DoctorDetail getDoctor() { return doctor; }
        public void setDoctor(DoctorDetail doctor) { this.doctor = doctor; }
        public String getChangeReason() { return changeReason; }
        public void setChangeReason(String changeReason) { this.changeReason = changeReason; }
        public String getChangedBy() { return changedBy; }
        public void setChangedBy(String changedBy) { this.changedBy = changedBy; }
        public boolean isActive() { return active; }
        public void setActive(boolean active) { this.active = active; }
    }

    public static class FollowUp {
        private boolean followUpRequired;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        private OffsetDateTime followUpDate;
        private String notes;
        public FollowUp() {}
        public boolean isFollowUpRequired() { return followUpRequired; }
        public void setFollowUpRequired(boolean followUpRequired) { this.followUpRequired = followUpRequired; }
        public OffsetDateTime getFollowUpDate() { return followUpDate; }
        public void setFollowUpDate(OffsetDateTime followUpDate) { this.followUpDate = followUpDate; }
        public String getNotes() { return notes; }
        public void setNotes(String notes) { this.notes = notes; }
    }

    public static class AuditInfo {
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        private OffsetDateTime createdAt;
        @JsonFormat(pattern = "yyyy-MM-dd'T'HH:mm:ss.SSSXXX")
        private OffsetDateTime lastUpdatedAt;
        private String createdBy;
        public AuditInfo() {}
        public OffsetDateTime getCreatedAt() { return createdAt; }
        public void setCreatedAt(OffsetDateTime createdAt) { this.createdAt = createdAt; }
        public OffsetDateTime getLastUpdatedAt() { return lastUpdatedAt; }
        public void setLastUpdatedAt(OffsetDateTime lastUpdatedAt) { this.lastUpdatedAt = lastUpdatedAt; }
        public String getCreatedBy() { return createdBy; }
        public void setCreatedBy(String createdBy) { this.createdBy = createdBy; }
    }

    // --- Main Prescription Getters & Setters ---

    public String getId() { return id; }
    public void setId(String id) { this.id = id; }
    public String getRecordStatus() { return recordStatus; }
    public void setRecordStatus(String recordStatus) { this.recordStatus = recordStatus; }
    public boolean isDoctorChangeAllowed() { return doctorChangeAllowed; }
    public void setDoctorChangeAllowed(boolean doctorChangeAllowed) { this.doctorChangeAllowed = doctorChangeAllowed; }
    public PatientInfo getPatient() { return patient; }
    public void setPatient(PatientInfo patient) { this.patient = patient; }
    public CurrentDoctor getCurrentDoctor() { return currentDoctor; }
    public void setCurrentDoctor(CurrentDoctor currentDoctor) { this.currentDoctor = currentDoctor; }
    public Diagnosis getDiagnosis() { return diagnosis; }
    public void setDiagnosis(Diagnosis diagnosis) { this.diagnosis = diagnosis; }
    public TreatmentTimeline getTreatmentTimeline() { return treatmentTimeline; }
    public void setTreatmentTimeline(TreatmentTimeline treatmentTimeline) { this.treatmentTimeline = treatmentTimeline; }
    public List<MedicationItem> getMedications() { return medications; }
    public void setMedications(List<MedicationItem> medications) { this.medications = medications; }
    public List<ProcedureItem> getProcedures() { return procedures; }
    public void setProcedures(List<ProcedureItem> procedures) { this.procedures = procedures; }
    public List<InvestigationItem> getInvestigations() { return investigations; }
    public void setInvestigations(List<InvestigationItem> investigations) { this.investigations = investigations; }
    public List<PreviousDoctorRecord> getPreviousDoctors() { return previousDoctors; }
    public void setPreviousDoctors(List<PreviousDoctorRecord> previousDoctors) { this.previousDoctors = previousDoctors; }
    public FollowUp getFollowUp() { return followUp; }
    public void setFollowUp(FollowUp followUp) { this.followUp = followUp; }
    public AuditInfo getAudit() { return audit; }
    public void setAudit(AuditInfo audit) { this.audit = audit; }
}