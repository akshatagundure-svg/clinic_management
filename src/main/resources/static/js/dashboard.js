/**
 * dashboard.js
 * Core logic for the HealthFlow Pro SaaS Dashboard
 * Optimized for Search, Edit, and Delete synchronization
 */

document.addEventListener('DOMContentLoaded', () => {
    initSaaSDashboard();
});

// Base URL for the Spring Boot Backend
const API_BASE = "http://localhost:8080";

/**
 * Concurrent fetching for all 6 modular metric cards and initial list views
 */
async function initSaaSDashboard() {
    try {
        const [docs, pats, prescriptions, tests, meds, availability] = await Promise.all([
            fetch(`${API_BASE}/api/doctors/count`).then(res => res.json()),
            fetch(`${API_BASE}/api/patients/count`).then(res => res.json()),
            fetch(`${API_BASE}/api/prescriptions`).then(res => res.json()),
            fetch(`${API_BASE}/api/medical-tests`).then(res => res.json()),
            fetch(`${API_BASE}/api/medicines`).then(res => res.json()),
            fetch(`${API_BASE}/api/doctor-availability/filter?isAvailable=true`).then(res => res.json())
        ]);

        // 1. Update Metric Card Counts
        setCount('docCount', docs);
        setCount('patCount', pats);
        setCount('presCount', prescriptions.length);
        setCount('testCount', tests.length); 
        setCount('medsCount', meds.length);
        setCount('availCount', availability.length);

        // 2. Initial rendering of default view (Latest Lab Activity)
        renderTests(tests);

    } catch (error) {
        console.error("Dashboard Sync Error:", error);
        const ids = ['docCount', 'patCount', 'presCount', 'testCount', 'medsCount', 'availCount'];
        ids.forEach(id => {
            const el = document.getElementById(id);
            if (el) el.innerText = "!";
        });
        showTableError("System Sync Failed. Please ensure the Backend is running.");
    }
}

/**
 * Helper to update counter elements safely
 */
function setCount(id, value) {
    const el = document.getElementById(id);
    if (el) {
        el.innerText = value !== undefined ? value : 0;
    }
}

/**
 * Displays a professional error message inside the main dashboard table
 */
function showTableError(message) {
    const tableBody = document.getElementById('mainTableBody');
    if (tableBody) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="5" class="text-center py-12 bg-white">
                    <div class="text-red-500 font-bold mb-1"><i class="fas fa-exclamation-circle mr-2"></i> ${message}</div>
                    <div class="text-[10px] text-slate-400 uppercase tracking-widest font-semibold">Service Unavailable</div>
                </td>
            </tr>`;
    }
}

/**
 * REUSABLE ACTION BUTTONS COMPONENT
 */
function getActionButtons(id, type, name) {
    return `
        <div class="flex items-center justify-end space-x-2">
            <button onclick="editItem('${type}', '${id}')" class="p-2 text-indigo-600 hover:bg-indigo-50 rounded-lg transition-colors" title="Edit">
                <i class="fas fa-edit text-xs"></i>
            </button>
            <button onclick="deleteItem('${type}', '${id}', '${name}')" class="p-2 text-rose-600 hover:bg-rose-50 rounded-lg transition-colors" title="Delete">
                <i class="fas fa-trash text-xs"></i>
            </button>
        </div>`;
}

// --- VIEW ALL & SEARCH HANDLERS ---

/**
 * NEW: Updated to handle optional search parameters
 */
async function viewAllDoctors(searchParams = null) {
    try {
        let url = `${API_BASE}/api/doctors`;
        
        // If searchParams exist (from the search bar), use the search endpoint
        if (searchParams) {
            const query = new URLSearchParams(searchParams).toString();
            url = `${API_BASE}/api/doctors/search?${query}`;
        }

        const res = await fetch(url);
        const data = await res.json();
        
        updateTableUI("Doctor Directory", ["Name", "Specialization", "Contact", "Actions"]);
        
        const tableBody = document.getElementById('mainTableBody');
        if (data.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="4" class="text-center py-10 text-slate-400">No doctors found.</td></tr>`;
            return;
        }

        tableBody.innerHTML = data.map(d => `
            <tr class="hover:bg-slate-50 transition-colors">
                <td class="px-6 py-4 font-semibold text-slate-700">${d.name}</td>
                <td class="px-6 py-4"><span class="bg-indigo-50 text-indigo-600 text-[10px] font-bold px-2 py-1 rounded-lg">${d.specialization}</span></td>
                <td class="px-6 py-4 text-slate-500 text-sm font-mono">${d.phone}</td>
                <td class="px-6 py-4">${getActionButtons(d.id, 'doctors', d.name)}</td>
            </tr>
        `).join('');
    } catch (err) { showTableError("Unable to load Doctors."); }
}

