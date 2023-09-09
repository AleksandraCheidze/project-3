import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;


class ExpenseCategoryManagerTest {

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  /**
   * Adds a new expense category.
   */
  private ExpenseCategoryManager categoryManager;

  @BeforeEach
  void setUp() {
    categoryManager = new ExpenseCategoryManager();
    System.setOut(new PrintStream(outContent));
    System.setErr(new PrintStream(errContent));
  }

  @Test
  void addCategory_Successful() {
    String category = "TestCategory";

    categoryManager.addCategory(category);

    List<String> categories = categoryManager.getCategories();
    assertTrue(categories.contains(category));
  }

  @Test
  void addCategory_Duplicate() {
    String category = "TestCategory";
    categoryManager.addCategory(category);

    // Attempt to add the same category again
    categoryManager.addCategory(category);

    List<String> categories = categoryManager.getCategories();
    assertEquals(1, categories.stream().filter(c -> c.equals(category)).count());
  }

  @Test
  void addCategory_InvalidName() {
    // Attempt to add an empty category name
    categoryManager.addCategory("");
    List<String> categories = categoryManager.getCategories();
    assertFalse(categories.contains(""));

    // Attempt to add a category with whitespace-only name
    categoryManager.addCategory("    ");
    assertFalse(categories.contains("    "));
  }

  @Test
  void addCategory_NullCategory() {
    // Attempt to add a null category
    categoryManager.addCategory(null);
    List<String> categories = categoryManager.getCategories();
    assertFalse(categories.contains(null));
  }

  /**
   * Removes an expense category.
   */
  @Test
  void removeCategory_Successful() {
    String category = "TestCategory";
    categoryManager.addCategory(category);

    categoryManager.removeCategory(category);

    assertFalse(categoryManager.getCategories().contains(category));
  }

  @Test
  void removeCategory_NotFound() {
    String categoryToRemove = "NonExistentCategory";

    categoryManager.removeCategory(categoryToRemove);

    // Ensure that the category is still not found in the list
    assertFalse(categoryManager.getCategories().contains(categoryToRemove));
  }

  @Test
  void removeCategory_InvalidName() {
    // Attempt to remove an empty category name
    categoryManager.removeCategory("");
    assertFalse(categoryManager.getCategories().contains(""));

    // Attempt to remove a category with whitespace-only name
    categoryManager.removeCategory("    ");
    assertFalse(categoryManager.getCategories().contains("    "));
  }

  @Test
  void removeCategory_NullCategory() {
    // Attempt to remove a null category
    categoryManager.removeCategory(null);
    assertFalse(categoryManager.getCategories().contains(null));
  }

  /**
   * Manages categories interactively, allowing for adding and removing categories.
   */

  @Test
  void getCategories_AfterAddingCategories() {
    String category1 = "TestCategory1";
    String category2 = "TestCategory2";

    // Добавляем категории
    categoryManager.addCategory(category1);
    categoryManager.addCategory(category2);

    // Проверяем, что список категорий содержит добавленные категории
    assertTrue(categoryManager.getCategories().contains(category1));
    assertTrue(categoryManager.getCategories().contains(category2));
  }

  @Test
  void getCategories_AfterRemovingCategories() {
    String category1 = "TestCategory1";
    String category2 = "TestCategory2";

    // Добавляем категории
    categoryManager.addCategory(category1);
    categoryManager.addCategory(category2);

    // Удаляем категорию category1
    categoryManager.removeCategory(category1);

    // Проверяем, что список категорий больше не содержит удаленную категорию
    assertFalse(categoryManager.getCategories().contains(category1));
    assertTrue(categoryManager.getCategories().contains(category2));
  }

  /**
   * Manages categories interactively, allowing for adding and removing categories.
   */
  @Test
  void manageCategories_AddAndRemoveCategories() {
    // Создаем виртуальный ввод для теста
    String input = "1\nTestCategory1\n1\nTestCategory2\n2\nTestCategory1\n3\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    // Создаем сканнер для виртуального ввода
    Scanner scanner = new Scanner(System.in);

    // Запускаем метод управления категориями
    categoryManager.manageCategories(scanner);

    // Получаем список категорий после взаимодействия
    List<String> categories = categoryManager.getCategories();

    // Проверяем, что категории успешно добавлены и удалены
    assertEquals(1, categories.stream().filter(c -> c.equals("TestCategory2")).count());
  }

  @Test
  void manageCategories_InvalidChoice() {
    // Создаем виртуальный ввод для теста
    String input = "5\n3\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    // Создаем сканнер для виртуального ввода
    Scanner scanner = new Scanner(System.in);

    // Запускаем метод управления категориями
    categoryManager.manageCategories(scanner);

    // Получаем текст из потока ошибок (System.err)
    String errorOutput = errContent.toString().trim();

    // Проверяем, что метод выводит сообщение об ошибке
    assertEquals("Неверный выбор", errorOutput);
  }

  /**
   * Сheck method updateCategories()
   */
  @Test
  void updateCategories_FileError() {
    // Simulate a file error by making the file read-only
    File readOnlyFile = new File("res/categories.txt");
    assertTrue(readOnlyFile.setReadOnly());

    // Call the public updateCategories method
    categoryManager.updateCategories();

    // Verify that an error message was printed to System.err
    String errorOutput = errContent.toString().trim();
    assertTrue(errorOutput.contains("Ошибка при записи категорий в файл"));

    // Clean up: make the file writable again
    assertTrue(readOnlyFile.setWritable(true));
  }
}
