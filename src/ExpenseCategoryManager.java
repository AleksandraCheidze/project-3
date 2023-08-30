import java.util.ArrayList;
import java.util.List;

public class ExpenseCategoryManager {
  private List<String> categories;

  public ExpenseCategoryManager() {
    categories = new ArrayList<>();
    // Добавим стандартные категории по умолчанию
    categories.add("Еда");
    categories.add("Транспорт");
    categories.add("Развлечения");
    categories.add("Прочее");
  }

  public boolean addCategory(String category) {
    if (category != null && !category.isEmpty()) {
      if (categories.contains(category)) {
        System.out.println("Категория уже существует: " + category);
        return false;
      } else {
        categories.add(category);
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
}
