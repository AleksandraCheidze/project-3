import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class BudgetApp {

  private static final String FILE_PATH = "tasks.txt";
  private final Scanner scanner;
  private FileWriter writer;
  private final ArrayList<Expense> expenses = new ArrayList<>();
  private final List<String> expenseCategories = new ArrayList<>(ExpenseCategories.CATEGORIES);
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
  private final SimpleDateFormat monthYearFormat = new SimpleDateFormat("MM.yyyy");
  private final ExpenseCategoryManager categoryManager;
  public BudgetApp() {

    categoryManager = new ExpenseCategoryManager();

    scanner = new Scanner(System.in);
    try {
      this.writer = new FileWriter(FILE_PATH, true); // Append mode to not overwrite existing data
      loadExpensesFromFile(); // Load expenses from file if available
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
  }


  private void printMenu() {
    System.out.println("1. Добавить расходы");
    System.out.println("2. Посмотреть отчет расходов по дням");
    System.out.println("3. Посмотреть расходы за период по категориям");
    System.out.println("4. Посмотреть расходы за год");
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
        case 1 -> addExpenses();
        case 2 -> viewConsumption();
        case 3 -> expenditureCategories();
        case 4 -> categoryPerYear();
        case 5 -> manageCategories();
        case 6 -> {
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

  private void loadExpensesFromFile() { //Метод для загрузки расходов из файла, если доступно
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

  private void viewConsumption() {  // Метод для просмотра отчета о расходах по дням
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

  private void expenditureCategories() { // Метод для просмотра расходов за выбранный период по категориям
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



  private Date parseMonthYear(String dateString) {
    //для преобразования введенной пользователем строки в дату.
    try {
      return monthYearFormat.parse(dateString);
    } catch (ParseException e) {
      return null;
    }
  }

  private boolean isSameMonthYear(Date date1, Date date2) {
    // метод для проверки, являются ли две даты одним и тем же месяцем и годом.
    Calendar cal1 = Calendar.getInstance();
    cal1.setTime(date1);

    Calendar cal2 = Calendar.getInstance();
    cal2.setTime(date2);

    return cal1.get(Calendar.MONTH) == cal2.get(Calendar.MONTH) &&
        cal1.get(Calendar.YEAR) == cal2.get(Calendar.YEAR);
  }

  private void compareExpensesByMonth() {
    System.out.println("Сравнение затрат по месяцам:");

    Map<Month, Double> monthlyExpenses = new HashMap<>();

    for (Expense expense : expenses) {
      Date expenseDate = parseDate(expense.getDate());
      if (expenseDate != null) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(expenseDate);
        Month month = Month.values()[cal.get(Calendar.MONTH)];
        double amount = expense.getAmount();
        monthlyExpenses.put(month, monthlyExpenses.getOrDefault(month, 0.0) + amount);
      }
    }

    for (Map.Entry<Month, Double> entry : monthlyExpenses.entrySet()) {
      Month month = entry.getKey();
      double amount = entry.getValue();
      System.out.println("Месяц: " + month.getName());
      System.out.println("Сумма затрат: " + amount);
      System.out.println();
    }
  }

  private void manageCategories() {
    while (true) {
      System.out.println("Управление категориями:");
      System.out.println("1. Добавить категорию");
      System.out.println("2. Удалить категорию");
      System.out.println("3. Вернуться в главное меню");

      int choice;
      if (scanner.hasNextInt()) {
        choice = scanner.nextInt();
      } else {
        System.err.println("Пожалуйста, введите число.");
        scanner.next();
        continue;
      }

      switch (choice) {
        case 1 -> addCategory();
        case 2 -> removeCategory();
        case 3 -> {
          return;
        }
        default -> System.err.println("Неверный выбор");
      }
    }
  }

  private void addCategory() {
    System.out.println("Введите название новой категории:");
    scanner.nextLine(); // Очистка буфера
    String newCategory = scanner.nextLine().trim();
    if (!newCategory.isEmpty()) {
      boolean added = categoryManager.addCategory(newCategory);
      if (added) {
        updateCategoryList();
      }
    } else {
      System.err.println("Название категории не может быть пустым.");
    }
  }

  private void removeCategory() {
    System.out.println("Введите название категории для удаления:");
    scanner.nextLine(); // Очистка буфера
    String categoryToRemove = scanner.nextLine().trim();
    if (!categoryToRemove.isEmpty()) {
      boolean removed = categoryManager.removeCategory(categoryToRemove);
      if (removed) {
        updateCategoryList();
      }
    } else {
      System.err.println("Название категории не может быть пустым.");
    }
  }

  private void updateCategoryList() {
    expenseCategories.clear();
    expenseCategories.addAll(categoryManager.getCategories());
  }



  private void exit() {
    try {
      writer.close();
    } catch (Exception e) {
      System.out.println(e.getMessage());
    }
    System.out.println("Выход");
    scanner.close();
  }
}