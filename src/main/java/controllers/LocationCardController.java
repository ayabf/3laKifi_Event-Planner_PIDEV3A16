package controllers;

import Models.Location;
import Models.City;
import de.jensd.fx.glyphs.fontawesome.FontAwesomeIconView;
import javafx.event.EventHandler;
import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.Button;
import javafx.scene.control.Label;
import javafx.scene.image.Image;
import javafx.scene.image.ImageView;
import javafx.stage.Stage;
import services.ServiceLocation;
import javafx.event.ActionEvent;
import javafx.scene.layout.HBox;
import javafx.scene.control.Alert;
import javafx.scene.control.ButtonType;
import javafx.scene.control.DialogPane;
import javafx.scene.layout.VBox;
import javafx.scene.layout.GridPane;
import javafx.geometry.Pos;
import javafx.scene.layout.Region;

import java.io.ByteArrayInputStream;
import java.io.IOException;
import java.sql.SQLException;
import java.util.Optional;

public class LocationCardController {
    @FXML
    private HBox statusContainer;
    
    @FXML
    private FontAwesomeIconView statusIconView;
    
    @FXML
    private Label statusText;
    
    @FXML
    private ImageView locationImage;
    
    @FXML
    private Label locationName;
    
    @FXML
    private Label locationVille;
    
    @FXML
    private Label locationCapacity;
    
    @FXML
    private Label locationDimension;
    
    @FXML
    private Label locationPrice;
    
    @FXML
    private Button editButton;
    
    @FXML
    private Button deleteButton;
    
    @FXML
    private Button detailsButton;
    
    @FXML
    private Button tour3DButton;

    private Location location;
    private final ServiceLocation locationService = new ServiceLocation();
    private EventHandler<ActionEvent> onEdit;
    private EventHandler<ActionEvent> onDelete;
    private EventHandler<ActionEvent> onDetails;
    private boolean isAvailable = true;

    @FXML
    public void initialize() {
        System.out.println("LocationCardController initialized");
        System.out.println("Select button FXML onAction points to: " + 
            (detailsButton.getOnAction() != null ? "handleDetails" : "null"));
    }

    public void setLocation(Location location) {
        this.location = location;
        updateCard();
        System.out.println("Location set: " + location.getName());

        tour3DButton.setVisible(location.getHas3DTour());
        tour3DButton.setManaged(location.getHas3DTour());
    }

    public void setAvailabilityStatus(boolean isAvailable) {
        this.isAvailable = isAvailable;
        updateStatus();
    }

    private void updateCard() {
        if (location != null) {
            locationName.setText(location.getName());
            locationVille.setText(location.getVille().name());
            locationCapacity.setText(String.valueOf(location.getCapacity()) + " people");
            locationDimension.setText(location.getDimension());
            locationPrice.setText(String.format("%.2f DT", location.getPrice()));
            updateStatus();

            if (location.getImageFileName() != null && location.getImageData().length > 0) {
                try {
                    Image image = new Image(new ByteArrayInputStream(location.getImageData()));
                    locationImage.setImage(image);
                } catch (Exception e) {
                    System.err.println("Error loading image: " + e.getMessage());
                }
            }
        }
    }

    private void updateStatus() {
        if (isAvailable) {
            statusContainer.getStyleClass().add("available");
            statusIconView.setGlyphName("CHECK_CIRCLE");
            statusText.setText("Available");
        } else {
            statusContainer.getStyleClass().add("unavailable");
            statusIconView.setGlyphName("TIMES_CIRCLE");
            statusText.setText("Reserved");
        }
    }

    public void setOnEdit(EventHandler<ActionEvent> handler) {
        this.onEdit = handler;
        editButton.setOnAction(handler);
    }

    public void setOnDelete(EventHandler<ActionEvent> handler) {
        this.onDelete = handler;
        deleteButton.setOnAction(handler);
    }

    public void setOnDetails(EventHandler<ActionEvent> handler) {
        System.out.println("Setting details handler for location: " + (location != null ? location.getName() : "null"));
        this.onDetails = handler;

        detailsButton.setOnAction(this::handleDetails);
        
        System.out.println("Details handler set successfully for: " + 
            (location != null ? location.getName() : "null"));
    }

    public boolean hasDetailsHandler() {
        return onDetails != null;
    }

    @FXML
    private void handleEdit(ActionEvent event) {
        if (onEdit != null) {
            onEdit.handle(event);
        }
    }

    @FXML
    private void handleDelete(ActionEvent event) {
        if (onDelete != null) {
            onDelete.handle(event);
        }
    }

