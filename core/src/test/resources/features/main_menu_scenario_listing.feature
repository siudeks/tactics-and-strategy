Feature: Main menu scenario listing

  Scenario: Main menu displays all available game scenarios
    Given the main menu scenario list is available
    When I inspect scenario entries displayed in the main menu
    Then all available scenarios should be shown in order