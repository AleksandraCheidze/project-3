import java.text.SimpleDateFormat;
import java.util.Calendar;
import java.util.Date;
import java.util.List;
import java.util.Scanner;

public class BudgetApp {

  private static final String FILE_PATH = "res/expenses.txt";
  private static final String MENU_OPTION_ADD_EXPENSE = "1";
  private static final String MENU_OPTION_SHOW_REPORTS = "2";
  private static final String MENU_OPTION_MANAGE_CATEGORIES = "3";
  private static final String MENU_OPTION_EXIT = "4";

  private static final String SUBMENU_OPTION_REPORT_EXPENSES_BY_CATEGORY = "1";
  private static final String SUBMENU_OPTION_COMPARE_EXPENSES_THIS_MONTH = "2";
  private static final String SUBMENU_OPTION_COMPARE_EXPENSES_THIS_YEAR = "3";
  private static final String SUBMENU_OPTION_BACK_TO_MAIN_MENU = "4";

  final List<Expense> expenses;
  private final ExpenseCategoryManager categoryManager;
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
  public Scanner scanner;

  public BudgetApp() {
    categoryManager = new ExpenseCategoryManager();
    scanner = new Scanner(System.in);
    expenses = Expense.loadExpensesFromFile(FILE_PATH);
  }

  public void run() {
    while (true) {
      displayMainMenu();
      String choice = getUserChoice(scanner);
      switch (choice) {
        case MENU_OPTION_ADD_EXPENSE:
          addExpense();
          break;
        case MENU_OPTION_SHOW_REPORTS:
          showReportsMenu();
          break;
        case MENU_OPTION_MANAGE_CATEGORIES:
          categoryManager.manageCategories(scanner);
          break;
        case MENU_OPTION_EXIT:
          Expense.saveExpensesToFile(expenses, FILE_PATH);
          exit();
          return;
        default:
          System.err.println("Неверный выбор.");
      }
    }
  }

  private void displayMainMenu() {
    System.out.println("╔════════════════════════════════════════════════╗");
    System.out.println("║              Бюджетное приложение              ║");
    System.out.println("╠════════════════════════════════════════════════╣");
    System.out.println("║  1. Новый расход                               ║");
    System.out.println("║  2. Отчеты                                     ║");
    System.out.println("║  3. Управление категориями                     ║");
    System.out.println("║  4. Выход                                      ║");
    System.out.println("╚════════════════════════════════════════════════╝");
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
      String reportChoice = getUserChoice(scanner);
      switch (reportChoice) {
        case SUBMENU_OPTION_REPORT_EXPENSES_BY_CATEGORY:
          reportGenerator.viewExpensesByCategoryAndPeriod();
          break;
        case SUBMENU_OPTION_COMPARE_EXPENSES_THIS_MONTH:
          reportGenerator.compareExpensesWithPreviousMonth();
          break;
        case SUBMENU_OPTION_COMPARE_EXPENSES_THIS_YEAR:
          reportGenerator.compareExpensesByYear();
          break;
        case SUBMENU_OPTION_BACK_TO_MAIN_MENU:
          isSubMenuRunning = false;
          break;
        default:
          System.err.println("Неверный выбор.");
      }
    }
  }

  /**
   * Prompts the user for their choice and returns it as a string.
   *
   * @param scanner The Scanner object for user input.
   * @return The user's choice as a string.
   */

   String getUserChoice(Scanner scanner) {
    System.out.print("Введите ваш выбор: ");
    return scanner.nextLine().trim();
  }

  /**
   * Prompts the user to choose an expense category from a list.
   *
   * @param scanner The Scanner object for user input.
   * @return The chosen expense category as a string, or null if an invalid choice is made.
   */

   String chooseExpenseCategory(Scanner scanner) {
    System.out.println("Выберите категорию расхода:");
    List<String> categories = categoryManager.getCategories();
    for (int i = 0; i < categories.size(); i++) {
      System.out.println((i + 1) + ". " + categories.get(i));
    }

    int categoryChoice = Integer.parseInt(getUserChoice(this.scanner));
    if (categoryChoice < 1 || categoryChoice > categories.size()) {
      System.err.println("Неверный выбор категории.");
      return null;
    }

    return categories.get(categoryChoice - 1);
  }

  /**
   * Prompts the user to enter the expense amount and returns it as a double.
   *
   * @return The entered expense amount as a double.
   */
   double enterExpenseAmount() {
    System.out.print("Введите сумму расхода: ");
    return getDoubleInput(scanner);
  }

  /**
   * Prompts the user to enter the expense date and returns it as a Date object.
   *
   * @return The entered expense date as a Date object.
   */
   Date enterExpenseDate() {
    boolean validDate = false;
    Date date = null;

    while (!validDate) {
      System.out.print("Введите дату расхода (в формате dd.MM.yyyy): ");
      String dateStr = scanner.nextLine();

      String[] dateParts = dateStr.split("\\.");
      if (dateParts.length != 3) {
        System.err.println("Неверный формат даты. Используйте формат dd.MM.yyyy.");
        continue;
      }

      int day = Integer.parseInt(dateParts[0]);
      int month = Integer.parseInt(dateParts[1]);
      int year = Integer.parseInt(dateParts[2]);

      Calendar calendar = Calendar.getInstance();
      calendar.setLenient(false);

      try {
        calendar.set(Calendar.DAY_OF_MONTH, day);
        calendar.set(Calendar.MONTH, month - 1);
        calendar.set(Calendar.YEAR, year);

        if (day < 1 || day > 31 || month < 1 || month > 12 || year < 1900 || year > 2100) {
          System.err.println("Неверные значение даты. Пожалуйста, введите корректную дату.");
          continue;
        }

        date = calendar.getTime();
        dateFormat.format(date);

        validDate = true;
      } catch (IllegalArgumentException e) {
        System.err.println("Неверная дата. Пожалуйста, введите корректную дату.");
      }
    }
    return date;
  }

  /**
   * Adds a new expense to the expenses list based on user input.
   * Prompts the user to choose a category, enter an amount, and input the date.
   */
  public void addExpense() {
    String category = chooseExpenseCategory(scanner);
    if (category == null) {
      return;
    }

    double amount = enterExpenseAmount();
    Date date = enterExpenseDate();

    Expense expense = new Expense(category, amount, dateFormat.format(date));
    expenses.add(expense);
    System.out.println("Расход успешно добавлен.");
  }

  /**
   * Helper method to get a double input from the user via the scanner.
   *
   * @param scanner The Scanner object for user input.
   * @return The entered double value.
   */

  double getDoubleInput(Scanner scanner) {
    while (true) {
      try {
        return Double.parseDouble(scanner.nextLine().trim());
      } catch (NumberFormatException e) {
        System.err.println("Неверный ввод. Введите число.");
      }
    }
  }

   void exit() {
    System.out.println("Выход");
    scanner.close();
  }
}
