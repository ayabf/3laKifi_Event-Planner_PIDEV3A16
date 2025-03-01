package services;

import Models.Order;
import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.checkout.Session;
import com.stripe.param.checkout.SessionCreateParams;

public class StripeService {

    private static final String SECRET_KEY = "sk_test_51QwR8RJig9dgGMXA88GSrTkZMyTs3eDJnxmagMJK14JZZSHHfXqi46pWZ3lJS6CU5cRyjDS3EsSFgF3yV4yhHONv004ryM6e41";

    public StripeService() {
        Stripe.apiKey = SECRET_KEY;
    }

    public String createCheckoutSession(Order order) {
        try {
            SessionCreateParams params =
                    SessionCreateParams.builder()
                            .setMode(SessionCreateParams.Mode.PAYMENT)
                            .setSuccessUrl("http://localhost:8080/success?orderId=" + order.getOrderId())
                            .setCancelUrl("http://localhost:8080/cancel?orderId=" + order.getOrderId())
                            .addLineItem(
                                    SessionCreateParams.LineItem.builder()
                                            .setQuantity(1L)
                                            .setPriceData(
                                                    SessionCreateParams.LineItem.PriceData.builder()
                                                            .setCurrency("usd")
                                                            .setUnitAmount((long) (order.getTotalPrice() * 100)) // 💰 Stripe attend un montant en centimes
                                                            .setProductData(SessionCreateParams.LineItem.PriceData.ProductData.builder()
                                                                    .setName("Commande #" + order.getOrderId())
                                                                    .build())
                                                            .build()
                                            ).build()
                            ).build();


            Session session = Session.create(params);
            return session.getUrl(); // URL du paiement Stripe

        } catch (StripeException e) {
            e.printStackTrace();
            return null;
        }
    }
}
