# Snyk and Sonar Setup

The Java service workflows in this repository use the reusable workflow at `/.github/workflows/java-service-ci-reusable.yml`.

## GitHub Secrets

Add these repository secrets in GitHub:

- `SNYK_TOKEN`
- `SONAR_TOKEN`

Open the repository on GitHub and go to `Settings` -> `Secrets and variables` -> `Actions` -> `New repository secret`.

## Where to get the tokens

- `SNYK_TOKEN`
  - Sign in to Snyk
  - Open `Account Settings`
  - Copy the `API Token`

- `SONAR_TOKEN`
  - SonarCloud: `My Account` -> `Security` -> `Generate Tokens`
  - SonarQube: open your user profile -> `Security` -> generate a token

## How the workflow uses them

- Snyk runs `snyk test` against each selected service `pom.xml`
- Sonar runs `sonar-maven-plugin:sonar` against each selected service `pom.xml`
- If a token is missing, that step is skipped and the rest of the CI still runs

## Current Java services wired to the reusable workflow

- media
- product
- cart
- order
- rating
- customer
- location
- inventory
- tax
- search
