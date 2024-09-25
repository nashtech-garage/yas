Feature: User registration Flow

  Scenario: Register user successfully
    Given I'm on the home page
    When I click the login link on header
    Then I should be redirected to the welcome page
    When I click on the register button
    Then I should be redirected to the register page
    Given I fill necessary data for registration user and click Register button
    Then I should be redirected to the home page and display this user

  Scenario: Register user unsuccessfully - invalid email
    Given I'm on the home page
    When I click the login link on header
    Then I should be redirected to the welcome page
    When I click on the register button
    Then I should be redirected to the register page
    Given I fill invalid email and click Register button
    Then I should be kept the register page and display error message

  Scenario: Back to login page from user registration page
    Given I'm on the home page
    When I click the login link on header
    Then I should be redirected to the welcome page
    When I click on the register button
    Then I should be redirected to the register page
    When I click on Back to Login link
    Then I should be redirected to the welcome page