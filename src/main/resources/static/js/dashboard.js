/**
 * dashboard.js
 * Core logic for the HealthFlow Pro SaaS Dashboard
 * Optimized for full enterprise module filtering and system-wide String IDs
 */

document.addEventListener('DOMContentLoaded', () => {
    initSaaSDashboard();
});

const API_BASE = "http://localhost:8080";

/**
 * Concurrent fetching for metric cards and initial system activity feed
 */
async function initSaaSDashboard() {
    try {
        const [docs, pats, prescriptions, tests, meds, availability, clinics] = await Promise.all([
            fetch(`${API_BASE}/api/doctors/count`).then(res => res.json()).catch(() => 0),
            fetch(`${API_BASE}/api/patients/count`).then(res => res.json()).catch(() => 0),
            fetch(`${API_BASE}/api/prescriptions`).then(res => res.json()).catch(() => []),
            fetch(`${API_BASE}/api/medical-tests`).then(res => res.json()).catch(() => []),
            fetch(`${API_BASE}/api/medicines`).then(res => res.json()).catch(() => []),
            fetch(`${API_BASE}/api/doctor-availability`).then(res => res.json()).catch(() => []),
            fetch(`${API_BASE}/api/clinics`).then(res => res.json()).catch(() => [])
        ]);

        setCount('docCount', docs);
        setCount('patCount', pats);
        setCount('presCount', prescriptions.length);
        setCount('testCount', tests.length); 
        setCount('medsCount', meds.length);
        setCount('availCount', availability.length);
        setCount('clinicCount', Array.isArray(clinics) ? clinics.length : 0);

        // DEFAULT VIEW: Render Recent Lab Activity on load
        renderTests(tests);

    } catch (error) {
        console.error("Dashboard Sync Error:", error);
        showTableError("System Sync Failed. Please check backend connectivity.");
    }
}

function setCount(id, value) {
    const el = document.getElementById(id);
    if (el) el.innerText = value !== undefined ? value : 0;
}

/**
 * UI State Management: Table Headers and Titles
 */
function updateTableUI(title, headers) {
    const titleEl = document.getElementById('tableTitle');
    const headEl = document.getElementById('tableHead');
    if (titleEl) titleEl.innerText = title;
    if (headEl) {
        headEl.innerHTML = `
            <tr class="bg-slate-50 text-[10px] uppercase text-slate-400 font-bold tracking-widest border-b border-slate-100">
                ${headers.map((h, index) => `
                    <th class="px-6 py-4 ${index === headers.length - 1 ? 'text-right' : ''}">${h}</th>
                `).join('')}
            </tr>`;
    }
}

function showTableError(message) {
    const tableBody = document.getElementById('mainTableBody');
    if (tableBody) {
        tableBody.innerHTML = `
            <tr>
                <td colspan="6" class="text-center py-12 bg-white">
                    <div class="text-rose-500 font-bold mb-1"><i class="fas fa-exclamation-triangle mr-2"></i> ${message}</div>
                    <div class="text-[10px] text-slate-400 uppercase tracking-widest font-semibold">Service Unavailable</div>
                </td>
            </tr>`;
    }
}

/**
 * Universal Action Button Generator
 */
function getActionButtons(id, type, name) {
    const stringId = String(id);
    return `
        <div class="flex items-center justify-end space-x-2">
            <button onclick="editItem('${type}', '${stringId}')" class="p-2 text-blue-600 hover:bg-blue-50 rounded-lg transition-colors" title="Edit">
                <i class="fas fa-edit text-xs"></i>
            </button>
            <button onclick="deleteItem('${type}', '${stringId}', '${name}')" class="p-2 text-rose-600 hover:bg-rose-50 rounded-lg transition-colors" title="Delete">
                <i class="fas fa-trash text-xs"></i>
            </button>
        </div>`;
}

// --- FILTER & SEARCH CENTRAL LOGIC ---

/**
 * Universal Search Handler
 * Standardized to match Java Controller @RequestParam names
 */