/**
 * TRIGGER FUNCTION: Called from your Search Button
 */
function handleDoctorSearch() {
    const params = {
        name: document.getElementById('searchName')?.value || '',
        specialization: document.getElementById('searchSpec')?.value || '',
        minExp: document.getElementById('searchMinExp')?.value || ''
    };
    // Only send parameters that have values
    const activeFilters = Object.fromEntries(Object.entries(params).filter(([_, v]) => v !== ''));
    viewAllDoctors(activeFilters);
}

async function viewAllPatients() {
    try {
        const res = await fetch(`${API_BASE}/api/patients`);
        const data = await res.json();
        updateTableUI("Patient Registry", ["Full Name", "Patient ID", "Phone", "Actions"]);
        document.getElementById('mainTableBody').innerHTML = data.map(p => `
            <tr class="hover:bg-slate-50 transition-colors">
                <td class="px-6 py-4 font-semibold text-slate-700">${p.fullName}</td>
                <td class="px-6 py-4 font-mono text-xs text-slate-500">${p.patientId}</td>
                <td class="px-6 py-4 text-slate-600 text-sm">${p.phoneNumber}</td>
                <td class="px-6 py-4">${getActionButtons(p.id, 'patients', p.fullName)}</td>
            </tr>
        `).join('');
    } catch (err) { showTableError("Unable to load Patients."); }
}

async function viewAllAvailability() {
    try {
        const res = await fetch(`${API_BASE}/api/doctor-availability`);
        const data = await res.json();
        updateTableUI("Shift Schedule", ["Doctor ID", "Day", "Time Slot", "Status", "Actions"]);
        document.getElementById('mainTableBody').innerHTML = data.map(a => `
            <tr class="hover:bg-slate-50 transition-colors">
                <td class="px-6 py-4 font-mono text-sm text-indigo-600 font-bold">${a.doctorId}</td>
                <td class="px-6 py-4 text-slate-700 font-medium">${a.day}</td>
                <td class="px-6 py-4 text-slate-500 text-sm">${a.startTime} - ${a.endTime}</td>
                <td class="px-6 py-4">
                    <span class="text-[10px] font-bold px-2 py-1 rounded-full ${a.available ? 'bg-emerald-50 text-emerald-600' : 'bg-amber-50 text-amber-600'}">
                        ● ${a.available ? 'ONLINE' : 'OFFLINE'}
                    </span>
                </td>
                <td class="px-6 py-4">${getActionButtons(a.id, 'doctor-availability', a.doctorId)}</td>
            </tr>
        `).join('');
    } catch (err) { showTableError("Unable to load Availability."); }
}

async function viewAllPrescriptions() {
    try {
        const res = await fetch(`${API_BASE}/api/prescriptions`);
        const data = await res.json();
        updateTableUI("Prescription Registry", ["Patient ID", "Diagnosis", "Status", "Actions"]);
        document.getElementById('mainTableBody').innerHTML = data.map(p => `
            <tr class="hover:bg-slate-50 transition-colors">
                <td class="px-6 py-4 font-mono text-xs text-indigo-600 font-bold">${p.patient ? p.patient.patientId : 'N/A'}</td>
                <td class="px-6 py-4 font-semibold text-slate-700">${p.diagnosis ? p.diagnosis.confirmedDiagnosis : 'Pending'}</td>
                <td class="px-6 py-4">
                    <span class="text-[10px] font-bold px-2 py-1 rounded-full ${p.recordStatus === 'Active' ? 'bg-indigo-50 text-indigo-600' : 'bg-slate-100 text-slate-500'}">
                        ● ${p.recordStatus || 'DRAFT'}
                    </span>
                </td>
                <td class="px-6 py-4">${getActionButtons(p.id, 'prescriptions', p.patient ? p.patient.patientId : 'Record')}</td>
            </tr>
        `).join('');
    } catch (err) { showTableError("Unable to load Prescriptions."); }
}

async function viewAllTests() {
    try {
        const res = await fetch(`${API_BASE}/api/medical-tests`);
        const data = await res.json();
        updateTableUI("Laboratory Registry", ["Test Name", "Category", "Status", "Actions"]);
        document.getElementById('mainTableBody').innerHTML = data.map(t => `
            <tr class="hover:bg-slate-50 transition-colors">
                <td class="px-6 py-4 font-semibold text-slate-700">${t.testName}</td>
                <td class="px-6 py-4 text-slate-500 text-xs">${t.category}</td>
                <td class="px-6 py-4">
                    <span class="text-[10px] font-bold px-3 py-1 rounded-full 
                        ${t.resultStatus === 'Completed' ? 'bg-emerald-50 text-emerald-600' : 
                          t.resultStatus === 'Pending' ? 'bg-amber-50 text-amber-600' : 'bg-slate-100 text-slate-600'}">
                        ● ${t.resultStatus}
                    </span>
                </td>
                <td class="px-6 py-4">${getActionButtons(t.id, 'medical-tests', t.testName)}</td>
            </tr>
        `).join('');
    } catch (err) { showTableError("Lab Registry Retrieval Failed."); }
}