    @FXML
    private void handleDetails(ActionEvent event) {
        if (location != null) {
            try {
                Alert detailsDialog = new Alert(Alert.AlertType.NONE);
                detailsDialog.setTitle("Location Details");

                DialogPane dialogPane = new DialogPane();
                VBox contentBox = new VBox(15);
                contentBox.setStyle("-fx-padding: 20;");

                HBox headerBox = new HBox(15);
                headerBox.setAlignment(Pos.CENTER_LEFT);
                
                Label headerLabel = new Label(location.getName());
                headerLabel.setStyle("-fx-font-size: 24px; -fx-font-weight: bold; -fx-text-fill: #533C56;");

                Region statusCircle = new Region();
                statusCircle.setMinSize(12, 12);
                statusCircle.setMaxSize(12, 12);
                statusCircle.setStyle(isAvailable ? 
                    "-fx-background-color: #2E7D32; -fx-background-radius: 50%;" :
                    "-fx-background-color: #C62828; -fx-background-radius: 50%;");
                
                Label statusLabel = new Label(isAvailable ? " Available" : " Reserved");
                statusLabel.setStyle(isAvailable ? 
                    "-fx-text-fill: #2E7D32; -fx-font-size: 16px; -fx-font-weight: bold;" :
                    "-fx-text-fill: #C62828; -fx-font-size: 16px; -fx-font-weight: bold;");
                
                HBox statusBox = new HBox(5);
                statusBox.setAlignment(Pos.CENTER_LEFT);
                statusBox.getChildren().addAll(statusCircle, statusLabel);
                
                headerBox.getChildren().addAll(headerLabel, statusBox);
                contentBox.getChildren().add(headerBox);

                if (location.getImageData() != null && location.getImageData().length > 0) {
                    ImageView imageView = new ImageView(new Image(new ByteArrayInputStream(location.getImageData())));
                    imageView.setFitWidth(400);
                    imageView.setFitHeight(250);
                    imageView.setPreserveRatio(true);
                    
                    VBox imageContainer = new VBox(imageView);
                    imageContainer.setStyle("-fx-background-color: white; -fx-padding: 10;" +
                                         "-fx-effect: dropshadow(three-pass-box, rgba(0,0,0,0.2), 10, 0, 0, 0);" +
                                         "-fx-background-radius: 10;");
                    contentBox.getChildren().add(imageContainer);
                }

                GridPane detailsGrid = new GridPane();
                detailsGrid.setHgap(20);
                detailsGrid.setVgap(15);
                detailsGrid.setStyle("-fx-background-color: #F5F0F6; -fx-padding: 20; -fx-background-radius: 10;");

                addDetailRow(detailsGrid, 0, "📍", "Location", location.getVille().name());
                addDetailRow(detailsGrid, 1, "👥", "Capacity", location.getCapacity() + " people");
                addDetailRow(detailsGrid, 2, "📏", "Dimensions", location.getDimension());
                addDetailRow(detailsGrid, 3, "💰", "Price", String.format("%.2f DT", location.getPrice()));
                
                contentBox.getChildren().add(detailsGrid);

                ButtonType closeButton = new ButtonType("Close", ButtonType.OK.getButtonData());
                dialogPane.getButtonTypes().add(closeButton);
                
                Button closeBtnCtrl = (Button) dialogPane.lookupButton(closeButton);
                closeBtnCtrl.setStyle("-fx-background-color: #533C56; -fx-text-fill: white; " +
                                    "-fx-font-size: 14px; -fx-padding: 8 20; -fx-background-radius: 5;");

                dialogPane.setContent(contentBox);
                dialogPane.setStyle("-fx-background-color: white;");
                
                detailsDialog.setDialogPane(dialogPane);
                detailsDialog.showAndWait();
                
            } catch (Exception e) {
                System.err.println("Error showing location details: " + e.getMessage());
                e.printStackTrace();

                Alert errorAlert = new Alert(Alert.AlertType.ERROR);
                errorAlert.setTitle("Error");
                errorAlert.setHeaderText("Error Processing Request");
                errorAlert.setContentText("An error occurred: " + e.getMessage() + "\nPlease try again.");
                errorAlert.showAndWait();
            }
        }
    }
    
    private void addDetailRow(GridPane grid, int row, String icon, String label, String value) {
        Label iconLabel = new Label(icon);
        iconLabel.setStyle("-fx-font-size: 18px;");
        
        Label titleLabel = new Label(label);
        titleLabel.setStyle("-fx-font-size: 14px; -fx-text-fill: #666666;");
        
        Label valueLabel = new Label(value);
        valueLabel.setStyle("-fx-font-size: 14px; -fx-font-weight: bold; -fx-text-fill: #533C56;");
        
        grid.add(iconLabel, 0, row);
        grid.add(titleLabel, 1, row);
        grid.add(valueLabel, 2, row);
    }

    @FXML
    private void handle3DTour() {
        if (location != null) {
            try {
                FXMLLoader loader = new FXMLLoader(getClass().getResource("/LocationTour3D.fxml"));
                Parent root = loader.load();
                
                LocationTour3DController controller = loader.getController();
                controller.setLocation(location);
                
                Stage stage = new Stage();
                stage.setTitle("3D Tour - " + location.getName());
                Scene scene = new Scene(root);
                scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
                stage.setScene(scene);

                stage.setMaximized(true);

                stage.setOnCloseRequest(e -> controller.cleanup());
                
                stage.show();
            } catch (IOException e) {
                e.printStackTrace();
                showError("Error launching 3D tour", e);
            }
        }
    }

    public void hideManagementButtons() {
        editButton.setVisible(false);
        editButton.setManaged(false);
        deleteButton.setVisible(false);
        deleteButton.setManaged(false);
    }

    private void showError(String message, IOException e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
    }
} 