function performSearch(module) {
    const filters = {};
    
    if (module === 'clinics') {
        filters.clinicName = document.getElementById('searchName')?.value;
        filters.clinicType = document.getElementById('searchType')?.value;
        filters.status = document.getElementById('searchStatus')?.value;
        viewAllClinics(filters);
    } 
    else if (module === 'doctors') {
        filters.name = document.getElementById('searchName')?.value;
        filters.hospitalName = document.getElementById('searchHospital')?.value;
        filters.specialization = document.getElementById('searchSpecialization')?.value;
        viewAllDoctors(filters);
    }
    else if (module === 'patients') {
        // Matches PatientController @RequestParam names
        filters.name = document.getElementById('searchName')?.value;
        filters.patientId = document.getElementById('searchId')?.value;
        filters.bloodGroup = document.getElementById('searchBloodGroup')?.value;
        filters.phoneContains = document.getElementById('searchPhone')?.value;
        viewAllPatients(filters);
    }
    else if (module === 'medicines') {
        filters.name = document.getElementById('searchName')?.value;
        filters.company = document.getElementById('searchCompany')?.value;
        filters.status = document.getElementById('searchStatus')?.value;
        viewAllMedicines(filters);
    }
    else if (module === 'medical-tests') {
        filters.patientId = document.getElementById('searchPatientId')?.value;
        filters.testName = document.getElementById('searchTestName')?.value;
        filters.resultStatus = document.getElementById('searchStatus')?.value;
        viewAllTests(filters);
    }
    else if (module === 'prescriptions') {
        filters.patientId = document.getElementById('searchPatientId')?.value;
        filters.diagnosis = document.getElementById('searchDiagnosis')?.value;
        filters.status = document.getElementById('searchStatus')?.value;
        viewAllPrescriptions(filters);
    }
    else if (module === 'availability') {
        filters.doctorId = document.getElementById('searchDocId')?.value;
        filters.day = document.getElementById('searchDay')?.value;
        filters.isAvailable = document.getElementById('searchStatus')?.value;
        viewAllAvailability(filters);
    }
}

/**
 * Helper to build dynamic search URLs
 * Standardized to use /filter to match updated Java Controllers
 */
function buildSearchUrl(endpoint, filters) {
    const params = new URLSearchParams();
    Object.keys(filters).forEach(key => {
        if (filters[key] && filters[key] !== "") params.append(key, filters[key]);
    });
    const queryString = params.toString();
    // standardized to /filter to prevent conflict with /{id}
    return queryString ? `${API_BASE}${endpoint}/filter?${queryString}` : `${API_BASE}${endpoint}`;
}

// --- MODULE VIEW RENDERING ---

async function viewAllClinics(filters = {}) {
    try {
        const res = await fetch(buildSearchUrl('/api/clinics', filters));
        const data = await res.json();
        updateTableUI("Infrastructure Network", ["Clinic Entity", "Category", "Location", "Status", "Actions"]);
        const tableBody = document.getElementById('mainTableBody');
        tableBody.innerHTML = data.map(c => {
            const statusClass = c.status?.toUpperCase() === 'ACTIVE' ? 'bg-emerald-50 text-emerald-600' : 'bg-slate-100 text-slate-500';
            const recordId = String(c.clinicId || c.id);
            return `
                <tr class="hover:bg-slate-50 border-b border-slate-50">
                    <td class="px-6 py-4"><div class="font-bold text-slate-700">${c.clinicName}</div><div class="text-[10px] text-slate-400 font-mono">ID: ${c.registrationNumber || 'N/A'}</div></td>
                    <td class="px-6 py-4"><span class="bg-blue-50 text-blue-600 text-[10px] font-bold px-2 py-1 rounded uppercase">${c.clinicType || 'CLINIC'}</span></td>
                    <td class="px-6 py-4 text-sm text-slate-600">${c.address?.city || 'N/A'}</td>
                    <td class="px-6 py-4"><span class="text-[9px] font-bold px-2 py-1 rounded-full ${statusClass}">● ${c.status || 'INACTIVE'}</span></td>
                    <td class="px-6 py-4">${getActionButtons(recordId, 'clinics', c.clinicName)}</td>
                </tr>`;
        }).join('');
    } catch (err) { showTableError("Clinic load failed."); }
}

async function viewAllDoctors(filters = {}) {
    try {
        const res = await fetch(buildSearchUrl('/api/doctors', filters));
        const data = await res.json();
        updateTableUI("Medical Staff Directory", ["Doctor Info", "Specialization", "Hospital", "Status", "Actions"]);
        const tableBody = document.getElementById('mainTableBody');
        tableBody.innerHTML = data.map(d => {
            const statusClass = d.availability === 'AVAILABLE' ? 'bg-emerald-50 text-emerald-600' : 'bg-rose-50 text-rose-600';
            return `
                <tr class="hover:bg-slate-50 border-b border-slate-50">
                    <td class="px-6 py-4"><div class="font-bold text-slate-700">${d.name}</div><div class="text-[10px] text-slate-400">${d.email || d.phone}</div></td>
                    <td class="px-6 py-4"><span class="bg-indigo-50 text-indigo-600 text-[10px] font-bold px-2 py-1 rounded uppercase">${d.specialization}</span></td>
                    <td class="px-6 py-4 text-sm font-medium text-slate-700">${d.hospitalName || 'N/A'}</td>
                    <td class="px-6 py-4"><span class="text-[9px] font-bold px-2 py-1 rounded-full ${statusClass}">● ${d.availability || 'OFFLINE'}</span></td>
                    <td class="px-6 py-4">${getActionButtons(d.id, 'doctors', d.name)}</td>
                </tr>`;
        }).join('');
    } catch (err) { showTableError("Staff load failed."); }
}

