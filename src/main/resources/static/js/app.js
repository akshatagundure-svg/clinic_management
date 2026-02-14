/**
 * Doctor Dashboard & Patient Management Logic
 * File: src/main/resources/static/js/app.js
 */

document.addEventListener('DOMContentLoaded', () => {
    // 1. Dashboard Initialization (index.html)
    // Only runs if the specific dashboard stat elements exist
    if (document.getElementById('patientCount')) {
        fetchStats();
        // loadRecentPatients(); // Optional: Keep disabled if the table is removed from HTML
    }

    // 2. Full Patient List Initialization (patientinfo.html)
    if (document.getElementById('fullPatientTable')) {
        loadFullPatientList();
    }

    // 3. Full Doctor List Initialization (doctorinfo.html)
    if (document.getElementById('fullDoctorTable')) {
        loadFullDoctorList();
    }

    // 4. Add Patient Form Listener (add-patient.html)
    const patientForm = document.getElementById('patientForm');
    if (patientForm) {
        patientForm.addEventListener('submit', handlePatientSubmit);
    }

    // 5. Add Doctor Form Listener (add-doctor.html)
    const doctorForm = document.getElementById('doctorForm');
    if (doctorForm) {
        doctorForm.addEventListener('submit', handleDoctorSubmit);
    }
});

// =====================================================
// 📊 API FETCHERS & DASHBOARD FUNCTIONS
// =====================================================

/**
 * Fetch counts for the dashboard cards
 */
async function fetchStats() {
    try {
        const patientRes = await fetch('/api/patients/count');
        const patientCount = await patientRes.json();
        const pElem = document.getElementById('patientCount');
        if (pElem) pElem.innerText = patientCount;

        const doctorRes = await fetch('/api/doctors/count');
        const doctorCount = await doctorRes.json();
        const dElem = document.getElementById('doctorCount');
        if (dElem) dElem.innerText = doctorCount;
    } catch (error) {
        console.error('Error fetching stats:', error);
    }
}

/**
 * Load the comprehensive patient list for patientinfo.html
 */
async function loadFullPatientList() {
    const tableBody = document.getElementById('fullPatientTable');
    if (!tableBody) return;

    try {
        const response = await fetch('/api/patients');
        const patients = await response.json();

        if (patients.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="6" class="p-10 text-center text-gray-500">No records found.</td></tr>`;
            return;
        }

        // Display all patients, newest at the top
        tableBody.innerHTML = patients.reverse().map(p => `
            <tr class="hover:bg-gray-50 transition border-b border-gray-100">
                <td class="px-6 py-4 text-xs font-mono text-indigo-600 font-bold">${p.patientId || 'N/A'}</td>
                <td class="px-6 py-4 font-medium text-gray-800">${p.fullName}</td>
                <td class="px-6 py-4 text-sm text-gray-600">${p.phoneNumber || 'N/A'}</td>
                <td class="px-6 py-4">
                    <span class="px-2 py-1 bg-red-100 text-red-700 rounded text-xs font-bold">${p.bloodGroup || 'UNK'}</span>
                </td>
                <td class="px-6 py-4 text-xs text-gray-500">${p.height ? p.height + 'cm' : '-'} / ${p.weight ? p.weight + 'kg' : '-'}</td>
                <td class="px-6 py-4 text-right">
                    <button class="text-indigo-600 hover:text-indigo-900 text-sm font-semibold">View Details</button>
                </td>
            </tr>
        `).join('');
    } catch (e) {
        console.error("Error loading full patient database:", e);
        tableBody.innerHTML = `<tr><td colspan="6" class="p-6 text-red-500 text-center">Error loading database.</td></tr>`;
    }
}

/**
 * Load the comprehensive doctor list for doctorinfo.html
 */
async function loadFullDoctorList() {
    const tableBody = document.getElementById('fullDoctorTable');
    if (!tableBody) return;

    try {
        const response = await fetch('/api/doctors');
        const doctors = await response.json();

        if (doctors.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="6" class="p-10 text-center text-gray-500">No doctors registered.</td></tr>`;
            return;
        }

        tableBody.innerHTML = doctors.map(d => `
            <tr class="hover:bg-gray-50 transition border-b border-gray-100">
                <td class="px-6 py-4 font-medium text-gray-800">
                    <div>${d.name}</div>
                    <div class="text-xs text-gray-400 font-normal">${d.qualification ? d.qualification.join(', ') : ''}</div>
                </td>
                <td class="px-6 py-4 text-sm text-gray-600">${d.specialization}</td>
                <td class="px-6 py-4 text-sm text-gray-600">${d.experience} Years</td>
                <td class="px-6 py-4 text-sm text-gray-600">${d.hospitalName}</td>
                <td class="px-6 py-4 text-sm text-gray-600">
                    <div>${d.phone}</div>
                    <div class="text-xs text-blue-500">${d.email}</div>
                </td>
                <td class="px-6 py-4 text-right">
                    <button class="text-indigo-600 hover:text-indigo-900 text-sm font-semibold">Edit</button>
                </td>
            </tr>
        `).join('');
    } catch (e) {
        console.error("Error loading doctor database:", e);
        tableBody.innerHTML = `<tr><td colspan="6" class="p-6 text-red-500 text-center">Error loading doctor database.</td></tr>`;
    }
}

