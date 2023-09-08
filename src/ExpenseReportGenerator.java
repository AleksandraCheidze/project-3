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

  /**
   * Generates an expense report based on user-selected category and date range.
   */
  public void viewExpensesByCategoryAndPeriod() {
    System.out.println("Отчет о расходах по категориям и периоду:");
    System.out.println("Выберите категорию или введите 0 для выбора всех категорий:");
    List<String> categories = getDistinctCategories();
    for (int i = 0; i < categories.size(); i++) {
      System.out.println((i + 1) + ". " + categories.get(i));
    }
    int categoryChoice = getUserChoice(scanner, categories.size() - 1); // Allow 0 for all categories

    System.out.println("Введите начальную дату (в формате dd.MM.yyyy):");
    String startDateInput = scanner.next();
    System.out.println("Введите конечную дату (в формате dd.MM.yyyy):");
    String endDateInput = scanner.next();

    Date startDate = parseDate(startDateInput);
    Date endDate = parseDate(endDateInput);

    if (startDate != null && endDate != null) {
      String selectedCategory = categoryChoice == 0 ? "Все категории" : categories.get(categoryChoice - 1);
      Map<String, List<Expense>> categoryExpensesMap = new HashMap<>();

      for (Expense expense : expenses) {
        Date expenseDate = parseDate(expense.getDate());
        String expenseCategory = expense.getCategory();
        if (expenseDate != null && (selectedCategory.equals("Все категории") || expenseCategory.equals(selectedCategory))
            && expenseDate.compareTo(startDate) >= 0 && expenseDate.compareTo(endDate) <= 0) {
          categoryExpensesMap.computeIfAbsent(expenseCategory, k -> new ArrayList<>());
          categoryExpensesMap.get(expenseCategory).add(expense);
        }
      }

      System.out.println("======================================");
      System.out.println("Категория: " + selectedCategory);
      System.out.println("Период: с " + startDateInput + " по " + endDateInput);

      Comparator<Expense> expenseDateComparator = (e1, e2) -> {
        Date date1 = parseDate(e1.getDate());
        Date date2 = parseDate(e2.getDate());
        if (date1 != null && date2 != null) {
          return date1.compareTo(date2);
        } else {
          return 0;
        }
      };
      for (Map.Entry<String, List<Expense>> entry : categoryExpensesMap.entrySet()) {
        String category = entry.getKey();
        List<Expense> categoryExpenses = entry.getValue();
        categoryExpenses.sort(expenseDateComparator);
        for (Expense expense : categoryExpenses) {
          double amount = expense.getAmount();
          System.out.printf("%-11s | %-18s | %.1f%n", expense.getDate(), category, amount);
        }
      }
    } else {
      System.err.println("Неверный формат даты.");
    }
  }

  /**
   * Compares expenses of the current month with the previous month.
   */
  public void compareExpensesWithPreviousMonth() {
    System.out.println("Сравнение расходов с текущим месяцем:");

    Calendar currentMonthStart = Calendar.getInstance();
    currentMonthStart.set(Calendar.DAY_OF_MONTH, 1);

    Calendar previousMonthStart = Calendar.getInstance();
    previousMonthStart.add(Calendar.MONTH, -1);
    previousMonthStart.set(Calendar.DAY_OF_MONTH, 1);

    double totalExpensesCurrent = getTotalExpensesInMonth(currentMonthStart);
    double totalExpensesPrevious = getTotalExpensesInMonth(previousMonthStart);

    System.out.println("Расходы в текущем месяце: " + String.format("%.1f", totalExpensesCurrent));
    System.out.println("Расходы в предыдущем месяце: " + String.format("%.1f", totalExpensesPrevious));

    double difference = totalExpensesCurrent - totalExpensesPrevious;

    if (totalExpensesPrevious == 0) {
      if (difference > 0) {
        System.out.println("Расходы в текущем месяце больше на: " + String.format("%.1f", difference));
        System.out.println("Изменение в процентах: Нет данных (предыдущие расходы равны нулю)");
      } else if (difference < 0) {
        System.out.println("Расходы в текущем месяце меньше на: " + String.format("%.1f", Math.abs(difference)));
        System.out.println("Изменение в процентах: Нет данных (предыдущие расходы равны нулю)");
      } else {
        System.out.println("Расходы в текущем месяце равны расходам в предыдущем месяце.");
        System.out.println("Изменение в процентах: Нет данных (предыдущие расходы равны нулю)");
      }
    } else {
      double percentageChange = (difference / Math.abs(totalExpensesPrevious)) * 100;

      if (difference > 0) {
        System.out.println("Расходы в текущем месяце больше на: " + String.format("%.1f", difference));
        System.out.println("Изменение в процентах: " + String.format("%.1f", percentageChange) + "%");
      } else if (difference < 0) {
        System.out.println("Расходы в текущем месяце меньше на: " + String.format("%.1f", Math.abs(difference)));
        System.out.println("Изменение в процентах: " + String.format("%.1f", Math.abs(percentageChange)) + "%");
      } else {
        System.out.println("Расходы в текущем месяце равны расходам в предыдущем месяце.");
        System.out.println("Изменение в процентах: 0%");
      }
    }
  }

  /**
   * Compares expenses of the current year with the previous year.
   */
  public void compareExpensesByYear() {
    System.out.println("Сравнение расходов с текущим годом:");

    Calendar currentYearStart = Calendar.getInstance();
    currentYearStart.set(Calendar.DAY_OF_YEAR, 1);

    Calendar previousYearStart = Calendar.getInstance();
    previousYearStart.add(Calendar.YEAR, -1);
    previousYearStart.set(Calendar.DAY_OF_YEAR, 1);

    int currentYear = currentYearStart.get(Calendar.YEAR);
    int previousYear = currentYear - 1;

    double totalExpensesThisYear = getTotalExpensesForYear(currentYear);
    double totalExpensesPreviousYear = getTotalExpensesForYear(previousYear);

    System.out.println("Расходы в текущем году: " + String.format("%.1f", totalExpensesThisYear));
    System.out.println("Расходы в предыдущем году: " + String.format("%.1f", totalExpensesPreviousYear));

    double difference = totalExpensesThisYear - totalExpensesPreviousYear;

    if (totalExpensesPreviousYear != 0) {
      double percentageChange = (difference / totalExpensesPreviousYear) * 100;

      if (difference > 0) {
        System.out.println("Расходы в текущем году больше на: " + String.format("%.1f", difference));
        System.out.println("Изменение в процентах: " + String.format("%.1f", percentageChange) + "%");
      } else if (difference < 0) {
        System.out.println("Расходы в текущем году меньше на: " + String.format("%.1f", Math.abs(difference)));
        System.out.println("Изменение в процентах: " + String.format("%.1f", Math.abs(percentageChange)) + "%");
      } else {
        System.out.println("Расходы в текущем году равны расходам в предыдущем году.");
      }
    } else {
      System.out.println("Нет данных о расходах в предыдущем году.");
    }
  }

  /**
   * Calculates the total expenses for a specific year.
   *
   * @param year The year for which expenses are calculated.
   * @return The total expenses for the specified year.
   */
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

  /**
   * Calculates the total expenses for a specific month.
   *
   * @param monthStart A Calendar instance representing the start of the month.
   * @return The total expenses for the specified month.
   */
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

  /**
   * Retrieves a list of distinct expense categories from the expenses list.
   *
   * @return List of distinct expense categories.
   */
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

  /**
   * Parses a date string into a Date object.
   *
   * @param dateStr The date string to be parsed.
   * @return A Date object representing the parsed date, or null if parsing fails.
   */
  private Date parseDate(String dateStr) {
    try {
      return dateFormat.parse(dateStr);
    } catch (ParseException e) {
      System.err.println("Ошибка при разборе даты: " + e.getMessage());
      return null;
    }
  }

  /**
   * Prompts the user for a choice and ensures it falls within a specified range.
   *
   * @param scanner A Scanner object for user input.
   * @param max     The maximum allowed choice value.
   * @return The user's valid choice.
   */
  private int getUserChoice(Scanner scanner, int max) {
    while (true) {
      try {
        int choice = scanner.nextInt();
        if (choice >= 0 && choice <= max) {
          return choice;
        } else {
          System.err.println("Пожалуйста, введите число в диапазоне от " + 0 + " до " + max + ".");
        }
      } catch (InputMismatchException e) {
        System.err.println("Пожалуйста, введите число.");
        scanner.next();
      }
    }
  }
}
