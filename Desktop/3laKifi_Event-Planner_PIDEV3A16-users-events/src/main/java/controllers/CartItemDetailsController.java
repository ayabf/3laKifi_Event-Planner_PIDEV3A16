package controllers;

import Models.CartItem;
import javafx.fxml.FXML;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;

public class CartItemDetailsController {

    @FXML private Label productName;
    @FXML private Label productDescription;
    @FXML private Label productPrice;
    @FXML private Label productQuantity;
    @FXML private Label totalItemPrice;
    @FXML private ImageView productImage;

    public void setCartItemDetails(CartItem cartItem) {
        productName.setText(cartItem.getProduct().getName());
        productDescription.setText(cartItem.getProduct().getDescription()); // Ajout de la description
        productPrice.setText("$" + cartItem.getProduct().getPrice());
        productQuantity.setText(cartItem.getQuantity() + " unité(s)");
        totalItemPrice.setText("$" + (cartItem.getProduct().getPrice() * cartItem.getQuantity()));

        try {
            if (cartItem.getProduct().getImageUrl() != null && !cartItem.getProduct().getImageUrl().isEmpty()) {
                productImage.setImage(new Image(cartItem.getProduct().getImageUrl()));
            } else {
                productImage.setImage(new Image("file:images/default.png"));
            }
        } catch (Exception e) {
            productImage.setImage(new Image("file:images/default.png"));
        }
    }

    @FXML
    private void handleClose() {
        Stage stage = (Stage) productName.getScene().getWindow();
        stage.close();
    }
}
