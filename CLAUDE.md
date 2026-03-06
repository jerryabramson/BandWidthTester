# CLAUDE.md — BandWidthTester

This file provides guidance for AI assistants (and developers) working in this repository.

---

## Project Overview

**BandWidthTester** is a Java command-line tool that wraps `iperf3` to provide enhanced,
real-time network bandwidth measurements. It monitors iperf3 output asynchronously, formats
results with ANSI color coding, tracks min/max/average metrics, and supports repeated test
runs with result aggregation.

- **Author**: jerry (`@author jerry`)
- **Language**: Java 17
- **Build system**: Gradle with Shadow plugin (fat JAR)
- **Main class**: `org.jaa.bandwidthtester.BandwidthTester`
- **Artifact**: `build/libs/BandWidthTester-1.0-SNAPSHOT-all.jar`

---

## Repository Layout

```
BandWidthTester/
├── src/main/java/org/jaa/bandwidthtester/
│   ├── BandwidthTester.java      # Main entry point; arg parsing, execution loop, result output
│   ├── IPerf3Monitor.java        # Async monitoring of iperf3 process
│   ├── Executor.java             # Process launch and thread management
│   ├── MonitorIPerf3Output.java  # Real-time output parsing and formatted display
│   ├── Launcher.java             # Runnable stream reader for async stdout/stderr
│   ├── ConnectionDetails.java    # Bandwidth unit conversion; min/max/avg tracking
│   ├── ResultDetails.java        # Per-run result container (min, max, avg, rc, runDate)
│   ├── Args.java                 # Parsed command-line argument holder
│   ├── AnsiCodes.java            # ANSI escape sequence helpers (colors, bold, reset)
│   ├── TerminalType.java         # Terminal capability detection (ANSI vs DUMB, UTF-8)
│   └── OS.java                   # Windows console API integration via JNA
├── build.gradle                  # Gradle build config (Java 17, Shadow, JNA, SLF4J, JUnit 5)
├── settings.gradle               # Project name: BandWidthTester
├── run.sh                        # Build-and-run convenience script (Linux/Mac)
├── install.sh                    # Unix install: builds JAR, installs to /usr/local/
├── install.bat                   # Windows batch install
├── install.ps1                   # PowerShell install
├── newTestBandWidth.sh           # Unix shell wrapper (post-install)
├── newTestBandWidth.ps1          # PowerShell wrapper (post-install)
├── README.md                     # Usage documentation (HTML-formatted)
└── .gitignore                    # Excludes: target/, build/, .idea, .gradle, results.txt
```

---

## Build & Run Commands

### Build
```bash
gradle clean shadowJar
# Output: build/libs/BandWidthTester-1.0-SNAPSHOT-all.jar
```

### Run (directly)
```bash
java -jar build/libs/BandWidthTester-1.0-SNAPSHOT-all.jar <host> [options]
```

### Run (via convenience script)
```bash
./run.sh [args]
# This script builds and immediately runs with the given args
```

### Test
```bash
gradle test
# Uses JUnit 5 (junit-jupiter-engine:5.12.1)
```

### Install (system-wide)
```bash
# Linux/Mac
./install.sh

# Windows
./install.bat
# or
./install.ps1
```

After install, use the wrapper:
```bash
./newTestBandWidth.sh <host> [options]
newTestBandWidth.ps1 <host> [options]   # PowerShell
```

---

## Command-Line Arguments

| Argument | Default | Description |
|---|---|---|
| `<host>` (positional, arg 0) | — | iperf3 server hostname or IP |
| `-c <host>` / `-c<host>` | — | Alternative way to specify server |
| `-P <n>` / `-P<n>` | `8` | Parallel streams (`-P 1` enables single-thread mode) |
| `-t <sec>` / `-t<sec>` | `10` | Test duration in seconds |
| `-O <n>` / `-O<n>` | `2` | Omit first N seconds from results |
| `-R` | off | Reverse mode (receive instead of send) |
| `-l <n>` / `-l<n>` | `0` (no loop) | Repeat test N times |
| `-v` | off | Verbose output |
| `-d` | off | Debug mode (implies `-v`) |

Unknown options are passed through to iperf3 directly.

**Example**:
```bash
java -jar BandWidthTester-1.0-SNAPSHOT-all.jar myserver -P 4 -t 30 -R -l 5
```

