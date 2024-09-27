Feature: Create Product

  Scenario: Create Product successfully (with general information, product images)
    Given I logged in successfully
    When I click to product on menu
    Then I should be in product list page
    When I click to create product button
    Then I should be in create product page
    Given I fill necessary data for product and submit
    Then I should be in product list page
    Then Created product shown in product list

  Scenario: Edit Product successfully
    Given I logged in successfully
    When I click to product on menu
    Then I should be in product list page
    When I click to edit icon on row
    Then I should be in edit product page
    Given I update necessary data for product and submit
    Then I should be in product list page
    Then Updated product shown in product list

  Scenario: Delete Product successfully
    Given I logged in successfully
    When I click to product on menu
    Then I should be in product list page
    When I click to delete icon on row
    Then It shows popup confirm with button Delete
    When I click on button Delete
    Then This item is not existed in product list