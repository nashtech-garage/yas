🤖 AI AGENT INSTRUCTION: STANDARDIZED SERVICE IMPROVEMENT
🎯 MISSION
Standardize the CI/CD pipeline and reach >70% Test Coverage for EXACTLY ONE service from the approved list below.

📌 SCOPE (APPROVED SERVICES ONLY)
The AI Agent must only process ONE of the following services per execution:

backoffice-bff

recommendation

sampledata

storefront-bff

payment

🛠 EXECUTION WORKFLOW
Phase 1: Isolation (Branching)
You must not work directly on main.

Identify Target: Choose the service to be improved from the scope.

Create Branch: Name it feature/<SERVICE_NAME>-coverage-fix.

Bash
git checkout main && git pull origin main
git checkout -b feature/<SERVICE_NAME>-coverage-fix
Phase 2: JaCoCo & Test Implementation
Goal: Make mvn clean verify work and reach the 70% milestone.

POM Audit: Ensure jacoco-maven-plugin exists in <SERVICE_NAME>/pom.xml.

Add Tests:

Location: <SERVICE_NAME>/src/test/java/.

Strategy: Mock all external dependencies (DB, Kafka, Redis, API).

Goal: Reach >70% coverage (Instructions missed/covered).

Local Verify:

Command: mvn clean verify -pl <SERVICE_NAME> -am (or -f <SERVICE_NAME> if applicable).

Success Criteria: File <SERVICE_NAME>/target/site/jacoco/jacoco.csv is generated and contains coverage data.

Phase 3: CI Workflow Alignment
Standardize the file .github/workflows/<SERVICE_NAME>-ci.yaml to match the global Java pattern.

Required Job test Structure:

Permissions:

YAML
permissions:
  contents: read
  pull-requests: write
  checks: write
Standard Steps:

Checkout: uses: actions/checkout@v4 (fetch-depth: 0).

Setup Java/Maven: uses: ./.github/workflows/actions.

Run Maven: mvn clean verify -pl <SERVICE_NAME> -am -B.

Quality Gate (Console): Parse jacoco.csv. Fail build if coverage < 70%.

Add Coverage Report to PR: Use Madrapps/jacoco-report@v1.6.1 with min-coverage-overall: 70.

Upload Artifacts: Path must include jacoco and surefire-reports.

⚠️ STRICT CONSTRAINTS
Single Service Lock: You are forbidden from modifying files of any service other than the one currently being improved.

No "Dummy" Tests: Tests must assert real logic, not just empty executions.

No Threshold Lowering: You must not decrease the min-coverage-overall below 70% in the CI YAML.

Clean Code: Maintain the existing architectural patterns (Spring Boot, Records, Lombok).

📈 Local Calculation Script (For Agent Internal Check)
To verify coverage before pushing, run:

Bash
awk -F"," 'NR>1 { missed += $4; covered += $5 } END { if (missed+covered > 0) printf "CURRENT COVERAGE: %.2f%%\n", covered/(missed+covered)*100; else print "ERROR: No data" }' <SERVICE_NAME>/target/site/jacoco/jacoco.csv
END OF INSTRUCTION