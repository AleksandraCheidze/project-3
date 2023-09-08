import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ExpenseCategoryManager {

  private static final String CATEGORIES_FILE_PATH = "res/categories.txt";
  private final List<String> categories;

  public ExpenseCategoryManager() {
    categories = new ArrayList<>();
    loadCategoriesFromFile();
  }

  /**
   * Adds a new expense category.
   *
   * @param category The name of the category to add.
   */
  public void addCategory(String category) {
    if (category != null && !category.trim().isEmpty()) {
      if (!categories.contains(category)) {
        categories.add(category);
        updateCategories();
        System.out.println("Категория успешно добавлена: " + category);
      } else {
        System.out.println("Категория уже существует: " + category);
      }
    } else {
      System.out.println("Недопустимое название категории.");
    }
  }

  /**
   * Removes an expense category.
   *
   * @param category The name of the category to remove.
   */
  public void removeCategory(String category) {
    if (category != null && !category.trim().isEmpty()) {
      if (categories.remove(category)) {
        updateCategories();
        System.out.println("Категория успешно удалена: " + category);
      } else {
        System.out.println("Категория не найдена: " + category);
      }
    } else {
      System.out.println("Недопустимое название категории.");
    }
  }
  public List<String> getCategories() {
    return categories;
  }

  /**
   * Manages categories interactively, allowing for adding and removing categories.
   *
   * @param scanner A Scanner object for user input.
   */
  public void manageCategories(Scanner scanner) {
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
        case 1 -> addCategoryFromInput(scanner);
        case 2 -> removeCategoryFromInput(scanner);
        case 3 -> {
          return;
        }
        default -> System.err.println("Неверный выбор");
      }
    }
  }

  /**
   * Adds a category based on user input.
   *
   * @param scanner A Scanner object for user input.
   */
  private void addCategoryFromInput(Scanner scanner) {
    System.out.println("Введите название новой категории:");
    scanner.nextLine(); // Очистка буфера
    String newCategory = scanner.nextLine().trim();
    addCategory(newCategory);
  }

  /**
   * Removes a category based on user input.
   *
   * @param scanner A Scanner object for user input.
   */
  private void removeCategoryFromInput(Scanner scanner) {
    System.out.println("Введите название категории для удаления:");
    scanner.nextLine(); // Очистка буфера
    String categoryToRemove = scanner.nextLine().trim();
    removeCategory(categoryToRemove);
  }

  private void updateCategories() {
    try (FileWriter categoriesWriter = new FileWriter(CATEGORIES_FILE_PATH)) {
      for (String category : categories) {
        categoriesWriter.write(category + "\n");
      }
    } catch (IOException e) {
      System.err.println("Ошибка при записи категорий в файл: " + e.getMessage());
    }
  }

  private void loadCategoriesFromFile() {
    try (Scanner categoriesScanner = new Scanner(new File(CATEGORIES_FILE_PATH))) {
      while (categoriesScanner.hasNextLine()) {
        String category = categoriesScanner.nextLine().trim();
        if (!category.isEmpty()) {
          categories.add(category);
        }
      }
    } catch (FileNotFoundException e) {
    } catch (Exception e) {
      System.err.println("Ошибка при чтении категорий из файла: " + e.getMessage());
    }
  }
}
