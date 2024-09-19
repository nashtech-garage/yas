# Automation project for YAS application

## Tentative technologies and frameworks

- Java 21
- Spring boot 3.2
- Cucumber
- Cucumber-Spring
- Selenium
- Cucumber-Junit
- SonarCloud


## Local development architecture
```
+----------------------------------------+
|      **Test Runner** (JUnit, TestNG)   |
|  - Executes the Cucumber scenarios     |
|  - Initiates WebDriver & test flow     |
+----------------------------------------+
                    |
                    v
+----------------------------------------+
|      **Feature File** (.feature)       |
|  - Written in Gherkin language         |
|  - Defines scenarios (Given, When, Then)|
+----------------------------------------+
                    |
                    v
+----------------------------------------+
|   **Step Definitions** (using Java)    |
|  - Maps Gherkin steps to actual code   |
|  - Uses Selenium WebDriver for actions |
+----------------------------------------+
                    |
                    v
+----------------------------------------+
|      **Selenium WebDriver**            |
|  - Automates browser interactions      |
|  - Opens browser, simulates actions    |
|  - Fetches web elements, etc.          |
+----------------------------------------+
                    |
                    v
+----------------------------------------+
|      **Web Application**               |
|  - The actual application under test   |
+----------------------------------------+
```

## Getting started :

1. Make sure Yas application is up and running.
2. If you want to execute all scenarios in storefront, go to storefront project and execute command: mvn clean test , all test scenarios will be executed. Another way is running JUnitCucumberRunner class using IDE.