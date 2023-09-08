public class Expense {
  private final String category;
  private final double amount;
  private final String date;

  /**
   * Constructs an Expense object with the given category, amount, and date.
   *
   * @param category The category of the expense.
   * @param amount   The amount of the expense.
   * @param date     The date of the expense in the format "dd.MM.yyyy".
   */
  public Expense(String category, double amount, String date) {
    this.category = category;
    this.amount = amount;
    this.date = date;
  }

  /**
   * Gets the category of the expense.
   *
   * @return The category of the expense.
   */
  public String getCategory() {
    return category;
  }

  /**
   * Gets the amount of the expense.
   *
   * @return The amount of the expense.
   */
  public double getAmount() {
    return amount;
  }

  /**
   * Gets the date of the expense in the format "dd.MM.yyyy".
   *
   * @return The date of the expense.
   */
  public String getDate() {
    return date;
  }
}