async function viewAllPatients(filters = {}) {
    try {
        const res = await fetch(buildSearchUrl('/api/patients', filters));
        const data = await res.json();
        updateTableUI("Patient Registry", ["Patient Details", "Medical ID", "Contact Info", "Vitals", "Actions"]);
        const tableBody = document.getElementById('mainTableBody');
        if (!data || data.length === 0) {
            tableBody.innerHTML = '<tr><td colspan="5" class="p-12 text-center text-slate-400 italic">No records found.</td></tr>';
            return;
        }
        tableBody.innerHTML = data.map(p => `
            <tr class="hover:bg-slate-50 border-b border-slate-100">
                <td class="px-8 py-5">
                    <div class="font-bold text-slate-700">${p.fullName || 'Unnamed'}</div>
                    <div class="text-[10px] text-slate-400 font-medium lowercase">${p.emailAddress || 'No Email'}</div>
                </td>
                <td class="px-8 py-5"><span class="font-mono text-xs bg-slate-100 text-slate-600 px-2 py-1 rounded border border-slate-200">${p.patientId || 'N/A'}</span></td>
                <td class="px-8 py-5 text-slate-600 text-xs"><i class="fas fa-phone-alt mr-1 text-[10px] text-blue-400"></i> ${p.phoneNumber || 'N/A'}</td>
                <td class="px-8 py-5"><span class="bg-blue-50 text-blue-600 text-[10px] font-bold px-2 py-1 rounded uppercase">${p.bloodGroup || 'N/A'}</span></td>
                <td class="px-6 py-4">${getActionButtons(p.id, 'patients', p.fullName)}</td>
            </tr>`).join('');
    } catch (err) { showTableError("Patient load failed."); }
}

async function viewAllMedicines(filters = {}) {
    try {
        const res = await fetch(buildSearchUrl('/api/medicines', filters));
        const data = await res.json();
        updateTableUI("Pharmacy Inventory", ["Medicine", "Manufacturer", "Price", "Status", "Actions"]);
        const body = document.getElementById('mainTableBody');
        body.innerHTML = data.map(m => `
            <tr class="hover:bg-slate-50 border-b border-slate-50">
                <td class="px-6 py-4 font-bold text-slate-700">${m.name}</td>
                <td class="px-6 py-4 text-slate-500">${m.companyName}</td>
                <td class="px-6 py-4 font-bold">₹${m.price}</td>
                <td class="px-6 py-4"><span class="text-[10px] font-bold px-2 py-1 rounded-full ${m.recordStatus === 'ACTIVE' ? 'bg-emerald-50 text-emerald-600' : 'bg-rose-50 text-rose-600'}">${m.recordStatus}</span></td>
                <td class="px-6 py-4">${getActionButtons(m.id, 'medicines', m.name)}</td>
            </tr>`).join('');
    } catch (err) { showTableError("Medicine load failed."); }
}

async function viewAllTests(filters = {}) {
    try {
        const res = await fetch(buildSearchUrl('/api/medical-tests', filters));
        const data = await res.json();
        updateTableUI("Laboratory Registry", ["Patient Ref", "Test Name", "Fee", "Status", "Actions"]);
        const body = document.getElementById('mainTableBody');
        body.innerHTML = data.map(t => `
            <tr class="hover:bg-slate-50 border-b border-slate-50">
                <td class="px-6 py-4 font-mono text-xs text-blue-600">${t.patientId}</td>
                <td class="px-6 py-4 font-bold text-slate-700">${t.testName}</td>
                <td class="px-6 py-4">₹${t.price}</td>
                <td class="px-6 py-4"><span class="text-[9px] font-bold px-2 py-1 rounded-full ${t.resultStatus === 'Completed' ? 'bg-emerald-50 text-emerald-600' : 'bg-amber-50 text-amber-600'}">${t.resultStatus}</span></td>
                <td class="px-6 py-4">${getActionButtons(t.id, 'medical-tests', t.testName)}</td>
            </tr>`).join('');
    } catch (err) { showTableError("Lab load failed."); }
}

