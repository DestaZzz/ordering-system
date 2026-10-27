package com.example.orderingsystem.controller;

import com.example.orderingsystem.dto.request.CheckoutRequest;
import com.example.orderingsystem.dto.response.CheckoutResponse;

/**
 * CONTROLLER LAYER (BCE Architecture)
 * Antarmuka Controller BCE untuk mengorkestrasi logika bisnis sistem checkout.
 */
public interface CheckoutService {
    CheckoutResponse prosesCheckout(CheckoutRequest request);
    void batalkanTransaksi(String idPesanan, String alasan);
}
