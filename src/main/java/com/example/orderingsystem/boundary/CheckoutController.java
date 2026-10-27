package com.example.orderingsystem.boundary;

import com.example.orderingsystem.dto.request.BatalTransaksiRequest;
import com.example.orderingsystem.dto.request.CheckoutRequest;
import com.example.orderingsystem.dto.response.ApiResponse;
import com.example.orderingsystem.dto.response.CheckoutResponse;
import com.example.orderingsystem.controller.CheckoutService;
import jakarta.validation.Valid;
import lombok.RequiredArgsConstructor;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.*;

/**
 * BOUNDARY LAYER (BCE Architecture)
 * Mengisi peran Boundary untuk antarmuka REST API / Client.
 * Menerima HTTP Request, memvalidasi DTO, dan meneruskan ke Controller BCE (@Service).
 */
@RestController
@RequestMapping("/api/v1/checkout")
@RequiredArgsConstructor
public class CheckoutController {

    private final CheckoutService checkoutService;

    @PostMapping
    public ResponseEntity<ApiResponse<CheckoutResponse>> prosesCheckout(
            @Valid @RequestBody CheckoutRequest request) {
        CheckoutResponse response = checkoutService.prosesCheckout(request);
        return new ResponseEntity<>(
                ApiResponse.success("Checkout dan pembayaran berhasil diproses", response),
                HttpStatus.CREATED
        );
    }

    @PostMapping("/{idPesanan}/batal")
    public ResponseEntity<ApiResponse<Void>> batalkanTransaksi(
            @PathVariable String idPesanan,
            @Valid @RequestBody BatalTransaksiRequest request) {
        checkoutService.batalkanTransaksi(idPesanan, request.getAlasan());
        return ResponseEntity.ok(ApiResponse.success("Transaksi berhasil dibatalkan: " + request.getAlasan(), null));
    }
}
