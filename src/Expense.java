import java.io.File;
import java.io.FileWriter;
import java.io.IOException;
import java.util.ArrayList;
import java.util.List;
import java.util.Scanner;

public class Expense {
  private final String category;
  private final double amount;
  private final String date;

  public Expense(String category, double amount, String date) {
    this.category = category;
    this.amount = amount;
    this.date = date;
  }

  public String getCategory() {
    return category;
  }

  public double getAmount() {
    return amount;
  }

  public String getDate() {
    return date;
  }

  /**
   * Loads a list of expenses from the specified file.
   *
   * @param filePath The path to the file from which to load expenses.
   * @return A list of expense objects loaded from the file.
   */
  public static List<Expense> loadExpensesFromFile(String filePath) {
    List<Expense> expenses = new ArrayList<>();
    try (Scanner fileScanner = new Scanner(new File(filePath))) {
      while (fileScanner.hasNextLine()) {
        String line = fileScanner.nextLine();
        String[] parts = line.split(" ");
        if (parts.length >= 3) {
          String category = parts[0];
          double amount = Double.parseDouble(parts[1]);
          String date = parts[2];
          expenses.add(new Expense(category, amount, date));
        }
      }
    } catch (Exception e) {
      System.err.println("Невозможно считать расходы: " + e.getMessage());
    }
    return expenses;
  }

  /**
   * Saves a list of expenses to the specified file.
   *
   * @param expenses The list of expenses to be saved.
   * @param filePath The path to the file where expenses should be saved.
   */
  public static void saveExpensesToFile(List<Expense> expenses, String filePath) {
    try (FileWriter writer = new FileWriter(filePath)) {
      for (Expense expense : expenses) {
        writer.write(
            expense.getCategory() + " " + expense.getAmount() + " " + expense.getDate() + "\n");
      }
      System.out.println("Расходы сохранены.");
    } catch (IOException e) {
      System.err.println("Ошибка при сохранении расходов: " + e.getMessage());
    }
  }
}