// =====================================================
// 📝 FORM HANDLER FUNCTIONS
// =====================================================

async function handlePatientSubmit(event) {
    event.preventDefault();
    const splitAndClean = (str) => str ? str.split(',').map(s => s.trim()).filter(s => s !== "") : [];

    const patientData = {
        fullName: document.getElementById('fullName').value,
        dateOfBirth: document.getElementById('dateOfBirth').value,
        gender: document.getElementById('gender').value,
        phoneNumber: document.getElementById('phoneNumber').value,
        emailAddress: document.getElementById('emailAddress').value,
        bloodGroup: document.getElementById('bloodGroup').value,
        residentialAddress: document.getElementById('residentialAddress').value,
        height: parseFloat(document.getElementById('height').value) || 0.0,
        weight: parseFloat(document.getElementById('weight').value) || 0.0,
        allergies: splitAndClean(document.getElementById('allergies').value),
        chronicDiseases: splitAndClean(document.getElementById('chronicDiseases').value),
        currentMedications: [] 
    };

    try {
        const response = await fetch('/api/patients', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(patientData)
        });

        if (response.ok) {
            const saved = await response.json();
            alert(`Patient Added! ID: ${saved.patientId}`);
            window.location.href = '/patientinfo'; 
        } else {
            alert("Failed to save patient profile.");
        }
    } catch (error) {
        alert("Could not connect to the server.");
    }
}

async function handleDoctorSubmit(event) {
    event.preventDefault();

    const doctorData = {
        name: document.getElementById('name').value,
        specialization: document.getElementById('specialization').value,
        experience: parseInt(document.getElementById('experience').value),
        qualification: document.getElementById('qualification').value.split(',').map(s => s.trim()),
        gender: document.getElementById('gender').value,
        phone: document.getElementById('phone').value,
        email: document.getElementById('email').value,
        consultationFee: parseFloat(document.getElementById('consultationFee').value),
        availability: document.getElementById('availability').value,
        hospitalName: document.getElementById('hospitalName').value,
        address: document.getElementById('address').value
    };

    try {
        const response = await fetch('/api/doctors', {
            method: 'POST',
            headers: { 'Content-Type': 'application/json' },
            body: JSON.stringify(doctorData)
        });

        if (response.ok) {
            alert('Doctor profile created successfully!');
            window.location.href = '/doctordb';
        } else {
            const errorText = await response.text();
            alert('Failed to save doctor profile: ' + errorText);
        }
    } catch (err) {
        console.error('Network error:', err);
        alert('Network error while saving doctor profile.');
    }
}