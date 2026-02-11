// Route Zen - JavaScript Implementation
// Smart Bus Scheduling System

// Bus stops data
const BUS_STOPS = [
    "Kuttipuram", "Thangal Padi", "Kavumpuram", "Kandanakam", "Mannor",
    "Edappal", "Naduvattom", "Kalachal", "Panthavoor", "Manthadam",
    "Changramkullam", "Valayamkullam", "Pavitapuram", "Kolikara", "Kadavalur",
    "Kallumpuram", "Vattamavu", "Korattikara", "Ansar Hospital", "Permbilavu",
    "Akkikavu", "Parempadam", "Kunamkullam", "Kanipayoor", "Choondal",
    "Kechery", "Kaiparambu", "Mundur", "Peramangalam", "Amala",
    "Muthuvara", "Puzhakkal", "Shoba City", "Deshaabhimani", "Poonkunnam",
    "Patturaikkal", "Vaddake Stand", "Sapna", "Railway Station", "Thrissur"
];

// Stop distances from origin (Kuttipuram)
const STOP_DISTANCES = {
    "Kuttipuram": 0, "Thangal Padi": 3, "Kavumpuram": 6, "Kandanakam": 9,
    "Mannor": 12, "Edappal": 15, "Naduvattom": 18, "Kalachal": 21,
    "Panthavoor": 24, "Manthadam": 27, "Changramkullam": 30,
    "Valayamkullam": 33, "Pavitapuram": 36, "Kolikara": 39,
    "Kadavalur": 42, "Kallumpuram": 45, "Vattamavu": 47,
    "Korattikara": 49, "Ansar Hospital": 51, "Permbilavu": 53,
    "Akkikavu": 54, "Parempadam": 55, "Kunamkullam": 57,
    "Kanipayoor": 58, "Choondal": 59, "Kechery": 60,
    "Kaiparambu": 61, "Mundur": 62, "Peramangalam": 63,
    "Amala": 64, "Muthuvara": 65, "Puzhakkal": 66,
    "Shoba City": 67, "Deshaabhimani": 68, "Poonkunnam": 69,
    "Patturaikkal": 70, "Vaddake Stand": 71, "Sapna": 72,
    "Railway Station": 73, "Thrissur": 75
};

// Bus types configuration
const BUS_TYPES = {
    ORDINARY: { fare: 60, speed: 25, seats: 45 },
    LIMITED: { fare: 100, speed: 30, seats: 40 },
    SUPERFAST: { fare: 150, speed: 37, seats: 35 }
};

let currentResults = [];

// Initialize app on page load
document.addEventListener('DOMContentLoaded', function() {
    console.log('🚌 Route Zen initialized');
    populateStops();
    setCurrentTime();
    bindEvents();
});

/**
 * Populate stop dropdowns with bus stops
 */
function populateStops() {
    const fromSelect = document.getElementById('fromStop');
    const toSelect = document.getElementById('toStop');

    BUS_STOPS.forEach(stop => {
        fromSelect.appendChild(new Option(stop, stop));
        toSelect.appendChild(new Option(stop, stop));
    });

    // Set default values
    fromSelect.value = 'Changramkullam';
    toSelect.value = 'Kunamkullam';
}

/**
 * Set current time in boarding time input
 */
function setCurrentTime() {
    const now = new Date();
    const timeString = now.toTimeString().slice(0, 5);
    document.getElementById('boardingTime').value = timeString;
}

/**
 * Bind event listeners
 */
function bindEvents() {
    document.getElementById('searchForm').addEventListener('submit', handleSearch);
    document.getElementById('swapButton').addEventListener('click', swapStops);
}

/**
 * Swap boarding and destination stops
 */
function swapStops() {
    const fromSelect = document.getElementById('fromStop');
    const toSelect = document.getElementById('toStop');

    const temp = fromSelect.value;
    fromSelect.value = toSelect.value;
    toSelect.value = temp;

    updateStatus('🔄 Route direction swapped!', 'success');
}

/**
 * Handle search form submission
 */
