Feature: Create Category

  Scenario: Successfully create a new category
    Given I am logged in successfully
    When I click on the "categories" option in the menu
    Then I should be redirected to the category list page
    When I click on the Create Category button
    Then I should be redirected to the create category page
    Given I have filled in all the necessary data for the new category
    When I click the Save button
    Then I should be redirected to the category list page again
    And the new category should be displayed in the category list