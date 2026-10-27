package com.example.orderingsystem.boundary.impl;

import com.example.orderingsystem.boundary.PaymentGatewayAPI;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Component;
import java.util.UUID;

@Component
public class PaymentGatewayAPIImpl implements PaymentGatewayAPI {

    @Value("${payment.gateway.endpoint-url}")
    private String endpointUrl;

    @Value("${payment.gateway.api-key}")
    private String apiKey;

    @Override
    public String mintaTransaksiPembayaran(double total, String tokenKartu) {
        System.out.println("== [BOUNDARY LOG] Mengirim Transaksi ke Gateway ==");
        System.out.println("URL: " + endpointUrl);
        if (apiKey != null && apiKey.length() >= 7) {
            System.out.println("API Key: " + apiKey.substring(0, 7) + "********");
        } else {
            System.out.println("API Key: ********");
        }
        System.out.println("Charge Total: Rp" + total + " | Card Token: " + tokenKartu);

        // Validasi token tiruan ("invalid_card" akan gagal)
        if ("invalid_card".equalsIgnoreCase(tokenKartu)) {
            throw new RuntimeException("Penolakan dari Gateway Pembayaran: Saldo tidak cukup atau token kartu tidak sah");
        }

        return "PG-REF-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase();
    }
}
