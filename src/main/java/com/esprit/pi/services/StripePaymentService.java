package com.esprit.pi.services;

import com.stripe.Stripe;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import com.stripe.param.PaymentIntentCreateParams;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;

import jakarta.annotation.PostConstruct;

@Service
public class StripePaymentService {

    @Value("${stripe.secret.key}")
    private String stripeSecretKey;

    @PostConstruct
    public void init() {
        Stripe.apiKey = stripeSecretKey;
        System.out.println("StripePaymentService - Clé Stripe initialisée : " + (stripeSecretKey != null ? "Oui" : "Non"));
    }

    public PaymentIntent createPaymentIntent(Long evaluationId, double montant) throws StripeException {
        if (Stripe.apiKey == null || Stripe.apiKey.isEmpty()) {
            throw new IllegalStateException("La clé API Stripe n'est pas configurée dans StripePaymentService.");
        }

        PaymentIntentCreateParams params = PaymentIntentCreateParams.builder()
                .setAmount((long) (montant * 100)) // Montant en centimes
                .setCurrency("eur")
                .setDescription("Don fictif pour motiver l'équipe du projet " + evaluationId)
                .putMetadata("evaluationId", evaluationId.toString())
                .build();

        PaymentIntent paymentIntent = PaymentIntent.create(params);
        System.out.println("StripePaymentService - PaymentIntent créé : " + paymentIntent.getId());
        return paymentIntent;
    }
}
