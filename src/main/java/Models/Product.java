package Models;

public class Product {
    private int productId;
    private String name;
    private String description;
    private double price;
    private int stockId;
    private String imageUrl;

    public Product(int productId, String name, String description, double price, int stockId, String imageUrl) {
        this.productId = productId;
        this.name = name;
        this.description = description;
        this.price = price;
        this.stockId = stockId;
        this.imageUrl = imageUrl;

        System.out.println("📢 Création du produit : " + name + " | Prix : " + this.price);
        if (this.name == null || this.price == 0.0) {
            System.err.println("⚠ ERREUR : `name` est null ou `price` est incorrect !");
        }
    }

    public Product(int productId, String name, double price, String defaultDescription, String defaultImage) {
        this(productId, name, defaultDescription, price, 0, defaultImage);
    }

    public int getProductId() {
        return productId;
    }

    public void setProductId(int productId) {
        this.productId = productId;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getDescription() {
        return description;
    }

    public void setDescription(String description) {
        this.description = description;
    }

    public double getPrice() {
        return price;
    }

    public void setPrice(double price) {
        this.price = price;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public String getImageUrl() {
        return imageUrl;
    }

    public void setImageUrl(String imageUrl) {
        this.imageUrl = imageUrl;
    }
    @Override
    public String toString() {
        return "Product{" +
                "productId=" + productId +
                ", name='" + name + '\'' +
                ", description='" + description + '\'' +
                ", price=" + price +
                ", stockId=" + stockId +
                ", imageUrl='" + imageUrl + '\'' +
                '}';
    }
}