**DUMB terminal example** (CI/scripts without ANSI support):
```bash
LANG=C TERM=dumb java -jar BandWidthTester-1.0-SNAPSHOT-all.jar localhost
```

---

## Key Architecture Decisions

### 1. Async Stream Monitoring
`Launcher` implements `Runnable` and runs in a dedicated thread per stream (stdout/stderr).
An `ArrayBlockingQueue` passes lines to `IPerf3Monitor` without blocking iperf3's process.
This prevents deadlocks and enables real-time progress display.

### 2. iperf3 Executable Discovery
`BandwidthTester.findIPerf3()` probes a hardcoded list of paths in order:
- Unix: `/usr/jaalocal/bin`, `/usr/local/bin`, `/usr/bin`, `/bin`, `/opt/local/bin`
- Windows: Several `C:/Program Files/iperf3*` paths

The app exits with an error if no executable is found.

### 3. Terminal Capability Detection
`TerminalType` checks `TERM` and `LANG` environment variables to detect:
- ANSI color support vs. DUMB terminal (plain text fallback)
- UTF-8 support (enables Unicode box-drawing characters vs. ASCII fallback)

Windows gets special ANSI support enabled via the console API through JNA (`OS.java`).

### 4. Fallback to Unbuffered Mode
If iperf3 returns exit code `999` (unrecognized `--forceflush`/`--connect-timeout` flags),
the tool retries without those flags.

### 5. Result Aggregation
When `-l` is used, each run produces a `ResultDetails` object. After all runs, results
are printed to stdout and saved to `results.txt` (in the current working directory).

---

## Code Conventions

### Naming
| Scope | Convention | Example |
|---|---|---|
| Classes | PascalCase | `BandwidthTester`, `ConnectionDetails` |
| Methods | camelCase | `findIPerf3()`, `processLine()` |
| Constants | UPPER_SNAKE_CASE | `ANSI_BOLD`, `BASE_COLOR_CODE` |
| Private instance fields | `m_` prefix | `m_launcherOut`, `m_localHost` |

### Style
- Java 17 language features
- Compiler warnings enabled: `-Xlint:unchecked`, `-Xlint:deprecation`
- JavaDoc on public classes and methods
- Try-with-resources for all file/stream operations
- `@author jerry` annotation on class JavaDoc

### Packages
All source lives under `org.jaa.bandwidthtester`. Do not create sub-packages.

---

## Dependencies

| Dependency | Version | Purpose |
|---|---|---|
| `net.java.dev.jna:jna-platform` | `5.10.0` | Windows console API (ANSI enable) |
| `org.slf4j:slf4j-simple` | `2.0.13` | Logging |
| `org.junit.jupiter:junit-jupiter-engine` | `5.12.1` | Unit testing (test scope) |
| `com.gradleup.shadow` | `9.0.0-beta15` | Fat JAR creation |

---

## Output Files

- **`results.txt`**: Written to the current working directory after a multi-run (`-l`) test.
  Contains ISO timestamp, return code (OK / Error=N), and bandwidth statistics per run.
  This file is gitignored.

---

## What NOT to Do

- Do not add sub-packages under `org.jaa.bandwidthtester`.
- Do not change the main class path — install scripts and build.gradle depend on
  `org.jaa.bandwidthtester.BandwidthTester`.
- Do not commit `results.txt`, `build/`, `.idea/`, or `.gradle/` — all are gitignored.
- Do not replace the `ArrayBlockingQueue` pattern in `IPerf3Monitor`/`Launcher` without
  understanding the deadlock risk of synchronous stream reads.
- Do not remove the ANSI fallback logic in `AnsiCodes`/`TerminalType` — DUMB terminal
  support is intentional.

---

## Testing Notes

- Run `gradle test` for unit tests (JUnit 5).
- There are no integration tests that auto-launch iperf3; manual testing against a real
  iperf3 server is required for end-to-end validation.
- For terminal-related tests, set `TERM=dumb` and `LANG=C` to exercise the plain-text path.

---

## Platform Notes

| Platform | Notes |
|---|---|
| Linux/Mac | Primary target; full ANSI + UTF-8 support when terminal allows |
| Windows | JNA used to enable ANSI via `SetConsoleMode`; batch and PowerShell wrappers provided |
| CI/Headless | Use `TERM=dumb LANG=C` to suppress ANSI sequences |