async function viewAllPrescriptions(filters = {}) {
    try {
        const res = await fetch(buildSearchUrl('/api/prescriptions', filters));
        const data = await res.json();
        updateTableUI("Clinical History", ["Patient Ref", "Diagnosis", "Items", "Status", "Actions"]);
        const body = document.getElementById('mainTableBody');
        body.innerHTML = data.map(p => `
            <tr class="hover:bg-slate-50 border-b border-slate-50">
                <td class="px-6 py-4 font-mono text-xs">${p.patient?.patientId || 'N/A'}</td>
                <td class="px-6 py-4 font-medium text-slate-700">${p.diagnosis?.confirmedDiagnosis || 'Pending'}</td>
                <td class="px-6 py-4 text-xs">${p.medications?.length || 0} Meds</td>
                <td class="px-6 py-4"><span class="text-[9px] font-bold px-2 py-1 rounded-full bg-blue-50 text-blue-600">${p.recordStatus}</span></td>
                <td class="px-6 py-4">${getActionButtons(p.id, 'prescriptions', 'Prescription')}</td>
            </tr>`).join('');
    } catch (err) { showTableError("Prescription load failed."); }
}

async function viewAllAvailability(filters = {}) {
    try {
        const res = await fetch(buildSearchUrl('/api/doctor-availability', filters));
        const data = await res.json();
        updateTableUI("Shift Schedule", ["Doctor ID", "Day", "Time Window", "Status", "Actions"]);
        const body = document.getElementById('mainTableBody');
        body.innerHTML = data.map(a => `
            <tr class="hover:bg-slate-50 border-b border-slate-50">
                <td class="px-6 py-4 font-mono text-blue-600 font-bold">${a.doctorId}</td>
                <td class="px-6 py-4 font-semibold">${a.day}</td>
                <td class="px-6 py-4 text-slate-500 text-xs">${a.startTime} - ${a.endTime}</td>
                <td class="px-6 py-4"><span class="px-2 py-1 rounded-full text-[10px] font-bold ${a.available ? 'bg-emerald-50 text-emerald-600' : 'bg-amber-50 text-amber-600'}">${a.available ? 'ONLINE' : 'OFFLINE'}</span></td>
                <td class="px-6 py-4">${getActionButtons(a.id, 'availability', a.doctorId)}</td>
            </tr>`).join('');
    } catch (err) { showTableError("Shift load failed."); }
}

// --- CORE ACTIONS ---

function editItem(type, id) {
    const routeMap = { 
        'doctors': '/doctor-register', 'patients': '/patient-register', 
        'clinics': '/clinic-register', 'medicines': '/medicine-register',
        'medical-tests': '/medical-test-register', 'prescriptions': '/prescription-register',
        'availability': '/availability-register'
    };
    const path = routeMap[type];
    if (path) window.location.href = `${path}?id=${id}`;
}

async function deleteItem(type, id, name) {
    if (!confirm(`Confirm permanent deletion of: ${name}?`)) return;
    try {
        const response = await fetch(`${API_BASE}/api/${type}/${id}`, { method: 'DELETE' });
        if (response.ok || response.status === 204) {
            initSaaSDashboard();
            refreshCurrentView(type);
        }
    } catch (err) { alert("Delete failed."); }
}

function refreshCurrentView(type) {
    const viewMap = {
        'doctors': viewAllDoctors, 'patients': viewAllPatients, 'clinics': viewAllClinics,
        'medicines': viewAllMedicines, 'medical-tests': viewAllTests, 
        'prescriptions': viewAllPrescriptions, 'availability': viewAllAvailability
    };
    if (viewMap[type]) viewMap[type](); else initSaaSDashboard();
}

function renderTests(tests) {
    const tableBody = document.getElementById('mainTableBody');
    if (!tableBody) return;
    updateTableUI("Recent Laboratory Activity", ["Test Name", "Patient Ref", "Status", "Actions"]);
    const recent = Array.isArray(tests) ? tests.slice().reverse().slice(0, 5) : [];
    tableBody.innerHTML = recent.map(t => `
        <tr class="hover:bg-slate-50 border-b border-slate-50">
            <td class="px-6 py-4 font-bold text-slate-700">${t.testName}</td>
            <td class="px-6 py-4 font-mono text-xs text-slate-500">${t.patientId}</td>
            <td class="px-6 py-4"><span class="text-[10px] font-bold px-2 py-1 rounded-full ${t.resultStatus === 'Completed' ? 'bg-emerald-50 text-emerald-600' : 'bg-amber-50 text-amber-600'} uppercase">● ${t.resultStatus}</span></td>
            <td class="px-6 py-4">${getActionButtons(t.id, 'medical-tests', t.testName)}</td>
        </tr>`).join('');
}