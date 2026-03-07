/**
 * Doctor Dashboard & Healthcare Management Logic
 * File: src/main/resources/static/js/app.js
 */

document.addEventListener('DOMContentLoaded', () => {

    // 1️⃣ Dashboard Initialization
    if (document.getElementById('patientCount') || document.getElementById('doctorCount') || document.getElementById('clinicCount')) {
        fetchStats();
    }

    // 2️⃣ Patient List Page
    if (document.getElementById('patientTableBody')) {
        loadPatientInfo();
    }

    // 3️⃣ Clinic List Page
    if (document.getElementById('doctorClinicTable')) {
        loadDoctorClinicList();
    }

    // 4️⃣ Prescription Page Init  ✅ NEW
    if (document.getElementById('prescriptionForm')) {
        initPrescriptionPage();
    }

    // 5️⃣ Form Listeners
    const patientForm = document.getElementById('patientForm');
    if (patientForm) patientForm.addEventListener('submit', handlePatientSubmit);

    const profileForm = document.getElementById('profileUpdateForm');
    if (profileForm) profileForm.addEventListener('submit', handleProfileUpdate);

    const clinicForm = document.getElementById('clinicForm');
    if (clinicForm) clinicForm.addEventListener('submit', handleClinicSubmit);

    const prescriptionForm = document.getElementById('prescriptionForm');
    if (prescriptionForm) prescriptionForm.addEventListener('submit', handlePrescriptionSubmit);
});


// =====================================================
// ✅ PRESCRIPTION PAGE INIT (NEW)
// =====================================================

function initPrescriptionPage() {

    const params = new URLSearchParams(window.location.search);
    const patientId = params.get("patientId");

    if (!patientId) {
        alert("No patient selected!");
        window.location.href = "/patientinfo";
        return;
    }

    const pidField = document.getElementById("linkPatientId");
    const label = document.getElementById("activePatientLabel");

    if (pidField) pidField.value = patientId;
    if (label) label.innerText = patientId;

    const docNameInput = document.getElementById("displayDocName");
    const docNameHidden = document.getElementById("doctorNameDisplay");
    if (docNameInput && docNameHidden) {
        docNameHidden.innerText = docNameInput.value;
    }

    addMedicationRow();
}


// =====================================================
// 📊 DASHBOARD
// =====================================================

async function fetchStats() {
    const doctorId = document.getElementById('profId')?.value;

    try {
        if (doctorId) {
            const patientRes = await fetch(`/api/patients/doctor/${doctorId}`);
            if (patientRes.ok) {
                const patients = await patientRes.json();
                document.getElementById('patientCount')?.innerText = patients.length;
            }

            const clinicRes = await fetch(`/api/clinics/doctor/${doctorId}`);
            if (clinicRes.ok) {
                const clinics = await clinicRes.json();
                document.getElementById('clinicCount')?.innerText = clinics.length;
            }
        }

        const doctorRes = await fetch('/api/doctors');
        if (doctorRes.ok) {
            const doctors = await doctorRes.json();
            document.getElementById('doctorCount')?.innerText = doctors.length;
        }

    } catch (error) {
        console.error('Error fetching stats:', error);
    }
}


// =====================================================
// 👥 PATIENT LIST
// =====================================================

/**
 * Fetches and displays patients associated with the logged-in doctor
 */
