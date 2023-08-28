import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.ArrayList;
import java.util.Date;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Scanner;

public class BudgetApp {

  private static final String FILE_PATH = "tasks.txt";
  private final Scanner scanner;
  private FileWriter writer;
  private final ArrayList<Expense> expenses = new ArrayList<>();
  private final List<String> expenseCategories = new ArrayList<>(ExpenseCategories.CATEGORIES);
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

  public BudgetApp() {
    scanner = new Scanner(System.in);
    try {
      this.writer = new FileWriter(FILE_PATH);
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }


  private void printMenu() {
    System.out.println("1. Добавить расходы");
    System.out.println("2. Посмотреть отчет расходов по дням");
    System.out.println("3. Посмотреть расходы по категориям");
    System.out.println("4. Выйти");
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
        case 1 -> addExpenses();
        case 2 -> viewConsumption();
        case 3 -> expenditureCategories();
        case 4 -> {
          exit();
          return;
        }
        default -> System.err.println("Неверный выбор");
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

  static int getUserChoice(Scanner scanner) {
    return scanner.nextInt();
  }

  private void addExpenses() {
    System.out.println("Выберите категорию расхода:");
    for (int i = 0; i < expenseCategories.size(); i++) {
      System.out.println((i + 1) + ". " + expenseCategories.get(i));
    }
    int categoryChoice = scanner.nextInt();
    scanner.nextLine();

    if (categoryChoice >= 1 && categoryChoice <= expenseCategories.size()) {
      String category = expenseCategories.get(categoryChoice - 1);

      System.out.println("Введите сумму расхода:");
      double amount = scanner.nextDouble();
      scanner.nextLine();

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
    } else {
      System.err.println("Неверный выбор категории.");
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

    SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");

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
    scanner.nextLine(); // Добавьте эту строку для очистки буфера
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


  private void exit () {
      try {
        writer.close();
      } catch (Exception e) {
        System.out.println(e.getMessage());
      }
      System.out.println("Выход");
      scanner.close();
    }
  }


