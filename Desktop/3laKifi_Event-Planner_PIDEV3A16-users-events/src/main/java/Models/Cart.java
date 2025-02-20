package Models;

import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.List;

public class Cart {
    private int cartId;
    private User user;
    private LocalDateTime createdAt;
    private double totalPrice;
    private List<CartItem> items;


    public Cart(int cartId, User user, LocalDateTime createdAt, double totalPrice) {
        this.cartId = cartId;
        this.user = user;
        this.createdAt = createdAt;
        this.totalPrice = totalPrice;
        this.items = new ArrayList<>();
    }

    public int getCartId() { return cartId; }

    public void setCartId(int cartId) {
        this.cartId = cartId;
    }

    public void setTotalPrice(double totalPrice) {
        this.totalPrice = totalPrice;
    }

    public User getUser() { return user; }
    public LocalDateTime getCreatedAt() { return createdAt; }
    public double getTotalPrice() { return totalPrice; }

    public List<CartItem> getItems() { return items; }
    public void addItem(CartItem item) {
        this.items.add(item);
        updateTotalPrice();
    }
    public void removeItem(CartItem item) {
        this.items.remove(item);
        updateTotalPrice();
    }

    public void updateTotalPrice() {
        this.totalPrice = items.stream().mapToDouble(CartItem::getTotalPrice).sum();
    }

    @Override
    public String toString() {
        return "Cart{" +
                "cartId=" + cartId +
                ", user=" + user +
                ", createdAt=" + createdAt +
                ", totalPrice=" + totalPrice +
                ", items=" + items +
                '}';
    }
}
