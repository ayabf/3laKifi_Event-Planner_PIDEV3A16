package controllers;

import javafx.fxml.FXML;
import javafx.fxml.FXMLLoader;
import javafx.scene.Parent;
import javafx.scene.Scene;
import javafx.scene.control.*;
import javafx.scene.control.cell.PropertyValueFactory;
import javafx.scene.layout.StackPane;
import javafx.stage.Stage;
import services.ServiceEvent;
import services.ServiceBooking;
import services.ServiceLocation;
import Models.Event;
import Models.Location;
import Models.Booking;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import javafx.event.ActionEvent;
import javafx.scene.Node;
import javafx.beans.binding.Bindings;
import javafx.scene.chart.PieChart;
import javafx.scene.chart.BarChart;
import javafx.scene.chart.LineChart;
import javafx.scene.chart.XYChart;
import javafx.collections.FXCollections;
import javafx.collections.ObservableList;
import java.time.format.DateTimeFormatter;
import java.time.LocalDateTime;
import java.time.LocalDate;
import java.time.YearMonth;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;
import java.util.DoubleSummaryStatistics;
import java.util.IntSummaryStatistics;
import java.util.Comparator;
import java.io.IOException;
import java.net.URL;
import java.sql.SQLException;

public class AdminDashboardController {
    @FXML private StackPane contentArea;
    @FXML private Label totalEventsLabel;
    @FXML private Label totalBookingsLabel;
    @FXML private Label totalLocationsLabel;
    
    @FXML private TableView<Event> eventsTable;
    @FXML private TableColumn<Event, String> eventNameColumn;
    @FXML private TableColumn<Event, String> eventDateColumn;
    @FXML private TableColumn<Event, String> eventCityColumn;
    @FXML private TableColumn<Event, Integer> eventCapacityColumn;
    @FXML private TableColumn<Event, String> eventStatusColumn;
    
    @FXML private TableView<Location> locationsTable;
    @FXML private TableColumn<Location, String> locationNameColumn;
    @FXML private TableColumn<Location, String> locationCityColumn;
    @FXML private TableColumn<Location, Integer> locationCapacityColumn;
    @FXML private TableColumn<Location, Double> locationPriceColumn;
    @FXML private TableColumn<Location, String> locationStatusColumn;

    @FXML private Label upcomingEventsLabel;
    @FXML private Label inProgressEventsLabel;
    @FXML private Label completedEventsLabel;
    @FXML private Label avgEventCapacityLabel;
    @FXML private Label monthlyBookingsLabel;
    @FXML private Label bookingTrendLabel;
    @FXML private Label popularLocationLabel;
    @FXML private Label popularLocationBookingsLabel;
    @FXML private Label avgLocationPriceLabel;
    @FXML private Label avgLocationCapacityLabel;

    @FXML
    private PieChart eventStatusChart;
    @FXML
    private BarChart<String, Number> eventCapacityChart;
    @FXML
    private LineChart<String, Number> bookingTrendChart;
    @FXML
    private BarChart<String, Number> popularLocationsChart;
    @FXML
    private BarChart<String, Number> priceDistributionChart;
    @FXML
    private BarChart<String, Number> capacityDistributionChart;

    private final ServiceEvent eventService = new ServiceEvent();
    private final ServiceBooking bookingService = new ServiceBooking();
    private final ServiceLocation locationService = new ServiceLocation();
    private final DateTimeFormatter dateFormatter = DateTimeFormatter.ofPattern("dd/MM/yyyy HH:mm");

    @FXML
    void initialize() {
        initializeEventTable();
        initializeLocationTable();
        updateDashboardStats();
        loadTableData();
        updateAdvancedStatistics();
    }
    
    private void initializeEventTable() {
        eventNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        eventDateColumn.setCellValueFactory(cellData -> {
            LocalDateTime date = cellData.getValue().getStart_date();
            return Bindings.createStringBinding(
                () -> date.format(dateFormatter)
            );
        });
        eventCityColumn.setCellValueFactory(cellData -> 
            Bindings.createStringBinding(
                () -> cellData.getValue().getCity().name()
            ));
        eventCapacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        eventStatusColumn.setCellValueFactory(cellData -> {
            LocalDateTime now = LocalDateTime.now();
            LocalDateTime startDate = cellData.getValue().getStart_date();
            LocalDateTime endDate = cellData.getValue().getEnd_date();
            return Bindings.createStringBinding(() -> {
                if (now.isBefore(startDate)) return "Upcoming";
                if (now.isAfter(endDate)) return "Completed";
                return "In Progress";
            });
        });
    }
    
