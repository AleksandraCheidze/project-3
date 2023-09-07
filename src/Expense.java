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
}
