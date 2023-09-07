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
    System.out.println("Выберите категорию или введите 0 для выбора всех категорий:");
    List<String> categories = getDistinctCategories();
    for (int i = 0; i < categories.size(); i++) {
      System.out.println((i + 1) + ". " + categories.get(i));
    }
    int categoryChoice = getUserChoice(scanner, 0, categories.size() - 1); // Allow 0 for all categories

    System.out.println("Введите начальную дату (в формате dd.MM.yyyy):");
    String startDateInput = scanner.next();
    System.out.println("Введите конечную дату (в формате dd.MM.yyyy):");
    String endDateInput = scanner.next();

    Date startDate = parseDate(startDateInput);
    Date endDate = parseDate(endDateInput);

    if (startDate != null && endDate != null) {
      String selectedCategory = categoryChoice == 0 ? "Все категории" : categories.get(categoryChoice - 1);
      double totalExpenses = 0.0;
      Map<String, Double> categoryExpensesMap = new HashMap<>();

      System.out.println("======================================");
      System.out.println("Категория: " + selectedCategory);
      System.out.println("Период: с " + startDateInput + " по " + endDateInput);

      for (Expense expense : expenses) {
        Date expenseDate = parseDate(expense.getDate());
        String expenseCategory = expense.getCategory();
        if (expenseDate != null && (selectedCategory.equals("Все категории") || expenseCategory.equals(selectedCategory))
            && expenseDate.compareTo(startDate) >= 0 && expenseDate.compareTo(endDate) <= 0) {
          totalExpenses += expense.getAmount();

          categoryExpensesMap.put(expenseCategory, categoryExpensesMap.getOrDefault(expenseCategory, 0.0) + expense.getAmount());
        }
      }

      System.out.println("Итоговые расходы: " + totalExpenses);

      String maxCategory = "";
      double maxCategoryExpenses = 0.0;

      for (Map.Entry<String, Double> entry : categoryExpensesMap.entrySet()) {
        String category = entry.getKey();
        double categoryExpenses = entry.getValue();
        if (categoryExpenses > maxCategoryExpenses) {
          maxCategoryExpenses = categoryExpenses;
          maxCategory = category;
        }
      }

      if (selectedCategory.equals("Все категории")) {
        System.out.println("\u001B[33mНаибольшие расходы в категории '" + maxCategory + "' в периоде " + startDateInput + " по " + endDateInput + ": " + maxCategoryExpenses + "\u001B[0m");
      } else if (!selectedCategory.isEmpty()) {
        return;
      }

      System.out.println("=========================================");
      System.out.println("Таблица расходов по месяцам и категориям:");
      System.out.println("Месяц/Год | Категория          | Расходы");
      System.out.println("-----------------------------------------");

      Map<String, Map<String, Double>> categoryExpensesByMonth = new HashMap<>();

      for (Expense expense : expenses) {
        Date expenseDate = parseDate(expense.getDate());
        String expenseCategory = expense.getCategory();
        if (expenseDate != null && (selectedCategory.equals("Все категории") || expenseCategory.equals(selectedCategory))
            && expenseDate.compareTo(startDate) >= 0 && expenseDate.compareTo(endDate) <= 0) {
          Calendar cal = Calendar.getInstance();
          cal.setTime(expenseDate);
          String monthYearKey = (cal.get(Calendar.MONTH) + 1) + "/" + cal.get(Calendar.YEAR); // Adding 1 to month as it is zero-based

          categoryExpensesByMonth.computeIfAbsent(monthYearKey, k -> new HashMap<>());
          categoryExpensesByMonth.get(monthYearKey).put(expenseCategory, categoryExpensesByMonth.get(monthYearKey).getOrDefault(expenseCategory, 0.0) + expense.getAmount());
        }
      }

      for (Map.Entry<String, Map<String, Double>> entry : categoryExpensesByMonth.entrySet()) {
        String monthYear = entry.getKey();
        Map<String, Double> expensesByCategory = entry.getValue();
        for (Map.Entry<String, Double> categoryExpenseEntry : expensesByCategory.entrySet()) {
          String category = categoryExpenseEntry.getKey();
          double amount = categoryExpenseEntry.getValue();
          System.out.printf("%-11s | %-18s | %.2f%n", monthYear, category, amount);
        }
      }
    } else {
      System.err.println("Неверный формат даты.");
    }
  }

  public void compareExpensesWithPreviousMonth() {
    System.out.println("Сравнение расходов с текущим месяцем:");

    Calendar currentMonthStart = Calendar.getInstance();
    currentMonthStart.set(Calendar.DAY_OF_MONTH, 1);

    Calendar previousMonthStart = Calendar.getInstance();
    previousMonthStart.add(Calendar.MONTH, -1);
    previousMonthStart.set(Calendar.DAY_OF_MONTH, 1);

    double totalExpensesCurrent = getTotalExpensesInMonth(currentMonthStart);
    double totalExpensesPrevious = getTotalExpensesInMonth(previousMonthStart);

    System.out.println("Расходы в текущем месяце: " + totalExpensesCurrent);
    System.out.println("Расходы в предыдущем месяце: " + totalExpensesPrevious);

    double difference = totalExpensesCurrent - totalExpensesPrevious;

    if (totalExpensesPrevious == 0) {
      if (difference > 0) {
        System.out.println("Расходы в текущем месяце больше на: " + difference);
        System.out.println("Изменение в процентах: Нет данных (предыдущие расходы равны нулю)");
      } else if (difference < 0) {
        System.out.println("Расходы в текущем месяце меньше на: " + Math.abs(difference));
        System.out.println("Изменение в процентах: Нет данных (предыдущие расходы равны нулю)");
      } else {
        System.out.println("Расходы в текущем месяце равны расходам в предыдущем месяце.");
        System.out.println("Изменение в процентах: Нет данных (предыдущие расходы равны нулю)");
      }
    } else {
      double percentageChange = (difference / Math.abs(totalExpensesPrevious)) * 100;

      if (difference > 0) {
        System.out.println("Расходы в текущем месяце больше на: " + difference);
        System.out.println("Изменение в процентах: " + percentageChange + "%");
      } else if (difference < 0) {
        System.out.println("Расходы в текущем месяце меньше на: " + Math.abs(difference));
        System.out.println("Изменение в процентах: " + Math.abs(percentageChange) + "%");
      } else {
        System.out.println("Расходы в текущем месяце равны расходам в предыдущем месяце.");
        System.out.println("Изменение в процентах: 0%");
      }
    }
  }




  public void compareExpensesByYear() {
    System.out.println("Сравнение расходов с текущим годом:");

    Calendar currentYearStart = Calendar.getInstance();
    currentYearStart.set(Calendar.DAY_OF_YEAR, 1);

    Calendar previousYearStart = Calendar.getInstance();
    previousYearStart.add(Calendar.YEAR, -1);
    previousYearStart.set(Calendar.DAY_OF_YEAR, 1);

    int currentYear = currentYearStart.get(Calendar.YEAR);  // Get the current year
    int previousYear = currentYear - 1;  // Calculate the previous year

    double totalExpensesThisYear = getTotalExpensesForYear(currentYear);
    double totalExpensesPreviousYear = getTotalExpensesForYear(previousYear);

    System.out.println("Расходы в текущем году: " + totalExpensesThisYear);
    System.out.println("Расходы в предыдущем году: " + totalExpensesPreviousYear);

    double difference = totalExpensesThisYear - totalExpensesPreviousYear;

    if (totalExpensesPreviousYear != 0) {
      double percentageChange = (difference / totalExpensesPreviousYear) * 100;

      if (difference > 0) {
        System.out.println("Расходы в текущем году больше на: " + difference);
        System.out.println("Изменение в процентах: " + percentageChange + "%");
      } else if (difference < 0) {
        System.out.println("Расходы в текущем году меньше на: " + Math.abs(difference));
        System.out.println("Изменение в процентах: " + Math.abs(percentageChange) + "%");
      } else {
        System.out.println("Расходы в текущем году равны расходам в предыдущем году.");
      }
    } else {
      System.out.println("Нет данных о расходах в предыдущем году.");
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
