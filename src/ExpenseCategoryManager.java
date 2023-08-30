import java.io.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class ExpenseCategoryManager {

  private static final String CATEGORIES_FILE_PATH = "categories.txt";
  private final List<String> categories;

  public ExpenseCategoryManager() {
    categories = new ArrayList<>();
    loadCategoriesFromFile();
  }

  public boolean addCategory(String category) {
    if (category != null && !category.isEmpty()) {
      if (categories.contains(category)) {
        System.out.println("Категория уже существует: " + category);
        return false;
      } else {
        categories.add(category);
        updateCategories();
        System.out.println("Категория добавлена: " + category);
        return true;
      }
    } else {
      System.out.println("Неверное название категории.");
      return false;
    }
  }

  public boolean removeCategory(String category) {
    if (categories.remove(category)) {
      updateCategories();
      System.out.println("Категория удалена: " + category);
      return true;
    } else {
      System.out.println("Категория не найдена: " + category);
      return false;
    }
  }

  public List<String> getCategories() {
    return categories;
  }

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
        case 1:
          addCategoryFromInput(scanner);
          break;
        case 2:
          removeCategoryFromInput(scanner);
          break;
        case 3:
          return;
        default:
          System.err.println("Неверный выбор");
      }
    }
  }

  private void addCategoryFromInput(Scanner scanner) {
    System.out.println("Введите название новой категории:");
    scanner.nextLine(); // Очистка буфера
    String newCategory = scanner.nextLine().trim();
    if (!newCategory.isEmpty()) {
      addCategory(newCategory);
    } else {
      System.err.println("Название категории не может быть пустым.");
    }
  }

  private void removeCategoryFromInput(Scanner scanner) {
    System.out.println("Введите название категории для удаления:");
    scanner.nextLine(); // Очистка буфера
    String categoryToRemove = scanner.nextLine().trim();
    if (!categoryToRemove.isEmpty()) {
      removeCategory(categoryToRemove);
    } else {
      System.err.println("Название категории не может быть пустым.");
    }
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
      // Файл не найден, это нормально, он может быть пустым или отсутствовать
    } catch (Exception e) {
      System.err.println("Ошибка при чтении категорий из файла: " + e.getMessage());
    }
  }
}
