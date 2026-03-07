# BandWidthTester AGENTS.md

This file defines the build, test, and lint commands for the BandWidthTester project, along with code style and formatting guidelines.

---

## Build Commands

```bash
# Build the JAR file
mvn clean package
```

```bash
# Build with tests
mvn clean package -DskipTests=false
```

```bash
# Build without tests
mvn clean package -DskipTests=true
```

```bash
# Build and install locally
mvn clean install
```

---

## Test Commands

### Running Tests

```bash
# Run all tests
mvn test
```

```bash
# Run a single test class
mvn test -Dtest=ConnectionDetailsTest
```

```bash
# Run a single test method
mvn test -Dtest=ConnectionDetailsTest#testMaxBitsBytesPerSec
```

```bash
# Run tests with debug output
mvn test -Dtest=ConnectionDetailsTest -Dmaven.surefire.debug=true
```

### Test Coverage

```bash
# Generate test coverage report
mvn jacoco:report
```

```bash
# View coverage report in browser
open target/site/jacoco/index.html
```

---

## Lint and Formatting

### Code Style

**Java Language Version**: 21

**Formatting Rules**:
- 4 spaces for indentation
- No tabs
- Class names: `PascalCase`
- Method names: `camelCase`
- Variable names: `camelCase`
- Constant names: `UPPER_CASE`
- Interface names: `PascalCase`
- Package names: `lowercase`

**Import Order**:
1. `java.` packages
2. `javax.` packages
3. Third-party libraries (`com.`, `org.`)
4. Project imports
5. Blank line
6. Static imports

**Line Length**: 120 characters

### Linting

```bash
# Run checkstyle
mvn checkstyle:check
```

```bash
# Run spotbugs
mvn spotbugs:check
```

```bash
# Run pmd
mvn pmd:check
```

```bash
# Run all static analysis tools
mvn verify
```

```bash
# Run checkstyle with error level
mvn checkstyle:check -Dcheckstyle.failOnViolation=true
```

### Formatting

```bash
# Format code using Google Java Format
mvn google-java-format:format
```

```bash
# Check formatting without changes
mvn google-java-format:check
```

```bash
# Format with specific profile
mvn google-java-format:format -DgoogleJavaFormat.profile=custom
```

---

## Code Structure

### Directory Layout
```
src/main/java/
├── org/jaa/bandwidthtester/
│   ├── Args.java
│   ├── ANSI.java
│   ├── ConnectionDetails.java
│   ├── Executor.java
│   ├── IPerf3Monitor.java
│   ├── Launcher.java
│   ├── MonitorIPerf3Output.java
│   ├── OS.java
│   ├── ResultDetails.java
│   ├── TerminalType.java
│   └── BandwidthTester.java
│
└── resources/
    └── (any resource files)
```

### Key Classes

- `BandwidthTester.java`: Main entry point
- `IPerf3Monitor.java`: Manages iperf3 execution
- `MonitorIPerf3Output.java`: Processes and displays output
- `ConnectionDetails.java`: Tracks connection statistics

---

## Development Workflow

```bash
# Quick development cycle
mvn clean package
# Run tests
mvn test
# Rebuild with changes
mvn package -DskipTests
```

```bash
# Debug with breakpoints
mvnDebug -Dtest=ConnectionDetailsTest
```

```bash
# Run with custom arguments
java -jar target/BandWidthTester-1.0-SNAPSHOT.jar localhost
```

---

## Logging and Debugging

```bash
# Enable verbose output
java -jar target/BandWidthTester-1.0-SNAPSHOT.jar localhost -v

# Enable debug output
java -jar target/BandWidthTester-1.0-SNAPSHOT.jar localhost -d
```

```bash
# View results in file
cat results.txt
```

---

## Release Process

```bash
# Prepare for release
mvn release:prepare

# Perform release
mvn release:perform

# Clean release state
mvn release:clean
```

```bash
# Deploy to Maven Central
mvn deploy -P release
```

---

## Build Verification

```bash
# Verify build
mvn verify

# Check dependency tree
mvn dependency:tree

# Analyze dependencies
mvn dependency:analyze
```

---

## IDE Setup

### IntelliJ IDEA
- Install Maven plugin
- Import project using `pom.xml`
- Configure code style to match Google Java Style
- Enable Checkstyle and SpotBugs inspections

### VS Code
- Install Java extensions
- Open project directory
- Run `mvn clean install` once to set up

---

## Contribution Guidelines

1. Fork the repository
2. Create feature branch: `git checkout -b feature/your-feature`
3. Commit changes with meaningful messages
4. Run tests: `mvn test`
5. Run lint: `mvn checkstyle:check`
6. Push changes: `git push origin feature/your-feature`
7. Create pull request

```bash
# Update from main
git checkout main
git pull
git checkout feature/your-feature
git merge main
git push origin feature/your-feature
```

---

## Code Quality Metrics

```bash
# View code coverage
mvn jacoco:report
open target/site/jacoco/index.html

# View code complexity
mvn pmd:cpd-check
```

## License

This project is licensed under the MIT License - see the LICENSE file for details.