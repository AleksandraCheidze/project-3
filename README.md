# Expense and Category Tracking

## Project Description

The "Expense and Category Tracking" project is a Java-based budget management application designed
to help users efficiently manage their finances. This application enables users to track their
expenses across various categories, create and manage expense records, and generate insightful
financial reports. It offers a comprehensive solution for monitoring and analyzing personal
expenses.

## Technical Requirements

- **Programming Language:** Java 18-20.
- **Dependencies:** JUnit 8 (for testing).
- **Entry Point:** The primary entry point for the application is the `Main.java` file, housing
  the `main()` method that initiates the application's execution.
- **Required Files:** The application utilizes two essential files, `expenses.txt` for storing
  expense data and `categories.txt` for maintaining a list of categories.

## Project Structure

The project is organized around key classes and interfaces:

- **`BudgetApp`**: The central application class responsible for user interactions, expense
  recording, and report generation.
- **`ExpenseCategoryManager`**: A class dedicated to managing expense categories, offering
  functionalities for adding and removing categories.
- **`Expense`**: A class representing individual expense records, featuring fields
  for `category`, `amount`, and `date`.
- **`ExpenseReportGenerator`**: A versatile class designed for generating a variety of expense
  reports, including reports categorized by day, by category, and more.

The relationships between these classes are as follows:

- `BudgetApp` utilizes `ExpenseCategoryManager` to manage and manipulate categories.
- `BudgetApp` leverages `ExpenseReportGenerator` for generating detailed financial reports.
- `ExpenseReportGenerator` relies on data from the `Expense` class to produce meaningful reports.

To ensure code quality and functionality,
the project includes comprehensive test coverage with dedicated
test classes for each application class, utilizing the JUnit 8
library. These tests validate the core functions and methods of
the application, guaranteeing its reliability and accuracy.