// Stock.java (Model)
package Models;

import java.sql.Timestamp;

public class Stock {
    private int stockId;
    private int availableQuantity;
    private int minimumQuantity;
    private Timestamp lastUpdate;
    private int idUser;

    public Stock() {}

    public Stock(int stockId, int availableQuantity, int minimumQuantity, Timestamp lastUpdate, int idUser) {
        this.stockId = stockId;
        this.availableQuantity = availableQuantity;
        this.minimumQuantity = minimumQuantity;
        this.lastUpdate = lastUpdate;
        this.idUser = idUser;
    }

    public Stock(int availableQuantity, int minimumQuantity, int idUser) {
        this.availableQuantity = availableQuantity;
        this.minimumQuantity = minimumQuantity;
        this.idUser = idUser;
    }

    public int getStockId() {
        return stockId;
    }

    public void setStockId(int stockId) {
        this.stockId = stockId;
    }

    public int getAvailableQuantity() {
        return availableQuantity;
    }

    public void setAvailableQuantity(int availableQuantity) {
        this.availableQuantity = availableQuantity;
    }

    public int getMinimumQuantity() {
        return minimumQuantity;
    }

    public void setMinimumQuantity(int minimumQuantity) {
        this.minimumQuantity = minimumQuantity;
    }

    public Timestamp getLastUpdate() {
        return lastUpdate;
    }

    public void setLastUpdate(Timestamp lastUpdate) {
        this.lastUpdate = lastUpdate;
    }

    public int getIdUser() {
        return idUser;
    }

    public void setIdUser(int idUser) {
        this.idUser = idUser;
    }

    @Override
    public String toString() {
        return "Stock{" +
                "stockId=" + stockId +
                ", availableQuantity=" + availableQuantity +
                ", minimumQuantity=" + minimumQuantity +
                ", lastUpdate=" + lastUpdate +
                ", idUser=" + idUser +
                '}';
    }
}
