# MediTrack

A console-based clinic management system built in core Java, covering
OOP fundamentals through advanced topics (cloning, immutability, design
patterns, streams, and a rule-based AI feature). Built as part of the
Airtribe Java mentorship program.

No external dependencies — just the JDK.

---

## Features

**Core**
- CRUD for Doctors and Patients
- Appointment booking, confirmation, and cancellation with status tracking (`AppointmentStatus` enum)
- Menu-driven console UI (`Main.java`)
- Dynamic search: by name/specialization for doctors, by text query or exact age for patients

**Billing**
- `Bill` generation via a Factory (`BillFactory`) that picks a `BillingStrategy` (Standard vs. Insurance) based on the patient's insurance status
- Immutable `BillSummary` value object

**Bonus features implemented (2 of 4 required, plus extras)**
- **File I/O & Persistence** — save/load Doctors & Patients as CSV (`CSVUtil`), using try-with-resources; load on startup via `--loadData`
- **Design Patterns** — Singleton (`IdGenerator`), Factory (`BillFactory`), Strategy (`BillingStrategy` implementations), Observer (`AppointmentObserver` / `ConsoleReminderObserver`)
- **AI Feature** — rule-based symptom → specialization recommender (`AIHelper`) plus appointment slot auto-suggestion
- **Streams + Lambdas** — filter doctors by specialization, compute average consultation fee, group appointment counts per doctor

**Advanced OOP**
- Deep vs. shallow copy: `Patient` and `Appointment` implement `Cloneable` with true deep-copy semantics for nested objects
- Static block + static counter in `Person` (`totalPersonsCreated`)
- Centralized validation via `Validator`

---

## Architecture

```
com.airtribe.meditrack
├── entity/       Person (abstract) → Doctor, Patient
│                 MedicalEntity (abstract) → Person, Appointment, Bill
│                 Specialization, AppointmentStatus (enums)
│                 BillSummary (immutable)
├── service/      DoctorService, PatientService, AppointmentService
│                 BillFactory, BillingStrategy + implementations
│                 AppointmentObserver + ConsoleReminderObserver
├── util/         Validator, DateUtil, CSVUtil, IdGenerator (singleton)
│                 DataStore<T> (generic in-memory store), AIHelper
├── exception/    InvalidDataException, AppointmentNotFoundException
├── interfaces/   Payable, Searchable
├── constants/    Constants
└── test/         TestRunner (manual smoke tests)
```

`DataStore<T>` is a generic, reusable CRUD backing store keyed by ID,
shared across all three services instead of duplicating storage logic.

---

## Setup & Running

**Prerequisites:** JDK 17 or newer. See [`docs/Setup_Instructions.md`](docs/Setup_Instructions.md) for a full walkthrough (install, verify, IDE setup, troubleshooting).

Compile:
```bash
find src -name "*.java" > sources.txt
javac -d bin @sources.txt
```

Run:
```bash
java -cp bin com.airtribe.meditrack.Main
```

Run with previously saved CSV data loaded on startup:
```bash
java -cp bin com.airtribe.meditrack.Main --loadData
```

---

## Usage Walkthrough

A typical session from the menu:

1. **Add Doctor** — register a doctor with a specialization and consultation fee
2. **Add Patient** — register a patient, including insurance status
3. **Book Appointment** — link a doctor and patient at a given date/time (fires an observer console reminder)
4. **Generate Bill** — produces a `Bill` via the correct billing strategy based on the patient's insurance
5. **Save Data to CSV** — persists doctors and patients to `data/*.csv` for reuse via `--loadData` next run

Try **Recommend Doctor by Symptom (AI)** to see the rule-based
specialization suggestion, or **Show Analytics** for the streams-based
average fee and appointments-per-doctor breakdown.

---

## Testing

Run the manual test suite:
```bash
java -cp bin com.airtribe.meditrack.test.TestRunner
```

Covers: doctor search, deep-clone correctness on `Patient`, the full
appointment lifecycle (confirm/cancel/not-found), both billing
strategies, and the AI recommender. Expected output ends with:
```
9 passed, 0 failed
```

---

## Docs

- [`docs/Setup_Instructions.md`](docs/Setup_Instructions.md) — JDK install & run guide
- [`docs/JVM_Report.md`](docs/JVM_Report.md) — Class Loader, Runtime Data Areas, Execution Engine, JIT vs. Interpreter, WORA