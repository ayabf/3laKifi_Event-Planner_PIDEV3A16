package Models;

public class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        this.product = product;
        this.quantity = quantity;
        System.out.println("📢 Création du CartItem : " + product.getName() + " | Prix : " + product.getPrice() + " | Quantité : " + quantity);
    }

    public Product getProduct() { return product; }
    public int getQuantity() { return quantity; }
    public void setQuantity(int quantity) { this.quantity = quantity; }

    public double getTotalPrice() {
        double total = quantity * product.getPrice();
        System.out.println("📢 Vérification : " + product.getName() + " x " + quantity + " = " + total);
        return total;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "product=" + product +
                ", quantity=" + quantity +
                ", totalPrice=" + getTotalPrice() +
                '}';
    }
}
