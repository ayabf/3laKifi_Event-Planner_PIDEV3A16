package Models;

public class Product {
    private int productId;
    private String reference;  // Unique reference
    private String name;
    private String description;
    private float price;
    private int stockId;
    private String imageUrl;
    private String category;
    private int idUser;

    public Product() {}

    public Product(int productId, String reference, String name, String description, float price, int stockId, String imageUrl, String category, int idUser) {
        this.productId = productId;
        this.reference = reference;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockId = stockId;
        this.imageUrl = imageUrl;
        this.category = category;
        this.idUser = idUser;
    }

    public Product(String reference, String name, String description, float price, int stockId, String imageUrl, String category, int idUser) {
        this.reference = reference;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockId = stockId;
        this.imageUrl = imageUrl;
        this.category = category;
        this.idUser = idUser;
    }

    public Product(int productId, String name, String s, double price, int i, String imageUrl) {
        productId = productId;
        name = name;
        price = price;
        stockId = i;
        imageUrl = imageUrl;
    }

    public int getProductId() { return productId; }
    public void setProductId(int productId) { this.productId = productId; }

    public String getReference() { return reference; }
    public void setReference(String reference) { this.reference = reference; }

    public String getName() { return name; }
    public void setName(String name) { this.name = name; }

    public String getDescription() { return description; }
    public void setDescription(String description) { this.description = description; }

    public float getPrice() { return price; }
    public void setPrice(float price) { this.price = price; }

    public int getStockId() { return stockId; }
    public void setStockId(int stockId) { this.stockId = stockId; }

    public String getImageUrl() { return imageUrl; }
    public void setImageUrl(String imageUrl) { this.imageUrl = imageUrl; }

    public String getCategory() { return category; }
    public void setCategory(String category) { this.category = category; }

    public int getIdUser() { return idUser; }
    public void setIdUser(int idUser) { this.idUser = idUser; }

    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", reference='" + reference + '\'' +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", stockId=" + stockId +
                ", imageUrl='" + imageUrl + '\'' +
                ", category='" + category + '\'' +
                ", idUser=" + idUser +
                '}';
    }
}