async function handleSearch(event) {
    event.preventDefault();

    const fromStop = document.getElementById('fromStop').value;
    const toStop = document.getElementById('toStop').value;
    const boardingTime = document.getElementById('boardingTime').value;

    // Validate inputs
    if (!fromStop || !toStop || !boardingTime) {
        updateStatus('⚠️ Please fill in all fields', 'error');
        return;
    }

    if (fromStop === toStop) {
        updateStatus('❌ Boarding and destination stops must be different', 'error');
        return;
    }

    // Search buses
    updateStatus('🔍 Searching for your perfect bus...', 'loading');

    setTimeout(() => {
        const results = searchBuses(fromStop, toStop, boardingTime);
        displayResults(results);
    }, 1000);
}

/**
 * Search for available buses
 */
function searchBuses(fromStop, toStop, boardingTime) {
    const distance = calculateDistance(fromStop, toStop);
    const results = [];

    // Generate buses for each type
    Object.entries(BUS_TYPES).forEach(([type, config]) => {
        const busCount = type === 'ORDINARY' ? 6 : type === 'LIMITED' ? 4 : 3;

        for (let i = 0; i < busCount; i++) {
            const bus = {
                busNumber: \`\${type.slice(0, 3)}-\${Math.floor(1000 + Math.random() * 9000)}\`,
                busType: type,
                boarding: boardingTime,
                duration: calculateDuration(distance, config.speed),
                fare: calculateFare(distance, config.fare),
                operator: Math.random() > 0.5 ? 'KSRTC' : 'PRIVATE',
                seats: config.seats,
                availableSeats: Math.floor(config.seats * (0.4 + Math.random() * 0.6))
            };
            results.push(bus);
        }
    });

    return results.sort((a, b) => a.boarding.localeCompare(b.boarding));
}

/**
 * Calculate distance between stops
 */
function calculateDistance(fromStop, toStop) {
    const from = STOP_DISTANCES[fromStop] || 0;
    const to = STOP_DISTANCES[toStop] || 0;
    return Math.abs(to - from);
}

/**
 * Calculate journey duration
 */
function calculateDuration(distance, speed) {
    const hours = Math.floor(distance / speed);
    const minutes = Math.round((distance / speed - hours) * 60);
    return hours > 0 ? \`\${hours}h \${minutes}m\` : \`\${minutes}m\`;
}

/**
 * Calculate fare
 */
function calculateFare(distance, baseFare) {
    return Math.round((baseFare / 75) * distance);
}

/**
 * Display search results
 */
function displayResults(results) {
    currentResults = results;

    if (results.length === 0) {
        updateStatus('😔 No buses found', 'error');
        return;
    }

    document.getElementById('resultsSection').style.display = 'block';
    document.getElementById('resultCount').textContent = \`\${results.length} buses found\`;

    const tbody = document.getElementById('resultsTableBody');
    tbody.innerHTML = '';

    results.forEach(bus => {
        const row = document.createElement('tr');
        row.innerHTML = \`
            <td><strong>\${bus.busNumber}</strong></td>
            <td><span class="bus-type \${bus.busType.toLowerCase()}">\${bus.busType}</span></td>
            <td><strong>\${bus.boarding}</strong></td>
            <td>--</td>
            <td>\${bus.duration}</td>
            <td><strong style="color: #6B7D3C;">₹\${bus.fare}</strong></td>
            <td>\${bus.operator}</td>
            <td>📍 En route</td>
        \`;
        tbody.appendChild(row);
    });

    updateStatus(\`✨ Found \${results.length} buses!\`, 'success');

    setTimeout(() => {
        document.getElementById('resultsSection').scrollIntoView({ behavior: 'smooth' });
    }, 300);
}

/**
 * Update status message
 */
function updateStatus(message, type = '') {
    const statusElement = document.getElementById('searchStatus');
    statusElement.textContent = message;
    statusElement.className = \`search-status \${type}\`;
}

console.log('✓ Route Zen app.js loaded successfully');
