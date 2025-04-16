package com.esprit.pi.controllers;

import com.esprit.pi.services.StripePaymentService;
import com.stripe.exception.StripeException;
import com.stripe.model.PaymentIntent;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

import java.util.HashMap;
import java.util.Map;

@RestController
@RequestMapping("/api/payment")
@CrossOrigin(origins = "http://localhost:4200")
public class PaymentController {

    @Autowired
    private StripePaymentService stripePaymentService;

    @PostMapping("/create-payment-intent/{evaluationId}")
    public ResponseEntity<Map<String, String>> createPaymentIntent(
            @PathVariable Long evaluationId,
            @RequestBody Map<String, Double> requestBody) {
        try {
            Double amount = requestBody.get("amount");
            if (amount == null || amount <= 0) {
                Map<String, String> errorResponse = new HashMap<>();
                errorResponse.put("error", "Montant invalide ou non fourni");
                return ResponseEntity.status(HttpStatus.BAD_REQUEST).body(errorResponse);
            }

            // Appelle le service pour créer le PaymentIntent
            PaymentIntent paymentIntent = stripePaymentService.createPaymentIntent(evaluationId, amount);

            Map<String, String> response = new HashMap<>();
            response.put("clientSecret", paymentIntent.getClientSecret());
            System.out.println("PaymentController - ClientSecret envoyé : " + paymentIntent.getClientSecret());

            return ResponseEntity.ok(response);
        } catch (StripeException e) {
            e.printStackTrace();
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", "Erreur Stripe : " + e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        } catch (IllegalStateException e) {
            Map<String, String> errorResponse = new HashMap<>();
            errorResponse.put("error", e.getMessage());
            return ResponseEntity.status(HttpStatus.INTERNAL_SERVER_ERROR).body(errorResponse);
        }
    }
}