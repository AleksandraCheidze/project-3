# Expense and Category Tracking

## Project Description

The "Expense and Category Tracking" project is a budget management application developed in Java.
The application allows users to keep track of their finances by recording expenses in different
categories and managing the list of categories.
The main idea is to provide a tool for monitoring and analyzing expenses across various aspects.

## Technical Requirements

- Language Version: Java 18-20.
- Dependencies: JUnit 8 (for testing).
- Entry Point: The main application file is `Main.java`, where the `main()` method is located, and
  the execution of the application starts.
- Required Files: The application uses `expenses.txt` to store expense data and `categories.txt` to
  store the list of categories.

## Project Structure

The project consists of the following classes and interfaces:

- `BudgetApp`: The main application class that manages user interaction, expense addition, and
  report generation.
- `ExpenseCategoryManager`: A class for managing expense categories, including adding and removing
  categories.
- `Expense`: A class representing an expense with fields for `category`, `amount`, and `date`.
- `ExpenseReportGenerator`: A class for generating expense reports, including reports by day, by
  category, and more.

The relationships between classes are as follows:

- `BudgetApp` uses `ExpenseCategoryManager` to manage categories.
- `BudgetApp` also uses `ExpenseReportGenerator` to generate reports.
- `ExpenseReportGenerator` works with data from the `Expense` class.

Tests for the project are described in separate test classes for each of the application classes,
using the JUnit 5 library. They verify the core functions and methods of the application.

