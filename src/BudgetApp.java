import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.Calendar;
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
  public void addExpense() {
    System.out.println("Выберите категорию расхода:");
    List<String> categories = categoryManager.getCategories();
    for (int i = 0; i < categories.size(); i++) {
      System.out.println((i + 1) + ". " + categories.get(i));
    }

    int categoryChoice = getUserChoice(this.scanner);
    if (categoryChoice < 1 || categoryChoice > categories.size()) {
      System.err.println("Неверный выбор категории.");
      return;
    }

    String category = categories.get(categoryChoice - 1);

    System.out.println("Введите сумму расхода:");
    double amount = getDoubleInput();
    this.scanner.nextLine();

    boolean validDate = false;
    Date date = null;

    while (!validDate) {
      System.out.println("Введите дату расхода (в формате dd.MM.yyyy):");
      String dateStr = this.scanner.nextLine();

      // Разбиваем введенную дату на день, месяц и год
      String[] dateParts = dateStr.split("\\.");
      if (dateParts.length != 3) {
        System.err.println("Неверный формат даты. Используйте формат dd.MM.yyyy.");
        continue;
      }

      int day = Integer.parseInt(dateParts[0]);
      int month = Integer.parseInt(dateParts[1]);
      int year = Integer.parseInt(dateParts[2]);

      // Получаем календарь и устанавливаем его на введенную дату
      Calendar calendar = Calendar.getInstance();
      calendar.setLenient(false); // Отключаем "мягкое" преобразование дат

      try {
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month - 1); // Месяцы в Calendar начинаются с 0
        calendar.set(Calendar.YEAR, year);

        // Проверяем, что день, месяц и год находятся в допустимых диапазонах
        if (day < 1 || day > 31 || month < 1 || month > 12 || year < 1900 || year > 2100) {
          System.err.println("Неверные значение даты. Пожалуйста, введите корректную дату.");
          continue;
        }

        // Пытаемся распарсить введенную дату
        date = calendar.getTime();
        dateFormat.format(date); // Проверка на некорректные даты (например, 30 февраля)

        validDate = true;
      } catch (IllegalArgumentException e) {
        System.err.println("Неверная дата. Пожалуйста, введите корректную дату.");
      }
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