async function loadPatientInfo() {
    const tableBody = document.getElementById('patientTableBody');
    const loader = document.getElementById('tableLoader');
    if (!tableBody) return;

    const doctorId = document.getElementById('profId')?.value;

    if (!doctorId || doctorId === "") {
        if (loader) loader.innerHTML = `<p class="text-red-500 text-sm font-bold p-10">Session Error: Please login again.</p>`;
        return;
    }

    try {
        const response = await fetch(`/api/patients/doctor/${doctorId}`);
        const patients = await response.json();

        if (loader) loader.classList.add('hidden');
        tableBody.innerHTML = "";

        if (!patients || patients.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="5" class="p-20 text-center text-slate-400 font-medium">No patients found linked to your account.</td></tr>`;
            return;
        }

        // Render patients with corrected navigation to add-prescription
        tableBody.innerHTML = patients.reverse().map(p => `
            <tr class="hover:bg-slate-50 transition-all border-b border-slate-100">
                <td class="px-6 py-4 text-xs font-mono font-bold text-indigo-600">${p.patientId || 'N/A'}</td>
                <td class="px-6 py-4 font-bold text-slate-800">${p.fullName}</td>
                <td class="px-6 py-4 text-sm text-slate-600">${p.phone || 'N/A'}</td>
                <td class="px-6 py-4">
                    <span class="px-2.5 py-1 bg-red-50 text-red-600 rounded-lg text-xs font-bold border border-red-100">
                        ${p.bloodGroup || '--'}
                    </span>
                </td>
                <td class="px-6 py-4 text-right">
                    <button onclick="window.location.href='/add-prescription?patientId=${p.patientId}'" 
                            class="bg-blue-600 hover:bg-blue-700 text-white px-4 py-2 rounded-lg text-[10px] font-bold uppercase tracking-widest transition-all shadow-md">
                        <i class="fas fa-file-medical mr-1"></i> Prescribe
                    </button>
                </td>
            </tr>`).join('');
    } catch (err) {
        console.error("Fetch Error:", err);
        if (loader) loader.innerHTML = `<p class="text-red-500 font-bold p-10">Failed to load data from server.</p>`;
    }
}

// =====================================================
// 🏥 CLINICS
// =====================================================

async function loadDoctorClinicList() {
    const doctorId = document.getElementById('profId')?.value;
    if (!doctorId) return;

    const response = await fetch(`/api/clinics/doctor/${doctorId}`);
    const clinics = await response.json();

    const tableBody = document.getElementById('doctorClinicTable');

    tableBody.innerHTML = clinics.map(c => `
        <tr>
            <td>${c.clinicId}</td>
            <td>${c.clinicName}</td>
            <td>${c.address?.city || ''}</td>
            <td>${c.clinicType}</td>
            <td>${c.status}</td>
        </tr>
    `).join('');
}


// =====================================================
// 💊 MEDICATION ROW BUILDER (NEW)
// =====================================================

function addMedicationRow() {

    const container = document.getElementById('medicationContainer');
    if (!container) return;

    const row = document.createElement('div');
    row.className = "med-row grid grid-cols-12 gap-3";

    row.innerHTML = `
        <input class="med-name col-span-3 border p-2" placeholder="Medicine" required>
        <input class="med-dosage col-span-2 border p-2" placeholder="Dosage" required>
        <input class="med-freq col-span-3 border p-2" placeholder="Frequency" required>
        <input class="med-inst col-span-3 border p-2" placeholder="Instructions">
        <button type="button" onclick="this.parentElement.remove()">❌</button>
    `;

    container.appendChild(row);
}


// =====================================================
// 📝 PRESCRIPTION SUBMIT
// =====================================================

async function handlePrescriptionSubmit(event) {

    event.preventDefault();

    const doctorId = document.getElementById('profId')?.value;
    const patientId = document.getElementById('linkPatientId')?.value;

    const payload = {
        patient: { patientId },
        currentDoctor: { doctorId },
        diagnosis: {
            confirmedDiagnosis: document.getElementById('confirmedDiagnosis').value,
            severity: document.getElementById('severity').value
        },
        medications: Array.from(document.querySelectorAll('.med-row')).map(r => ({
            medicineName: r.querySelector('.med-name').value,
            dosage: r.querySelector('.med-dosage').value,
            frequency: r.querySelector('.med-freq').value,
            instructions: r.querySelector('.med-inst').value
        }))
    };

    const res = await fetch('/api/prescriptions', {
        method: 'POST',
        headers: {'Content-Type':'application/json'},
        body: JSON.stringify(payload)
    });

    if (res.ok) {
        alert("Prescription Saved");
        window.location.href="/patientinfo";
    }
}


// =====================================================
// 👤 PATIENT CREATE
// =====================================================

// =====================================================
// 👤 PATIENT CREATE (FIXED)
// =====================================================

async function handlePatientSubmit(e){
    e.preventDefault();

    const doctorId = document.getElementById('profId')?.value;

    const payload = {
        fullName: document.getElementById('fullName').value,
        dateOfBirth: document.getElementById('dateOfBirth').value,
        gender: document.getElementById('gender').value,
        phone: document.getElementById('phoneNumber').value,
        emailAddress: document.getElementById('emailAddress').value,
        residentialAddress: document.getElementById('residentialAddress').value,
        bloodGroup: document.getElementById('bloodGroup').value,
        height: document.getElementById('height').value,
        weight: document.getElementById('weight').value,
        allergies: document.getElementById('allergies').value
            ? document.getElementById('allergies').value.split(',').map(a => a.trim())
            : [],
        chronicDiseases: document.getElementById('chronicDiseases').value
            ? document.getElementById('chronicDiseases').value.split(',').map(c => c.trim())
            : []
    };

    try {
        const res = await fetch(`/api/patients/doctor/${doctorId}`, {
            method:'POST',
            headers:{'Content-Type':'application/json'},
            body: JSON.stringify(payload)
        });

        if(res.ok){
            window.location.href='/patientinfo';
        } else {
            alert("Failed to save patient");
        }

    } catch(err){
        console.error(err);
        alert("Server error");
    }
}

