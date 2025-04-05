package Models;


import java.time.LocalDateTime;

public class Order {
    private int orderId;
    private int cartId;
    private int userId;
    private String status;
    private String paymentMethod;
    private String exactAddress;
    private LocalDateTime eventDate;
    private LocalDateTime orderedAt;
    private double totalPrice;

    // ✅ Constructeur utilisé lors de la création d'une commande (sans orderId car généré par MySQL)
    public Order(int cartId, int userId, String status, double totalPrice, LocalDateTime eventDate, String exactAddress, String paymentMethod) {
        this.cartId = cartId;
        this.userId = userId;
        this.status = status;
        this.totalPrice = totalPrice;
        this.eventDate = eventDate;
        this.exactAddress = exactAddress;
        this.paymentMethod = paymentMethod;
        this.orderedAt = LocalDateTime.now(); // 🔥 Date actuelle de la commande
    }

    // ✅ Constructeur utilisé pour récupérer une commande depuis la base
    public Order(int orderId, int cartId, int userId, String status, String paymentMethod, String exactAddress, LocalDateTime eventDate, LocalDateTime orderedAt, double totalPrice) {
        this.orderId = orderId;
        this.cartId = cartId;
        this.userId = userId;
        this.status = status;
        this.paymentMethod = paymentMethod;
        this.exactAddress = exactAddress;
        this.eventDate = eventDate;
        this.orderedAt = orderedAt;
        this.totalPrice = totalPrice;
    }


    // ✅ Getters et Setters
    public int getOrderId() { return orderId; }
    public void setOrderId(int orderId) { this.orderId = orderId; }

    public int getCartId() { return cartId; }
    public void setCartId(int cartId) { this.cartId = cartId; }

    public int getUserId() { return userId; }
    public void setUserId(int userId) { this.userId = userId; }

    public String getStatus() { return status; }
    public void setStatus(String status) { this.status = status; }

    public String getPaymentMethod() { return paymentMethod; }
    public void setPaymentMethod(String paymentMethod) { this.paymentMethod = paymentMethod; }

    public String getExactAddress() { return exactAddress; }
    public void setExactAddress(String exactAddress) { this.exactAddress = exactAddress; }

    public LocalDateTime getEventDate() { return eventDate; }
    public void setEventDate(LocalDateTime eventDate) { this.eventDate = eventDate; }

    public LocalDateTime getOrderedAt() { return orderedAt; }
    public void setOrderedAt(LocalDateTime orderedAt) { this.orderedAt = orderedAt; }

    public double getTotalPrice() { return totalPrice; }
    public void setTotalPrice(double totalPrice) { this.totalPrice = totalPrice; }

    @Override
    public String toString() {
        return "Order{" +
                "orderId=" + orderId +
                ", cartId=" + cartId +
                ", userId=" + userId +
                ", status='" + status + '\'' +
                ", paymentMethod='" + paymentMethod + '\'' +
                ", exactAddress='" + exactAddress + '\'' +
                ", eventDate=" + eventDate +
                ", orderedAt=" + orderedAt +
                ", totalPrice=" + totalPrice +
                '}';
    }


}