async function viewAllMedicines() {
    try {
        const res = await fetch(`${API_BASE}/api/medicines`);
        const data = await res.json();
        updateTableUI("Pharmacy Inventory", ["Medicine Name", "Company", "Price", "Actions"]);
        document.getElementById('mainTableBody').innerHTML = data.map(m => `
            <tr class="hover:bg-slate-50 transition-colors">
                <td class="px-6 py-4 font-semibold text-slate-700">${m.name}</td>
                <td class="px-6 py-4 text-slate-500 text-sm">${m.companyName || 'N/A'}</td>
                <td class="px-6 py-4 font-bold text-indigo-600">$${m.price ? Number(m.price).toFixed(2) : '0.00'}</td>
                <td class="px-6 py-4">${getActionButtons(m.id, 'medicines', m.name)}</td>
            </tr>
        `).join('');
    } catch (err) { showTableError("Inventory Access Failed."); }
}

/**
 * Updates table headers and title dynamically
 */
function updateTableUI(title, headers) {
    const titleEl = document.getElementById('tableTitle');
    const headEl = document.getElementById('tableHead');
    if (titleEl) titleEl.innerText = title;
    if (headEl) {
        headEl.innerHTML = `
            <tr class="bg-slate-50 text-[10px] uppercase text-slate-400 font-bold tracking-widest border-b border-slate-100">
                ${headers.map((h, index) => `<th class="px-6 py-4 ${index === headers.length - 1 ? 'text-right' : ''}">${h}</th>`).join('')}
            </tr>`;
    }
}

// --- CRUD ACTION LOGIC ---

function editItem(type, id) {
    const routeMap = {
        'doctors': '/doctor-register',
        'patients': '/patient-register',
        'doctor-availability': '/availability-register',
        'prescriptions': '/prescription-register',
        'medical-tests': '/medical-test-register',
        'medicines': '/medicine-register'
    };

    const path = routeMap[type];
    if (path) {
        window.location.href = `${path}?id=${id}`;
    } else {
        console.error("Unknown type for editing:", type);
    }
}

async function deleteItem(type, id, name) {
    if (!confirm(`Are you sure you want to permanently delete: ${name}?`)) return;

    try {
        const response = await fetch(`${API_BASE}/api/${type}/${id}`, { 
            method: 'DELETE' 
        });

        if (response.ok || response.status === 204) {
            alert("Record deleted successfully.");
            initSaaSDashboard();
            refreshCurrentView(type);
        } else {
            const errorText = await response.text();
            alert(`Delete failed: ${errorText || "Access denied."}`);
        }
    } catch (err) {
        console.error("Delete Error:", err);
        alert("Server communication error.");
    }
}

function refreshCurrentView(type) {
    switch(type) {
        case 'doctors': viewAllDoctors(); break;
        case 'patients': viewAllPatients(); break;
        case 'medicines': viewAllMedicines(); break;
        case 'prescriptions': viewAllPrescriptions(); break;
        case 'medical-tests': viewAllTests(); break;
        case 'doctor-availability': viewAllAvailability(); break;
    }
}

function renderTests(tests) {
    const tableBody = document.getElementById('mainTableBody');
    if (!tableBody) return;
    updateTableUI("Recent Laboratory Activity", ["Test Name", "Patient ID", "Status", "Actions"]);
    
    const recentTests = tests.slice().reverse().slice(0, 5);
    
    tableBody.innerHTML = recentTests.map(t => `
        <tr class="hover:bg-slate-50 transition-colors">
            <td class="px-6 py-4 font-semibold text-slate-700">${t.testName}</td>
            <td class="px-6 py-4 font-mono text-xs text-slate-500">${t.patientId}</td>
            <td class="px-6 py-4">
                <span class="text-[10px] font-bold px-2 py-1 rounded-full 
                    ${t.resultStatus === 'Completed' ? 'bg-emerald-50 text-emerald-600' : 
                      t.resultStatus === 'Pending' ? 'bg-amber-50 text-amber-600' : 'bg-slate-100 text-slate-600'}">
                    ● ${t.resultStatus}
                </span>
            </td>
            <td class="px-6 py-4">${getActionButtons(t.id, 'medical-tests', t.testName)}</td>
        </tr>
    `).join('');
}