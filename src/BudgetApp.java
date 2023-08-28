import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.Scanner;

public class BudgetApp {
  private static final String FILE_PATH = "tasks.txt";
  private final Scanner scanner;
  private FileWriter writer;
  private final ArrayList<Expense> expenses = new ArrayList<>();
  private final String[] expenseCategories = {"Еда", "Транспорт", "Развлечения", "Прочее"};


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
    System.out.println("2. Посмотреть отчет расходов");
    System.out.println("3. Посмотреть расходы по категориям");
    System.out.println("4. Выйти");
  }

  public void start() {
    while (true) {
      printMenu();
      int choice ;
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

  static int getUserChoice(Scanner scanner) {
    return scanner.nextInt();
  }

  private void addExpenses() {
    System.out.println("Выберите категорию расхода:");
    for (int i = 0; i < expenseCategories.length; i++) {
      System.out.println((i + 1) + ". " + expenseCategories[i]);
    }
    int categoryChoice = scanner.nextInt();
    scanner.nextLine();
    if (categoryChoice >= 1 && categoryChoice <= expenseCategories.length) {
      String category = expenseCategories[categoryChoice - 1];

      System.out.println("Введите сумму расхода:");
      double amount = scanner.nextDouble();
      scanner.nextLine();

      System.out.println("Введите дату расхода:");
      String date = scanner.nextLine();

      Expense newExpense = new Expense(category, amount, date);
      expenses.add(newExpense);

      try (FileWriter writer = new FileWriter(FILE_PATH, true)) {
        writer.write(category + " " + amount + " " + date + "\n");
      } catch (IOException e) {
        System.err.println("Ошибка при записи в файл: " + e.getMessage());
      }
    } else {
      System.err.println("Неверный выбор категории.");
    }
  }


  private void viewConsumption() {
    // Реализация просмотра отчета о расходах
  }

  private void expenditureCategories() {
    // Реализация просмотра расходов по категориям
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

