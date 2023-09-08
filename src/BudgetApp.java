import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.InputMismatchException;
import java.util.List;
import java.util.Scanner;

public class BudgetApp {

  private static final String FILE_PATH = "res/expenses.txt";
  private final Scanner scanner;
  private final List<Expense> expenses;
  private final ExpenseCategoryManager categoryManager;
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

  public BudgetApp() {
    categoryManager = new ExpenseCategoryManager();
    scanner = new Scanner(System.in);
    expenses = Expense.loadExpensesFromFile(FILE_PATH);
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
          Expense.saveExpensesToFile(expenses, FILE_PATH);
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
      System.out.println("1. Отчет о расходах по категориям и периоду");
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
    scanner.nextLine();

    System.out.println("Введите дату расхода (в формате dd.MM.yyyy):");
    String dateStr = scanner.nextLine().trim();
    Date date;
    try {
      date = dateFormat.parse(dateStr);
    } catch (ParseException e) {
      System.err.println("Неверный формат даты. Используйте формат dd.MM.yyyy.");
      return;
    }

    Expense expense = new Expense(category, amount, dateFormat.format(date));
    expenses.add(expense);
    System.out.println("Расход успешно добавлен.");
  }

  private double getDoubleInput() {
    while (true) {
      try {
        return scanner.nextDouble();
      } catch (InputMismatchException e) {
        System.err.println("Неверный ввод. Введите число.");
        scanner.nextLine();
      }
    }
  }

  private void exit() {
    System.out.println("Выход");
    scanner.close();
  }
}