import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

public class ExpenseReportGenerator {

  private final List<Expense> expenses;
  private final SimpleDateFormat dateFormat = new SimpleDateFormat("dd.MM.yyyy");
  private final Scanner scanner;

  public ExpenseReportGenerator(List<Expense> expenses) {
    this.expenses = expenses;
    this.scanner = new Scanner(System.in);
  }

  public void viewExpensesByCategoryAndPeriod() {
    System.out.println("Отчет о расходах по категориям и периоду:");
    System.out.println("Выберите категорию:");
    List<String> categories = getDistinctCategories();
    for (int i = 0; i < categories.size(); i++) {
      System.out.println((i + 1) + ". " + categories.get(i));
    }
    int categoryChoice = getUserChoice(scanner, 1, categories.size());

    System.out.println("Введите начальную дату (в формате dd.MM.yyyy):");
    String startDateInput = scanner.next();
    System.out.println("Введите конечную дату (в формате dd.MM.yyyy):");
    String endDateInput = scanner.next();

    Date startDate = parseDate(startDateInput);
    Date endDate = parseDate(endDateInput);

    if (startDate != null && endDate != null) {
      String selectedCategory = categories.get(categoryChoice - 1);
      double totalExpenses = 0.0;

      for (Expense expense : expenses) {
        Date expenseDate = parseDate(expense.getDate());
        if (expenseDate != null && expense.getCategory().equals(selectedCategory)
            && expenseDate.compareTo(startDate) >= 0 && expenseDate.compareTo(endDate) <= 0) {
          totalExpenses += expense.getAmount();
        }
      }

      System.out.println("Расходы по категории '" + selectedCategory + "' за период с " + startDateInput + " по " + endDateInput + ": " + totalExpenses);
    } else {
      System.err.println("Неверный формат даты.");
    }
  }

  public void viewExpensesByDay() {
    System.out.println("Отчет о расходах по дням:");
    System.out.println("Введите дату (в формате dd.MM.yyyy):");
    String dateInput = scanner.next();
    Date selectedDate = parseDate(dateInput);

    if (selectedDate != null) {
      double totalExpenses = 0.0;

      for (Expense expense : expenses) {
        Date expenseDate = parseDate(expense.getDate());
        if (expenseDate != null && expenseDate.equals(selectedDate)) {
          totalExpenses += expense.getAmount();
        }
      }

      System.out.println("Расходы за " + dateInput + ": " + totalExpenses);
    } else {
      System.err.println("Неверный формат даты.");
    }
  }

  public void viewExpensesByMonthOfYear() {
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

  public void compareExpensesWithPreviousMonth() {
    System.out.println("Сравнение расходов с предыдущим месяцем:");
    System.out.println("Введите месяц (1-12) для сравнения:");
    int monthToCompare = scanner.nextInt();

    if (monthToCompare >= 1 && monthToCompare <= 12) {
      Calendar currentMonthStart = Calendar.getInstance();
      Calendar previousMonthStart = Calendar.getInstance();

      // Установка начала текущего месяца
      currentMonthStart.set(Calendar.DAY_OF_MONTH, 1);
      currentMonthStart.set(Calendar.MONTH, monthToCompare - 1); // -1, так как Calendar.MONTH начинается с 0 для января

      // Установка начала предыдущего месяца
      previousMonthStart.set(Calendar.MONTH, monthToCompare - 2); // -2, так как Calendar.MONTH начинается с 0, и мы вычитаем 2 для предыдущего месяца
      previousMonthStart.set(Calendar.DAY_OF_MONTH, 1);

      double totalExpensesCurrent = getTotalExpensesInMonth(currentMonthStart);
      double totalExpensesPrevious = getTotalExpensesInMonth(previousMonthStart);

      System.out.println("Расходы в текущем месяце: " + totalExpensesCurrent);
      System.out.println("Расходы в предыдущем месяце: " + totalExpensesPrevious);
    } else {
      System.err.println("Неверный формат месяца.");
    }
  }

  public void compareExpensesByYear() {
    System.out.println("Сравнение расходов с предыдущим годом:");
    System.out.println("Введите год для сравнения (в формате yyyy):");
    int yearToCompare = scanner.nextInt();

    if (yearToCompare >= 1000 && yearToCompare <= 9999) {
      double totalExpensesThisYear = getTotalExpensesForYear(yearToCompare);
      double totalExpensesPreviousYear = getTotalExpensesForYear(yearToCompare - 1);

      System.out.println("Расходы в " + yearToCompare + " году: " + totalExpensesThisYear);
      System.out.println("Расходы в " + (yearToCompare - 1) + " году: " + totalExpensesPreviousYear);

      if (totalExpensesPreviousYear != 0) {
        double difference = totalExpensesThisYear - totalExpensesPreviousYear;
        double percentageChange = (difference / totalExpensesPreviousYear) * 100;
        System.out.println("Изменение: " + difference + " (" + percentageChange + "%)");
      } else {
        System.out.println("Нет данных о расходах за предыдущий год.");
      }
    } else {
      System.err.println("Неверный формат года.");
    }
  }

  private double getTotalExpensesForYear(int year) {
    double totalExpenses = 0.0;
    for (Expense expense : expenses) {
      Date expenseDate = parseDate(expense.getDate());
      if (expenseDate != null) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(expenseDate);
        if (cal.get(Calendar.YEAR) == year) {
          totalExpenses += expense.getAmount();
        }
      }
    }
    return totalExpenses;
  }

  private double getTotalExpensesInMonth(Calendar monthStart) {
    double totalExpenses = 0.0;
    for (Expense expense : expenses) {
      Date expenseDate = parseDate(expense.getDate());
      if (expenseDate != null) {
        Calendar cal = Calendar.getInstance();
        cal.setTime(expenseDate);
        if (cal.get(Calendar.YEAR) == monthStart.get(Calendar.YEAR) &&
            cal.get(Calendar.MONTH) == monthStart.get(Calendar.MONTH)) {
          totalExpenses += expense.getAmount();
        }
      }
    }
    return totalExpenses;
  }

  private List<String> getDistinctCategories() {
    List<String> distinctCategories = new ArrayList<>();
    for (Expense expense : expenses) {
      String category = expense.getCategory();
      if (!distinctCategories.contains(category)) {
        distinctCategories.add(category);
      }
    }
    return distinctCategories;
  }

  private Date parseDate(String dateStr) {
    try {
      return dateFormat.parse(dateStr);
    } catch (ParseException e) {
      System.err.println("Ошибка при разборе даты: " + e.getMessage());
      return null;
    }
  }

  private int getUserChoice(Scanner scanner, int min, int max) {
    while (true) {
      try {
        int choice = scanner.nextInt();
        if (choice >= min && choice <= max) {
          return choice;
        } else {
          System.err.println("Пожалуйста, введите число в диапазоне от " + min + " до " + max + ".");
        }
      } catch (InputMismatchException e) {
        System.err.println("Пожалуйста, введите число.");
        scanner.next();
      }
    }
  }
}
