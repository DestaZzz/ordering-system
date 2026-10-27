package com.example.orderingsystem.entity;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.*;

class EntityDomainMethodTest {

    @Test
    @DisplayName("Menu Domain - validasiStok dan kurangStok Berhasil")
    void testMenuValidasiDanKurangStokSukses() {
        Menu menu = Menu.builder()
                .idMenu("MENU-01")
                .namaMenu("Kopi Susu Gula Aren")
                .harga(18000.0)
                .stok(10)
                .build();

        assertTrue(menu.validasiStok(5));
        boolean result = menu.kurangStok(5);

        assertTrue(result);
        assertEquals(5, menu.getStok());
    }

    @Test
    @DisplayName("Menu Domain - kurangStok Gagal ketika stok kurang")
    void testMenuKurangStokGagal() {
        Menu menu = Menu.builder()
                .idMenu("MENU-01")
                .namaMenu("Kopi Susu Gula Aren")
                .harga(18000.0)
                .stok(3)
                .build();

        assertFalse(menu.validasiStok(5));
        boolean result = menu.kurangStok(5);

        assertFalse(result);
        assertEquals(3, menu.getStok()); // Stok tidak berubah
    }

    @Test
    @DisplayName("ItemPesanan Domain - hitungSubtotal Akurat")
    void testItemPesananHitungSubtotal() {
        Menu menu = Menu.builder()
                .idMenu("MENU-02")
                .harga(25000.0)
                .build();

        ItemPesanan item = ItemPesanan.builder()
                .idItem("ITEM-01")
                .jumlah(3)
                .menu(menu)
                .build();

        double subtotal = item.hitungSubtotal();
        assertEquals(75000.0, subtotal);
        assertEquals(75000.0, item.getSubtotal());
    }

    @Test
    @DisplayName("Pesanan Domain - hitungTotal Kalkulasi Seluruh Item")
    void testPesananHitungTotal() {
        Menu menu1 = Menu.builder().harga(18000.0).build();
        Menu menu2 = Menu.builder().harga(25000.0).build();

        Pesanan pesanan = Pesanan.builder()
                .idPesanan("ORD-001")
                .status(StatusPesanan.MENUNGGU_BAYAR)
                .build();

        ItemPesanan item1 = ItemPesanan.builder().jumlah(2).menu(menu1).build();
        ItemPesanan item2 = ItemPesanan.builder().jumlah(1).menu(menu2).build();

        pesanan.tambahItem(item1);
        pesanan.tambahItem(item2);

        double total = pesanan.hitungTotal();
        assertEquals(61000.0, total);
        assertEquals(61000.0, pesanan.getTotalBiaya());
    }

    @Test
    @DisplayName("Pembayaran Domain - prosesVerifikasi Berhasil dan Gagal")
    void testPembayaranProsesVerifikasi() {
        Pembayaran bayarSukses = Pembayaran.builder()
                .statusBayar("SUCCESS")
                .kodeReferensi("PG-REF-12345")
                .build();

        assertTrue(bayarSukses.prosesVerifikasi());

        Pembayaran bayarGagal = Pembayaran.builder()
                .statusBayar("FAILED")
                .kodeReferensi(null)
                .build();

        assertFalse(bayarGagal.prosesVerifikasi());
    }

    @Test
    @DisplayName("Pelanggan Domain - ubahAlamat Validasi Input Kosong")
    void testPelangganUbahAlamat() {
        Pelanggan pelanggan = Pelanggan.builder()
                .nama("Budi Santoso")
                .email("budi@example.com")
                .alamat("Alamat Lama")
                .build();

        pelanggan.ubahAlamat("Jl. Baru No. 10");
        assertEquals("Jl. Baru No. 10", pelanggan.getAlamat());

        assertThrows(IllegalArgumentException.class, () -> pelanggan.ubahAlamat("   "));
    }
}
