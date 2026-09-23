public class Item {
    private int itemId;
    private String title;
    private String description;
    private String category;
    private double price;
    private String condition;

    public Item(int itemId, String title, String description, String category, double price, String condition) {
        this.itemId = itemId;
        this.title = title;
        this.description = description;
        this.category = category;
        this.price = price;
        this.condition = condition;
    }

    public int getItemId() { return itemId; }
    public String getTitle() { return title; }
    public String getDescription() { return description; }
    public String getCategory() { return category; }
    public double getPrice() { return price; }
    public String getCondition() { return condition; }
}
