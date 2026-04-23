Feature: User Login Flow

  Scenario: Successful login with valid credentials
    Given I am on the home page
    When I enter valid credentials
    And I click on the login button
    Then I should be redirected to the dashboard