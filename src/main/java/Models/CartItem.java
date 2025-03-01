package Models;

public class CartItem {
    private Product product;
    private int quantity;

    public CartItem(Product product, int quantity) {
        if (product == null) {
            throw new IllegalArgumentException("❌ Erreur : produit null dans CartItem !");
        }
        if (product.getPrice() == 0.0) {
            System.err.println("⚠ Alerte : prix du produit à 0.0 dans CartItem !");
        }

        this.product = product;
        this.quantity = quantity;

        System.out.println("📢 Création du CartItem : " + product.getName() + " | Prix : " + product.getPrice() + " | Quantité : " + quantity);
    }

    public Product getProduct() {
        return product;
    }

    public int getQuantity() {
        return quantity;
    }

    public void setQuantity(int quantity) {
        if (quantity < 1) {
            System.err.println("⚠ Alerte : la quantité ne peut pas être inférieure à 1 !");
            return;
        }
        this.quantity = quantity;
    }

    public double getTotalPrice() {
        double total = quantity * product.getPrice();
        return total;
    }

    @Override
    public String toString() {
        return "CartItem{" +
                "product=" + product.getName() +
                ", quantity=" + quantity +
                ", totalPrice=" + getTotalPrice() +
                '}';
    }
}
