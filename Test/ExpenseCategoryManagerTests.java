import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import java.io.*;
import java.util.List;
import java.util.Scanner;

import static org.junit.jupiter.api.Assertions.*;

class ExpenseCategoryManagerTest {

  private final ByteArrayOutputStream outContent = new ByteArrayOutputStream();
  private final ByteArrayOutputStream errContent = new ByteArrayOutputStream();
  private ExpenseCategoryManager categoryManager;

  @BeforeEach
  void setUp() throws IOException {
    // Создаем временный файл в текущей директории с нужным именем
    File tempFile = new File("res/categories_test.txt");

    if (!tempFile.getParentFile().exists()) {
      tempFile.getParentFile().mkdirs(); // Create a directory if it doesn't exist
    }

    // Создаем новый файл, если он уже существует, он будет перезаписан
    tempFile.createNewFile();

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

    assertFalse(categoryManager.getCategories().contains(categoryToRemove));
  }

  @Test
  void removeCategory_InvalidName() {
    categoryManager.removeCategory("");
    assertFalse(categoryManager.getCategories().contains(""));

    categoryManager.removeCategory("    ");
    assertFalse(categoryManager.getCategories().contains("    "));
  }

  @Test
  void removeCategory_NullCategory() {
    categoryManager.removeCategory(null);
    assertFalse(categoryManager.getCategories().contains(null));
  }

  @Test
  void getCategories_AfterAddingCategories() {
    String category1 = "TestCategory1";
    String category2 = "TestCategory2";

    categoryManager.addCategory(category1);
    categoryManager.addCategory(category2);

    assertTrue(categoryManager.getCategories().contains(category1));
    assertTrue(categoryManager.getCategories().contains(category2));
  }

  @Test
  void getCategories_AfterRemovingCategories() {
    String category1 = "TestCategory1";
    String category2 = "TestCategory2";

    categoryManager.addCategory(category1);
    categoryManager.addCategory(category2);

    categoryManager.removeCategory(category1);

    assertFalse(categoryManager.getCategories().contains(category1));
    assertTrue(categoryManager.getCategories().contains(category2));
  }

  @Test
  void manageCategories_AddAndRemoveCategories() {
    String input = "1\nTestCategory1\n1\nTestCategory2\n2\nTestCategory1\n3\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    Scanner scanner = new Scanner(System.in);

    categoryManager.manageCategories(scanner);

    List<String> categories = categoryManager.getCategories();

    assertEquals(1, categories.stream().filter(c -> c.equals("TestCategory2")).count());
  }

  @Test
  void manageCategories_InvalidChoice() {
    String input = "5\n3\n";
    InputStream in = new ByteArrayInputStream(input.getBytes());
    System.setIn(in);

    Scanner scanner = new Scanner(System.in);

    categoryManager.manageCategories(scanner);

    String errorOutput = errContent.toString().trim();

    assertEquals("Неверный выбор", errorOutput);
  }

  @Test
  void updateCategories_FileError() {
    File readOnlyFile = new File("res/categories.txt");
    assertTrue(readOnlyFile.setReadOnly());

    categoryManager.updateCategories();

    String errorOutput = errContent.toString().trim();
    assertTrue(errorOutput.contains("Ошибка при записи категорий в файл"));

    assertTrue(readOnlyFile.setWritable(true));
  }
}
