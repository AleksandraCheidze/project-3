import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ExpenseCategoryManager {
  private Scanner scanner;
  private List<String> categories = new ArrayList<>();

  public ExpenseCategoryManager(Scanner scanner) {
    this.scanner = scanner;
  }

  public void manageExpenseCategories() {
    System.out.println("Управление категориями:");
    System.out.println("1. Добавить категорию");
    System.out.println("2. Удалить категорию");
    System.out.println("3. Вернуться в предыдущее меню");

    int categoryChoice;
    while (true) {
      if (scanner.hasNextInt()) {
        categoryChoice = scanner.nextInt();
        if (categoryChoice >= 1 && categoryChoice <= 3) {
          break;  // Верный выбор, выход из цикла
        } else {
          System.err.println("Неверный выбор.");
        }
      } else {
        System.err.println("Пожалуйста, введите число.");
        scanner.next(); // Игнорирование неверного ввода
      }
    }

    switch (categoryChoice) {
      case 1:
        addExpenseCategory();
        break;
      case 2:
        removeExpenseCategory();
        break;
      case 3:
        // Вернуться в предыдущее меню
        break;
      default:
        System.err.println("Неверный выбор");
    }
  }

  private void addExpenseCategory() {
    System.out.println("Введите название новой категории:");
    String newCategory = scanner.next();

    categories.add(newCategory); // Добавить новую категорию
    System.out.println("Категория добавлена успешно!");
  }

  private void removeExpenseCategory() {
    System.out.println("Введите название категории для удаления:");
    String categoryToRemove = scanner.next();

    if (categories.contains(categoryToRemove)) {
      categories.remove(categoryToRemove); // Удалить категорию
      System.out.println("Категория удалена успешно!");
    } else {
      System.err.println("Категория не найдена или не может быть удалена.");
    }
  }

  public List<String> getCategories() {
    return categories;
  }
}