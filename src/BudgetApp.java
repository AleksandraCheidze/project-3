import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BudgetApp {

  private static final String FILE_PATH = "res/expenses.txt";
  private final Scanner scanner;
  private final List<Expense> expenses = new ArrayList<>();
  private final ExpenseCategoryManager categoryManager;
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

  public BudgetApp() {
    categoryManager = new ExpenseCategoryManager();
    scanner = new Scanner(System.in);
    loadExpensesFromFile();
  }

  /**
   * Starts the Expense and Category Tracking application.
   * This method initializes the application, provides a user interface, and manages user interactions.
   * It allows users to record expenses, manage expense categories, and generate expense reports.
   * The application continues to run until the user chooses to exit.
   */
  public void run() {
    while (true) {
      displayMainMenu();
      int choice = getUserChoice(scanner);
      switch (choice) {
        case 1 -> addExpense();
        case 2 -> showReportsMenu();
        case 3 -> categoryManager.manageCategories(scanner);
        case 4 -> {
          saveExpensesToFile();
          exit();
          return;
        }
        default -> System.err.println("Неверный выбор.");
      }
    }
  }

  /**
   * Displays the main menu and handles user interactions.
   */
  private void displayMainMenu() {
    System.out.println("╔════════════════════════════════════════════════╗");
    System.out.println("║              Бюджетное приложение              ║");
    System.out.println("╠════════════════════════════════════════════════╣");
    System.out.println("║  1. Новый расход                               ║");
    System.out.println("║  2. Отчеты                                     ║");
    System.out.println("║  3. Управление категориями                     ║");
    System.out.println("║  4. Выход                                      ║");
    System.out.println("╚════════════════════════════════════════════════╝");
    System.out.print("Выберите действие: ");
  }

  private void showReportsMenu() {
    boolean isSubMenuRunning = true;
    ExpenseReportGenerator reportGenerator = new ExpenseReportGenerator(expenses);
    while (isSubMenuRunning) {
      System.out.println("Меню отчетов:");
      System.out.println("1. Таблица расходов по месяцам и категориям");
      System.out.println("2. Сравнить расходы текущего месяца с прошлым");
      System.out.println("3. Сравнить расходы текущего года с прошлым");
      System.out.println("4. Назад в главное меню");
      int reportChoice = getUserChoice(scanner);
      switch (reportChoice) {
        case 1 -> reportGenerator.viewExpensesByCategoryAndPeriod();
        case 2 -> reportGenerator.compareExpensesWithPreviousMonth();
        case 3 -> reportGenerator.compareExpensesByYear();
        case 4 -> isSubMenuRunning = false;
        default -> System.err.println("Неверный выбор.");
      }
    }
  }

  private int getUserChoice(Scanner scanner) {
    try {
      int choice = scanner.nextInt();
      scanner.nextLine();
      return choice;
    } catch (InputMismatchException e) {
      System.err.println("Неверный ввод. Введите число.");
      scanner.nextLine();
      return -1;
    }
  }

  /**
   * Prompts the user to add a new expense and handles the addition.
   */
  private void addExpense() {
    System.out.println("Выберите категорию расхода:");
    List<String> categories = categoryManager.getCategories();
    for (int i = 0; i < categories.size(); i++) {
      System.out.println((i + 1) + ". " + categories.get(i));
    }

    int categoryChoice = getUserChoice(scanner);
    if (categoryChoice < 1 || categoryChoice > categories.size()) {
      System.err.println("Неверный выбор категории.");
      return;
    }

    String category = categories.get(categoryChoice - 1);

    System.out.println("Введите сумму расхода:");
    double amount = getDoubleInput();

    System.out.println("Введите дату расхода (в формате dd.MM.yyyy):");
    Date date = getDateInput();

    if (date != null) {
      Expense newExpense = new Expense(category, amount, dateFormat.format(date));
      expenses.add(newExpense);
      System.out.println("Расход добавлен успешно!");
    } else {
      System.err.println("Неверный формат даты.");
    }
  }

  private double getDoubleInput() {
    double amount;
    while (true) {
      try {
        amount = Double.parseDouble(scanner.nextLine());
        break;
      } catch (NumberFormatException e) {
        System.err.println("Пожалуйста, введите действительное число.");
      }
    }
    return amount;
  }

  private Date getDateInput() {
    Date date = null;
    while (date == null) {
      try {
        String dateString = scanner.nextLine();
        date = dateFormat.parse(dateString);
      } catch (ParseException e) {
        System.err.println("Неверный формат даты. Введите дату в формате dd.MM.yyyy:");
      }
    }
    return date;
  }

  /**
   * Prompts the user to manage expense categories.
   */
  private void loadExpensesFromFile() {
    try (Scanner fileScanner = new Scanner(new File(FILE_PATH))) {
      while (fileScanner.hasNextLine()) {
        String line = fileScanner.nextLine();
        String[] parts = line.split(" ");
        if (parts.length >= 3) {
          String category = parts[0];
          double amount = Double.parseDouble(parts[1]);
          String date = parts[2];
          expenses.add(new Expense(category, amount, date));
        }
      }
    } catch (Exception e) {
      System.err.println("Невозможно считать расходы: " + e.getMessage());
    }
  }

  /**
   * Saves expenses to the "expenses.txt" file.
   */
  private void saveExpensesToFile() {
    try (FileWriter writer = new FileWriter(FILE_PATH)) {
      for (Expense expense : expenses) {
        writer.write(
            expense.getCategory() + " " + expense.getAmount() + " " + expense.getDate() + "\n");
      }
      System.out.println("Расходы сохранены.");
    } catch (IOException e) {
      System.err.println("Ошибка при сохранении расходов: " + e.getMessage());
    }
  }

  private void exit() {
    System.out.println("Выход");
    scanner.close();
  }
}