    private void initializeLocationTable() {
        locationNameColumn.setCellValueFactory(new PropertyValueFactory<>("name"));
        locationCityColumn.setCellValueFactory(cellData ->
            Bindings.createStringBinding(
                () -> cellData.getValue().getVille().name()
            ));
        locationCapacityColumn.setCellValueFactory(new PropertyValueFactory<>("capacity"));
        locationPriceColumn.setCellValueFactory(new PropertyValueFactory<>("price"));
        locationStatusColumn.setCellValueFactory(new PropertyValueFactory<>("status"));
    }
    
    private void loadTableData() {
        try {
            ObservableList<Event> events = FXCollections.observableArrayList(eventService.getAll());
            eventsTable.setItems(events);

            ObservableList<Location> locations = FXCollections.observableArrayList(locationService.getAll());
            locationsTable.setItems(locations);
        } catch (SQLException e) {
            showError("Error loading table data", e);
        }
    }
    
    private void updateDashboardStats() {
        try {
            int totalEvents = eventService.getTotalCount();
            int totalBookings = bookingService.getTotalCount();
            int totalLocations = locationService.getTotalCount();

            totalEventsLabel.setText(String.valueOf(totalEvents));
            totalBookingsLabel.setText(String.valueOf(totalBookings));
            totalLocationsLabel.setText(String.valueOf(totalLocations));
        } catch (SQLException e) {
            showError("Error updating dashboard statistics", e);
        }
    }

    private void updateAdvancedStatistics() {
        try {
            updateEventStatusChart();
            updateEventCapacityChart();
            updateBookingTrendChart();
            updatePopularLocationsChart();
            updatePriceDistributionChart();
            updateCapacityDistributionChart();
        } catch (SQLException e) {
            showError("Error updating statistics", e);
        }
    }
    
    private void updateEventStatusChart() throws SQLException {
        ObservableList<PieChart.Data> pieChartData = FXCollections.observableArrayList();
        List<Event> events = eventService.getAll();
        LocalDateTime now = LocalDateTime.now();
        
        long upcomingCount = events.stream()
            .filter(e -> e.getStart_date().isAfter(now))
            .count();
        long inProgressCount = events.stream()
            .filter(e -> e.getStart_date().isBefore(now) && e.getEnd_date().isAfter(now))
            .count();
        long completedCount = events.stream()
            .filter(e -> e.getEnd_date().isBefore(now))
            .count();
        
        pieChartData.add(new PieChart.Data("Upcoming", upcomingCount));
        pieChartData.add(new PieChart.Data("In Progress", inProgressCount));
        pieChartData.add(new PieChart.Data("Completed", completedCount));
        
        eventStatusChart.setData(pieChartData);
        eventStatusChart.setTitle("Event Status Distribution");
    }
    
    private void updateEventCapacityChart() throws SQLException {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Average Capacity");
        
        Map<String, Double> cityCapacities = eventService.getAll().stream()
            .collect(Collectors.groupingBy(
                event -> event.getCity().name(),
                Collectors.averagingInt(Event::getCapacity)
            ));
        
        cityCapacities.forEach((city, avgCapacity) -> 
            series.getData().add(new XYChart.Data<>(city, avgCapacity))
        );
        
        eventCapacityChart.getData().clear();
        eventCapacityChart.getData().add(series);
        eventCapacityChart.setTitle("Average Event Capacity by City");
    }
    
    private void updateBookingTrendChart() throws SQLException {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Monthly Bookings");
        
        Map<YearMonth, Long> monthlyBookings = bookingService.getAll().stream()
            .collect(Collectors.groupingBy(
                b -> YearMonth.from(b.getStart_date()),
                Collectors.counting()
            ));
        
        monthlyBookings.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .limit(6)
            .forEach(entry -> 
                series.getData().add(new XYChart.Data<>(
                    entry.getKey().format(DateTimeFormatter.ofPattern("MMM yy")),
                    entry.getValue()
                ))
            );
        
        bookingTrendChart.getData().clear();
        bookingTrendChart.getData().add(series);
        bookingTrendChart.setTitle("Booking Trends (Last 6 Months)");
    }
    
    private void updatePopularLocationsChart() throws SQLException {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Bookings per Location");
        
        Map<Integer, Long> locationBookings = bookingService.getAll().stream()
            .collect(Collectors.groupingBy(
                Booking::getLocation_id,
                Collectors.counting()
            ));
        
        locationBookings.entrySet().stream()
            .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
            .limit(5)
            .forEach(entry -> {
                try {
                    Location location = locationService.getById(entry.getKey());
                    if (location != null) {
                        series.getData().add(new XYChart.Data<>(
                            location.getName(),
                            entry.getValue()
                        ));
                    }
                } catch (SQLException e) {
                    showError("Error loading location details", e);
                }
            });
        
        popularLocationsChart.getData().clear();
        popularLocationsChart.getData().add(series);
        popularLocationsChart.setTitle("Most Popular Locations");
    }
    
