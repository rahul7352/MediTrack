# JVM Report — How MediTrack Runs on the JVM

This report explains the JVM internals relevant to Environment Setup &
JVM Understanding, using MediTrack's own code (`Main.java`,
`Person.java`, `IdGenerator.java`, etc.) as concrete examples.

---

## 1. Class Loader

The Class Loader subsystem finds `.class` files and loads them into
memory as `Class` objects, in three phases:

1. **Loading** — reads the bytecode (e.g. `Main.class`,
   `Doctor.class`) and creates a `Class` object representing it.
2. **Linking**
   - *Verification*: checks the bytecode is structurally valid and doesn't violate JVM safety rules.
   - *Preparation*: allocates memory for static fields and sets them to default values (`0`, `null`, `false`).
   - *Resolution*: replaces symbolic references (e.g. a reference to `com.airtribe.meditrack.util.Validator`) with direct references.
3. **Initialization** — runs static initializers and static blocks, **top to bottom, in class-declaration order**.

MediTrack example: `Person.java` has

```java
static {
    totalPersonsCreated = 0;
    System.out.println("[MediTrack] Person subsystem initialized.");
}
```

This static block only runs **once**, the first time the `Person` class
is actively used (e.g. the first time a `Doctor` or `Patient` is
constructed) — not when the class file is merely present on the
classpath. That's why the message `[MediTrack] Person subsystem
initialized.` appears exactly once in the console output, right before
the first doctor or patient is created.

The three built-in loaders follow a **delegation hierarchy** (each asks
its parent to load first, and only loads it itself if the parent can't):

- **Bootstrap Class Loader** — loads core JDK classes (`java.lang.*`, `java.util.*`) from the JDK's own modules. Written in native code.
- **Platform (Extension) Class Loader** — loads JDK platform-specific modules.
- **Application (System) Class Loader** — loads MediTrack's own classes from `bin/` (or the compiled classpath), e.g. `com.airtribe.meditrack.Main`, `com.airtribe.meditrack.entity.Doctor`.

---

## 2. Runtime Data Areas

When the JVM runs, it divides memory into these regions:

### Heap
- Stores every **object** created with `new`, shared across all threads.
- Example: every `Doctor`, `Patient`, and `Appointment` object created in `Main.addDoctor()` / `addPatient()` / `bookAppointment()` lives here. The `DataStore<T>` maps in `DoctorService`, `PatientService`, and `AppointmentService` hold *references* to these heap objects.
- Garbage-collected: when no `DataStore` (or other object) still references a `Doctor`, it becomes eligible for GC.

### Stack (one per thread)
- Stores **stack frames** — one per method call — each holding local variables and partial results.
- Example: when `Main.bookAppointment()` calls `appointmentService.createAppointment(...)`, a new frame is pushed for `createAppointment`; local variables like `appointment` live in that frame until the method returns, at which point the frame is popped.
- Deep, unbounded recursion here would throw `StackOverflowError`.

### Method Area (part of Metaspace since Java 8)
- Stores per-class structures: the runtime constant pool, field/method data, and bytecode for methods.
- Example: the bytecode for `Bill.getAmount()` and the constant pool entries for string literals like `"Doctor[id=%s, ...]"` in `Doctor.describe()` live here, shared by every instance of that class.

### PC (Program Counter) Register
- One per thread; holds the address of the JVM instruction currently executing for that thread.
- Since MediTrack's console app is single-threaded (aside from JVM housekeeping threads), there is effectively one active PC register tracking execution as it steps through, e.g., the `switch` statement in `Main.main()`.

### Native Method Stacks
- Used for native (non-Java) method calls, e.g. some internal `java.io`/`java.util.Scanner` operations that delegate to native OS code when reading console input.

---

## 3. Execution Engine

The Execution Engine actually runs the bytecode loaded by the Class
Loader. It has three main parts:

- **Interpreter** — reads bytecode instructions one at a time and executes them directly. Fast to start, but re-interprets the same instructions every time they run.
- **JIT (Just-In-Time) Compiler** — see below.
- **Garbage Collector** — reclaims heap memory occupied by unreachable objects (e.g. `Appointment` objects removed via cancellation flows that are no longer referenced by any `DataStore`).

---

## 4. JIT Compiler vs. Interpreter

| | Interpreter | JIT Compiler |
|---|---|---|
| How it runs code | Translates and executes bytecode line-by-line, every time | Compiles frequently-run ("hot") bytecode into native machine code once, then reuses it |
| Startup cost | Low — starts executing immediately | Higher — compilation takes time up front |
| Steady-state speed | Slower for repeated execution | Much faster for repeated execution |

The JVM uses **both together**: it starts by interpreting bytecode so
the program begins running immediately, then profiles which methods
run often ("hot spots") and compiles just those to native code via the
JIT — this is why the strategy is called **HotSpot** (the name of
Oracle's/OpenJDK's JVM implementation).

MediTrack example: in a long-running session, `DataStore.getAll()`,
`DoctorService.searchDoctor()`, or the stream pipeline in
`appointmentsPerDoctor()` would be called repeatedly as a user searches
and books many appointments. After enough invocations, the JIT compiles
these hot methods to native code, making later calls noticeably faster
than the very first call — even though the Java source never changes.

---

## 5. "Write Once, Run Anywhere" (WORA)

Java source is compiled by `javac` into **bytecode** (`.class` files) —
not directly into machine code for a specific CPU/OS. That bytecode is
platform-*independent*: the same `Main.class`, `Doctor.class`, etc.
produced by `javac` on one machine can run unmodified on any machine
that has a matching JVM installed.

What actually differs between platforms is the **JVM implementation**
(one build for Windows, one for macOS, one for Linux) — each JVM knows
how to translate the same bytecode into the correct native instructions
and system calls for its own OS/CPU.

MediTrack example: after running `javac -d bin @sources.txt` once, the
resulting `bin/` folder full of `.class` files can be zipped up and run
with `java -cp bin com.airtribe.meditrack.Main` on a Windows laptop, a
Mac, or a Linux server — with **no recompilation** — as long as each
machine has a JDK/JRE of a compatible version installed. This is the
practical benefit of WORA: compile once during development, distribute
the same `.class` files everywhere.

---

## Summary

| JVM Concept | Where it shows up in MediTrack |
|---|---|
| Class Loader | `Person`'s static block runs once, on first real use of the class |
| Heap | Every `Doctor`/`Patient`/`Appointment`/`Bill` object |
| Stack | Local variables inside `Main`, `DoctorService`, etc. during each method call |
| Method Area | Bytecode + constant pool for every loaded class |
| PC Register | Tracks the current instruction per thread as the console loop runs |
| Interpreter | Executes the app the first time any method runs |
| JIT Compiler | Speeds up repeated calls like `searchDoctor()`/`getAllAppointments()` in long sessions |
| WORA | Same `bin/*.class` files run unmodified on Windows/macOS/Linux with any compatible JVM |
