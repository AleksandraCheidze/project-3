import java.io.*;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BudgetApp {

  private static final String FILE_PATH = "tasks.txt";
  private final Scanner scanner;
  private final ArrayList<Expense> expenses = new ArrayList<>();
  private final ExpenseCategoryManager categoryManager;
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

  public BudgetApp() {
    categoryManager = new ExpenseCategoryManager();
    scanner = new Scanner(System.in);
    loadExpensesFromFile();
  }

  public static int getUserChoice(Scanner scanner) {
    return -1;
  }

  private void printMenu() {
    System.out.println("1. Добавить расходы");
    System.out.println("2. Посмотреть отчет расходов по дням");
    System.out.println("3. Посмотреть расходы за период по категориям");
    System.out.println("4. Посмотреть расходы за год по месяцам ");
    System.out.println("5. Управление категориями");
    System.out.println("6. Выйти");
  }

  public void start() {
    while (true) {
      printMenu();
      int choice;
      if (scanner.hasNextInt()) {
        choice = scanner.nextInt();
      } else {
        System.err.println("Пожалуйста, введите число.");
        scanner.next();
        continue;
      }

      switch (choice) {
        case 1:
          addExpenses();
          break;
        case 2:
          viewConsumption();
          break;
        case 3:
          expenditureCategories();
          break;
        case 4:
          categoryPerYear();
          break;
        case 5:
          categoryManager.manageCategories(scanner);
          break;
        case 6:
          exit();
          return;
        default:
          System.err.println("Неверный выбор");
      }
    }
  }

  private Date parseDate(String dateString) {
    try {
      return dateFormat.parse(dateString);
    } catch (ParseException e) {
      return null;
    }
  }

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
    } catch (FileNotFoundException e) {
      // File not found, it's okay, there might not be any existing data
    } catch (Exception e) {
      System.err.println("Ошибка при чтении из файла: " + e.getMessage());
    }
  }

  private void addExpenses() {
    System.out.println("Выберите категорию расхода:");
    List<String> categories = categoryManager.getCategories();
    for (int i = 0; i < categories.size(); i++) {
      System.out.println((i + 1) + ". " + categories.get(i));
    }

    int categoryChoice;
    while (true) {
      if (scanner.hasNextInt()) {
        categoryChoice = scanner.nextInt();
        if (categoryChoice >= 1 && categoryChoice <= categories.size()) {
          break;  // Верный выбор, выход из цикла
        } else {
          System.err.println("Неверный выбор категории.");
        }
      } else {
        System.err.println("Пожалуйста, введите число.");
        scanner.next(); // Игнорирование неверного ввода
      }
    }
    scanner.nextLine(); // Игнорирование символа новой строки после nextInt()

    String category = categories.get(categoryChoice - 1);

    System.out.println("Введите сумму расхода:");
    double amount;
    while (true) {
      if (scanner.hasNextDouble()) {
        amount = scanner.nextDouble();
        break;
      } else {
        System.err.println("Пожалуйста, введите действительное число.");
        scanner.next(); // Игнорирование неверного ввода
      }
    }
    scanner.nextLine(); // Игнорирование символа новой строки после nextDouble()

    System.out.println("Введите дату расхода (в формате dd.MM.yyyy):");
    String dateInput = scanner.nextLine();

    Date date = parseDate(dateInput);
    if (date != null) {
      String formattedDate = dateFormat.format(date);

      Expense newExpense = new Expense(category, amount, formattedDate);
      expenses.add(newExpense);

      try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
        writer.write(category + " " + amount + " " + formattedDate + "\n");
        System.out.println("Расход добавлен успешно!");
      } catch (IOException e) {
        System.err.println("Ошибка при записи в файл: " + e.getMessage());
      }
    } else {
      System.err.println("Неверный формат даты.");
    }
  }

  private void viewConsumption() {
    System.out.println("Отчет о расходах по дням:");

    Map<String, Double> dailyExpenses = new HashMap<>();

    for (Expense expense : expenses) {
      String date = expense.getDate();
      double amount = expense.getAmount();

      dailyExpenses.put(date, dailyExpenses.getOrDefault(date, 0.0) + amount);
    }

    for (Map.Entry<String, Double> entry : dailyExpenses.entrySet()) {
      String date = entry.getKey();
      double amount = entry.getValue();

      System.out.println("Дата: " + date);
      System.out.println("Сумма: " + amount);
      System.out.println();
    }
  }

  private void expenditureCategories() {
    System.out.println("Введите начальную дату (в формате dd.MM.yyyy):");
    scanner.nextLine();
    String startDateInput = scanner.nextLine();

    System.out.println("Введите конечную дату (в формате dd.MM.yyyy):");
    String endDateInput = scanner.nextLine();

    Date startDate = parseDate(startDateInput);
    Date endDate = parseDate(endDateInput);

    if (startDate != null && endDate != null) {
      System.out.println("Расходы по выбранному периоду:");

      boolean hasExpensesInPeriod = false;

      for (Expense expense : expenses) {
        Date expenseDate = parseDate(expense.getDate());
        if (expenseDate != null && expenseDate.compareTo(startDate) >= 0 && expenseDate.compareTo(endDate) <= 0) {
          System.out.println("Дата: " + expense.getDate());
          System.out.println("Категория: " + expense.getCategory());
          System.out.println("Сумма: " + expense.getAmount());
          System.out.println();
          hasExpensesInPeriod = true;
        }
      }

      if (!hasExpensesInPeriod) {
        System.out.println("В выбранном периоде нет расходов.");
      }
    } else {
      System.err.println("Неверный формат даты.");
    }
  }

  private void categoryPerYear() {
    System.out.println("Посмотреть расходы по категориям за год:");

    System.out.println("Введите год (в формате yyyy):");
    String yearInput = scanner.next();

    int year;
    try {
      year = Integer.parseInt(yearInput);
    } catch (NumberFormatException e) {
      System.err.println("Неверный формат года.");
      return;
    }

    System.out.println("Расходы по категориям за " + year + " год:");

    Map<Month, Double> categoryExpensesPerMonth = new HashMap<>();
    for (Month month : Month.values()) {
      double totalExpenseForMonth = 0.0;
      for (Expense expense : expenses) {
        Date expenseDate = parseDate(expense.getDate());
        if (expenseDate != null) {
          Calendar cal = Calendar.getInstance();
          cal.setTime(expenseDate);
          if (cal.get(Calendar.YEAR) == year && cal.get(Calendar.MONTH) == month.ordinal()) {
            totalExpenseForMonth += expense.getAmount();
          }
        }
      }
      categoryExpensesPerMonth.put(month, totalExpenseForMonth);
    }

    for (Month month : Month.values()) {
      double amount = categoryExpensesPerMonth.getOrDefault(month, 0.0);
      System.out.println("Месяц: " + month.getName());
      System.out.println("Сумма затрат: " + amount);
      System.out.println();
    }
  }

  private void exit() {
    System.out.println("Выход");
    scanner.close();
  }
}
