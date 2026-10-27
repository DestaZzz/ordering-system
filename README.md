# Ordering System - BCE Architecture Spring Boot 3

Proyek Backend Sistem Checkout Pesanan dan Transaksi berbasis **Spring Boot 3 (Java 17)** dengan penerapan arsitektur **Boundary-Controller-Entity (BCE)** yang disesuaikan secara presisi dengan kriteria akademis.

---

## 📌 Penyesuaian Utama & Arsitektur BCE

Sesuai dengan standar arsitektur BCE:
1. **Boundary Layer (`package com.example.orderingsystem.boundary`)**:
   - `@RestController` (`CheckoutController`) ditempatkan di dalam package `boundary` karena berfungsi sebagai antarmuka/pintu masuk HTTP (UI/REST Contract) bagi aktor eksternal.
   - `PaymentGatewayAPI` & `PaymentGatewayAPIImpl` sebagai boundary integrasi dengan sistem pihak ketiga.
2. **Controller Layer (`package com.example.orderingsystem.controller`)**:
   - `CheckoutService` & `CheckoutServiceImpl` (`@Service`) bertindak sebagai **Controller BCE / Orchestrator**. Mengoordinasikan alur bisnis, verifikasi stok, pemanggilan payment gateway, dan manajemen transaksi database tanpa menyimpan state persisten di memori.
3. **Entity Layer (`package com.example.orderingsystem.entity`)**:
   - Implementasi **Rich Domain Model** yang menyimpan state dan mengandung domain logic internal seperti `validasiStok()`, `kurangStok()`, `hitungTotal()`, `hitungSubtotal()`, dan `prosesVerifikasi()`.
   - Mengombinasikan pola **Komposisi** (Pesanan ➔ ItemPesanan) dan **Agregasi** (ItemPesanan ➔ Menu).

---

## 🚀 Fitur & Kemudahan Jalankan (Out-of-the-Box)

- **Database Default H2 (In-Memory)**: Tidak memerlukan instalasi PostgreSQL secara manual.
- **Data Sample Otomatis (`data.sql`)**: Seed data pelanggan dan menu otomatis dimuat saat aplikasi dijalankan.
- **Tanpa Hardcoded Secret / Password**: Menjaga kebersihan repositori GitHub.

---

## 🛠️ Cara Menjalankan Aplikasi

### 1. Menggunakan Maven
Pastikan Anda menggunakan Java 17 atau 21. Jalankan perintah:

```bash
mvn spring-boot:run
```

Aplikasi akan berjalan pada port `8080`.

### 2. Membuka H2 Console
Akses database in-memory secara visual melalui browser:
- **URL**: `http://localhost:8080/h2-console`
- **JDBC URL**: `jdbc:h2:mem:ordering_db`
- **User**: `sa`
- **Password**: *(kosongkan)*

---

## 🧪 Menjalankan Unit Test

Proyek ini dilengkapi dengan Unit Test komprehensif untuk pengujian metode domain pada entitas (`validasiStok`, `kurangStok`, `hitungTotal`, `prosesVerifikasi`, dll).

Jalankan perintah pengujian:

```bash
mvn test
```

---

## 📡 Skenario Uji Coba REST API

### 1. Checkout Pesanan Sukses (POST)
**Endpoint**: `http://localhost:8080/api/v1/checkout`  
**Header**: `Content-Type: application/json`  
**Body**:
```json
{
  "idPelanggan": "CUST-001",
  "metodePembayaran": "CREDIT_CARD",
  "tokenKartu": "tok_visa_valid_999",
  "keranjangData": [
    {
      "idMenu": "MENU-01",
      "jumlah": 2
    },
    {
      "idMenu": "MENU-02",
      "jumlah": 1
    }
  ]
}
```

### 2. Uji Coba Stok Tidak Cukup (POST)
Kirim jumlah pembelian melebihi stok yang ada (misal `100`):
```json
{
  "idPelanggan": "CUST-001",
  "metodePembayaran": "CREDIT_CARD",
  "tokenKartu": "tok_visa_valid_999",
  "keranjangData": [
    {
      "idMenu": "MENU-02",
      "jumlah": 100
    }
  ]
}
```

### 3. Pembatalan Transaksi (POST)
**Endpoint**: `http://localhost:8080/api/v1/checkout/ORD-XXXXX/batal`  
**Body**:
```json
{
  "alasan": "Pelanggan ingin mengubah pesanan"
}
```

---

## 📤 Cara Push Proyek ke GitHub

```bash
git init
git add .
git commit -m "feat: Initial commit Ordering System with BCE architecture and H2 DB"
git branch -M main
git remote add origin https://github.com/USERNAME/ordering-system.git
git push -u origin main
```
