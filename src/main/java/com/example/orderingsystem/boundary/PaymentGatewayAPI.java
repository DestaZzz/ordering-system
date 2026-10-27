package com.example.orderingsystem.boundary;

public interface PaymentGatewayAPI {
    String mintaTransaksiPembayaran(double total, String tokenKartu);
}
