Feature: User Login Flow

  Scenario: Successful login with valid credentials
    Given I am on the home page
    When I click on the login link
    Then I should be redirected to the login page
    When I enter valid credentials
    And I click on the login button
    Then I should be redirected to the dashboard

  Scenario: Unsuccessful login with invalid credentials
    Given I am on the home page
    When I click on the login link
    Then I should be redirected to the login page
    When I enter invalid credentials
    And I click on the login button
    Then I should see an error message