    private void updatePriceDistributionChart() throws SQLException {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Locations");
        
        Map<String, Long> priceRanges = locationService.getAll().stream()
            .collect(Collectors.groupingBy(
                location -> getPriceRange(location.getPrice()),
                Collectors.counting()
            ));
        
        priceRanges.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> 
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()))
            );
        
        priceDistributionChart.getData().clear();
        priceDistributionChart.getData().add(series);
        priceDistributionChart.setTitle("Location Price Distribution");
    }
    
    private String getPriceRange(double price) {
        if (price < 100) return "0-100";
        else if (price < 200) return "100-200";
        else if (price < 500) return "200-500";
        else if (price < 1000) return "500-1000";
        else return "1000+";
    }
    
    private void updateCapacityDistributionChart() throws SQLException {
        XYChart.Series<String, Number> series = new XYChart.Series<>();
        series.setName("Locations");
        
        Map<String, Long> capacityRanges = locationService.getAll().stream()
            .collect(Collectors.groupingBy(
                location -> getCapacityRange(location.getCapacity()),
                Collectors.counting()
            ));
        
        capacityRanges.entrySet().stream()
            .sorted(Map.Entry.comparingByKey())
            .forEach(entry -> 
                series.getData().add(new XYChart.Data<>(entry.getKey(), entry.getValue()))
            );
        
        capacityDistributionChart.getData().clear();
        capacityDistributionChart.getData().add(series);
        capacityDistributionChart.setTitle("Location Capacity Distribution");
    }
    
    private String getCapacityRange(int capacity) {
        if (capacity < 50) return "0-50";
        else if (capacity < 100) return "50-100";
        else if (capacity < 200) return "100-200";
        else if (capacity < 500) return "200-500";
        else return "500+";
    }

    @FXML
    void handleHome() {
        try {
            URL dashboardUrl = getClass().getResource("/AdminDashboard.fxml");
            if (dashboardUrl == null) {
                throw new IOException("Cannot find AdminDashboard.fxml");
            }
            FXMLLoader loader = new FXMLLoader(dashboardUrl);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            showError("Error loading dashboard", e);
        }
    }

    private void navigateToView(String fxmlPath, Node sourceNode) {
        try {
            URL url = getClass().getResource(fxmlPath);
            if (url == null) {
                throw new IOException("Cannot find " + fxmlPath);
            }
            FXMLLoader loader = new FXMLLoader(url);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            Stage stage = (Stage) sourceNode.getScene().getWindow();
            stage.setScene(scene);
            stage.show();
        } catch (IOException e) {
            showError("Error loading view", e);
        }
    }

    @FXML
    void handleBackOffice(ActionEvent event) {
        LandingController.setPreviousPage("/AdminDashboard.fxml");
        navigateToView("/Landing.fxml", (Node) event.getSource());
    }

    @FXML
    void handleViewAllEvents() {
        loadView("/EventList.fxml", "Event List");
    }

    @FXML
    void handleManageBookings() {
        loadView("/BookingManagement.fxml", "Booking Management");
    }

    @FXML
    void handleViewAllLocations() {
        loadView("/LocationList.fxml", "Location List");
    }

    @FXML
    void handleLogout() {
        try {
            URL loginUrl = getClass().getResource("/log.fxml");
            if (loginUrl == null) {
                throw new IOException("Cannot find log.fxml");
            }
            FXMLLoader loader = new FXMLLoader(loginUrl);
            Parent root = loader.load();
            Scene scene = new Scene(root);
            scene.getStylesheets().add(getClass().getResource("/styles/global.css").toExternalForm());
            Stage stage = (Stage) contentArea.getScene().getWindow();
            stage.setScene(scene);
        } catch (IOException e) {
            showError("Error during logout", e);
        }
    }

    private void loadView(String fxmlPath, String title) {
        try {
            FXMLLoader loader = new FXMLLoader();
            URL resource = getClass().getResource(fxmlPath);
            if (resource == null) {
                throw new IOException("Cannot find " + fxmlPath);
            }
            loader.setLocation(resource);
            
            Parent view = loader.load();
            if (view == null) {
                throw new IOException("Failed to load view: " + fxmlPath);
            }
            
            contentArea.getChildren().clear();
            contentArea.getChildren().add(view);
            
        } catch (IOException e) {
            showError("Error loading view: " + title, e);
        }
    }

    private void showError(String message, Exception e) {
        Alert alert = new Alert(Alert.AlertType.ERROR);
        alert.setTitle("Error");
        alert.setHeaderText(message);
        alert.setContentText(e.getMessage());
        alert.showAndWait();
        e.printStackTrace();
    }
} 