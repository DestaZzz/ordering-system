package com.example.orderingsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "pembayaran")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Pembayaran {

    @Id
    @Column(name = "id_pembayaran", length = 36)
    private String idPembayaran;

    @Column(name = "jumlah_bayar", nullable = false)
    private double jumlahBayar;

    @Column(nullable = false, length = 50)
    private String metode;

    @Column(name = "status_bayar", nullable = false, length = 50)
    private String statusBayar;

    @Column(name = "kode_referensi", length = 100)
    private String kodeReferensi;

    @OneToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "id_pesanan", nullable = false, unique = true)
    private Pesanan pesanan;

    // Domain Method
    public boolean prosesVerifikasi() {
        return "SUCCESS".equalsIgnoreCase(this.statusBayar)
                && this.kodeReferensi != null
                && !this.kodeReferensi.trim().isEmpty();
    }
}
