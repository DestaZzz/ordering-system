package com.example.orderingsystem.controller.impl;

import com.example.orderingsystem.boundary.PaymentGatewayAPI;
import com.example.orderingsystem.dto.request.CheckoutItemRequest;
import com.example.orderingsystem.dto.request.CheckoutRequest;
import com.example.orderingsystem.dto.response.CheckoutResponse;
import com.example.orderingsystem.entity.*;
import com.example.orderingsystem.exception.BusinessValidationException;
import com.example.orderingsystem.exception.ResourceNotFoundException;
import com.example.orderingsystem.repository.MenuRepository;
import com.example.orderingsystem.repository.PelangganRepository;
import com.example.orderingsystem.repository.PesananRepository;
import com.example.orderingsystem.controller.CheckoutService;
import lombok.RequiredArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.time.LocalDateTime;
import java.util.UUID;

/**
 * CONTROLLER LAYER IMPLEMENTATION (BCE Architecture)
 * Mengisi peran Controller BCE / Orchestrator yang mengoordinasikan interaksi antara:
 * 1. Validasi entitas Pelanggan dan Menu
 * 2. Eksekusi metode domain validasiStok() dan kurangStok()
 * 3. Menghubungi Boundary Gateway Pembayaran
 * 4. Menyimpan status entitas Pesanan, ItemPesanan, dan Pembayaran
 */
@Service
@RequiredArgsConstructor
public class CheckoutServiceImpl implements CheckoutService {

    private final PelangganRepository pelangganRepository;
    private final MenuRepository menuRepository;
    private final PesananRepository pesananRepository;
    private final PaymentGatewayAPI paymentGatewayAPI; // Boundary Layer

    @Override
    @Transactional
    public CheckoutResponse prosesCheckout(CheckoutRequest request) {
        // 1. Verifikasi Pelanggan
        Pelanggan pelanggan = pelangganRepository.findById(request.getIdPelanggan())
                .orElseThrow(() -> new ResourceNotFoundException("Pelanggan dengan ID " + request.getIdPelanggan() + " tidak ditemukan"));

        // 2. Inisialisasi Pesanan Baru
        Pesanan pesanan = Pesanan.builder()
                .idPesanan("ORD-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .tanggal(LocalDateTime.now())
                .status(StatusPesanan.MENUNGGU_BAYAR)
                .pelanggan(pelanggan)
                .build();

        // 3. Iterasi Data Keranjang: Validasi Stok Menu & Rakit ItemPesanan
        for (CheckoutItemRequest itemReq : request.getKeranjangData()) {
            Menu menu = menuRepository.findById(itemReq.getIdMenu())
                    .orElseThrow(() -> new ResourceNotFoundException("Menu dengan ID " + itemReq.getIdMenu() + " tidak ditemukan"));

            // Validasi dan Kurangi Stok via Domain Method Menu
            if (!menu.kurangStok(itemReq.getJumlah())) {
                throw new BusinessValidationException("Stok tidak mencukupi untuk menu: " + menu.getNamaMenu() + " (Sisa: " + menu.getStok() + ")");
            }
            menuRepository.save(menu);

            // Buat ItemPesanan
            ItemPesanan item = ItemPesanan.builder()
                    .idItem("ITEM-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                    .jumlah(itemReq.getJumlah())
                    .menu(menu)
                    .build();

            // Tambahkan ke Pesanan
            pesanan.tambahItem(item);
        }

        // 4. Interaksi ke Boundary Eksternal: Payment Gateway
        String refCode;
        try {
            refCode = paymentGatewayAPI.mintaTransaksiPembayaran(pesanan.getTotalBiaya(), request.getTokenKartu());
        } catch (Exception ex) {
            throw new BusinessValidationException("Pembayaran gagal diproses: " + ex.getMessage());
        }

        // 5. Pembentukan Entitas Pembayaran
        Pembayaran pembayaran = Pembayaran.builder()
                .idPembayaran("PAY-" + UUID.randomUUID().toString().substring(0, 8).toUpperCase())
                .jumlahBayar(pesanan.getTotalBiaya())
                .metode(request.getMetodePembayaran())
                .statusBayar("SUCCESS")
                .kodeReferensi(refCode)
                .pesanan(pesanan)
                .build();

        // 6. Verifikasi Pembayaran via Domain Method
        if (pembayaran.prosesVerifikasi()) {
            pesanan.updateStatus(StatusPesanan.DIPROSES);
        } else {
            pesanan.updateStatus(StatusPesanan.BATAL);
        }
        pesanan.setPembayaran(pembayaran);

        // 7. Simpan Pesanan (Cascade Type ALL menyimpan ItemPesanan & Pembayaran)
        Pesanan savedPesanan = pesananRepository.save(pesanan);

        return CheckoutResponse.builder()
                .idPesanan(savedPesanan.getIdPesanan())
                .idPembayaran(pembayaran.getIdPembayaran())
                .totalBiaya(savedPesanan.getTotalBiaya())
                .statusPesanan(savedPesanan.getStatus())
                .statusPembayaran(pembayaran.getStatusBayar())
                .kodeReferensi(pembayaran.getKodeReferensi())
                .tanggal(savedPesanan.getTanggal())
                .build();
    }

    @Override
    @Transactional
    public void batalkanTransaksi(String idPesanan, String alasan) {
        Pesanan pesanan = pesananRepository.findById(idPesanan)
                .orElseThrow(() -> new ResourceNotFoundException("Pesanan dengan ID " + idPesanan + " tidak ditemukan"));

        if (pesanan.getStatus() == StatusPesanan.SELESAI || pesanan.getStatus() == StatusPesanan.DIKIRIM) {
            throw new BusinessValidationException("Pesanan sudah dikirim/selesai dan tidak dapat dibatalkan.");
        }

        // Kembalikan stok menu
        for (ItemPesanan item : pesanan.getDaftarItem()) {
            Menu menu = item.getMenu();
            menu.setStok(menu.getStok() + item.getJumlah());
            menuRepository.save(menu);
        }

        pesanan.updateStatus(StatusPesanan.BATAL);
        pesananRepository.save(pesanan);

        System.out.println("== [LOG PEMBATALAN] Pesanan " + idPesanan + " dibatalkan. Alasan: " + alasan + " ==");
    }
}
