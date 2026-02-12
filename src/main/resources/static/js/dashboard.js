/**
 * dashboard.js
 * Core logic for the HealthFlow Pro SaaS Dashboard
 * Optimized for Search, Edit, and Delete synchronization with New Doctor Fields
 */

document.addEventListener('DOMContentLoaded', () => {
    initSaaSDashboard();
});

const API_BASE = "http://localhost:8080";

/**
 * Concurrent fetching for metric cards and initial list views
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

        setCount('docCount', docs);
        setCount('patCount', pats);
        setCount('presCount', prescriptions.length);
        setCount('testCount', tests.length); 
        setCount('medsCount', meds.length);
        setCount('availCount', availability.length);

        // Render Recent Lab Activity by default
        renderTests(tests);

    } catch (error) {
        console.error("Dashboard Sync Error:", error);
        showTableError("System Sync Failed. Please ensure the Backend is running.");
    }
}

function setCount(id, value) {
    const el = document.getElementById(id);
    if (el) el.innerText = value !== undefined ? value : 0;
}

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
 * Updated to display Hospital, Fee, and Status
 */
async function viewAllDoctors(searchParams = null) {
    try {
        let url = `${API_BASE}/api/doctors`;
        if (searchParams) {
            const query = new URLSearchParams(searchParams).toString();
            url = `${API_BASE}/api/doctors/search?${query}`;
        }

        const res = await fetch(url);
        const data = await res.json();
        
        // Updated Headers for Doctor View
        updateTableUI("Doctor Directory", ["Doctor Info", "Specialization", "Hospital & Fee", "Status", "Actions"]);
        
        const tableBody = document.getElementById('mainTableBody');
        if (data.length === 0) {
            tableBody.innerHTML = `<tr><td colspan="5" class="text-center py-10 text-slate-400">No doctors found.</td></tr>`;
            return;
        }

        tableBody.innerHTML = data.map(d => {
            const statusClass = d.availability === 'AVAILABLE' ? 'bg-emerald-50 text-emerald-600' : 'bg-rose-50 text-rose-600';
            return `
                <tr class="hover:bg-slate-50 transition-colors">
                    <td class="px-6 py-4">
                        <div class="font-semibold text-slate-700">${d.name}</div>
                        <div class="text-[10px] text-slate-400">${d.email || d.phone}</div>
                    </td>
                    <td class="px-6 py-4">
                        <span class="bg-indigo-50 text-indigo-600 text-[10px] font-bold px-2 py-1 rounded-lg">${d.specialization}</span>
                        <div class="text-[10px] text-slate-400 mt-1">${d.experience} Yrs Exp.</div>
                    </td>
                    <td class="px-6 py-4">
                        <div class="text-sm font-medium text-slate-700">${d.hospitalName || 'N/A'}</div>
                        <div class="text-xs text-indigo-500 font-bold">₹${d.consultationFee || 0}</div>
                    </td>
                    <td class="px-6 py-4">
                        <span class="text-[9px] font-bold px-2 py-1 rounded-full ${statusClass}">
                            ● ${d.availability || 'OFFLINE'}
                        </span>
                    </td>
                    <td class="px-6 py-4">${getActionButtons(d.id, 'doctors', d.name)}</td>
                </tr>
            `;
        }).join('');
    } catch (err) { showTableError("Unable to load Doctors."); }
}

/**
 * Updated Search Handler to include Hospital and Fee
 */
function handleDoctorSearch() {
    const params = {
        name: document.getElementById('searchName')?.value || '',
        specialization: document.getElementById('searchSpec')?.value || '',
        hospitalName: document.getElementById('searchHospital')?.value || '', // New
        maxFee: document.getElementById('searchMaxFee')?.value || '',         // New
        minExp: document.getElementById('searchMinExp')?.value || ''
    };
    const activeFilters = Object.fromEntries(Object.entries(params).filter(([_, v]) => v !== ''));
    viewAllDoctors(activeFilters);
}

// ... (Rest of viewAllPatients, viewAllAvailability, etc. remain the same)

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

function editItem(type, id) {
    const routeMap = { 'doctors': '/doctor-register', 'patients': '/patient-register', 'medicines': '/medicine-register' };
    const path = routeMap[type];
    if (path) window.location.href = `${path}?id=${id}`;
}

async function deleteItem(type, id, name) {
    if (!confirm(`Are you sure you want to permanently delete: ${name}?`)) return;
    try {
        const response = await fetch(`${API_BASE}/api/${type}/${id}`, { method: 'DELETE' });
        if (response.ok || response.status === 204) {
            alert("Record deleted successfully.");
            initSaaSDashboard();
            refreshCurrentView(type);
        }
    } catch (err) { alert("Server communication error."); }
}

function refreshCurrentView(type) {
    if (type === 'doctors') viewAllDoctors();
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
                <span class="text-[10px] font-bold px-2 py-1 rounded-full ${t.resultStatus === 'Completed' ? 'bg-emerald-50 text-emerald-600' : 'bg-amber-50 text-amber-600'}">
                    ● ${t.resultStatus}
                </span>
            </td>
            <td class="px-6 py-4">${getActionButtons(t.id, 'medical-tests', t.testName)}</td>
        </tr>
    `).join('');
}