import java.util.ArrayList;
import java.util.Scanner;

public class BudgetApp {
  private Scanner scanner;

  public BudgetApp() {
    scanner = new Scanner(System.in);
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
      int choice = getUserChoice();

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
          exit();
          return;
        default:
          System.err.println("Неверный выбор");
      }
    }
  }

  private int getUserChoice() {
    return scanner.nextInt();
  }

  private void addExpenses() {
    // Реализация добавления расходов
  }

  private void viewConsumption() {
    // Реализация просмотра отчета о расходах
  }

  private void expenditureCategories() {
    // Реализация просмотра расходов по категориям
  }

  private void exit() {
    System.out.println("Выход");
    scanner.close();
  }
}






