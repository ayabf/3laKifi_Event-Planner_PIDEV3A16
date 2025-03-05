package services;

import org.apache.commons.math3.stat.regression.SimpleRegression;
import utils.DataSource;
import java.sql.*;
import java.time.LocalDate;
import java.util.HashMap;
import java.util.Map;

public class PredictionService {
    private Connection conn;

    public PredictionService() {
        conn = DataSource.getInstance().getConnection();
    }

    /**
     * 📌 Récupère les commandes passées et applique une régression linéaire pour prédire les commandes du mois suivant.
     */
    public int predictNextMonthOrders() {
        Map<Integer, Integer> ordersPerMonth = getOrdersPerMonth();

        if (ordersPerMonth.isEmpty()) {
            System.out.println("❌ Pas de données suffisantes pour la prédiction.");
            return 0;
        }

        // 📊 Création du modèle de régression linéaire
        SimpleRegression regression = new SimpleRegression();

        for (Map.Entry<Integer, Integer> entry : ordersPerMonth.entrySet()) {
            regression.addData(entry.getKey(), entry.getValue());
        }

        // 🔮 Prédiction pour le mois suivant
        int nextMonth = LocalDate.now().getMonthValue() + 1;
        double predictedOrders = regression.predict(nextMonth);

        return (int) Math.round(predictedOrders);
    }

    /**
     * 📌 Récupère le nombre de commandes par mois.
     */
    private Map<Integer, Integer> getOrdersPerMonth() {
        Map<Integer, Integer> ordersPerMonth = new HashMap<>();
        String sql = "SELECT MONTH(ordered_at) AS month, COUNT(*) AS total_orders FROM `order` GROUP BY month";

        try (Statement stmt = conn.createStatement();
             ResultSet rs = stmt.executeQuery(sql)) {

            while (rs.next()) {
                ordersPerMonth.put(rs.getInt("month"), rs.getInt("total_orders"));
            }

        } catch (SQLException e) {
            e.printStackTrace();
        }

        return ordersPerMonth;
    }

}
