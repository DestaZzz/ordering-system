package com.example.orderingsystem.entity;

import jakarta.persistence.*;
import lombok.*;

@Entity
@Table(name = "item_pesanan")
@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class ItemPesanan {

    @Id
    @Column(name = "id_item", length = 36)
    private String idItem;

    @Column(nullable = false)
    private int jumlah;

    @Column(nullable = false)
    private double subtotal;

    // Komposisi: Terikat erat ke Pesanan
    @ManyToOne(fetch = FetchType.LAZY, optional = false)
    @JoinColumn(name = "id_pesanan", nullable = false)
    private Pesanan pesanan;

    // Agregasi: Merujuk ke Menu
    @ManyToOne(fetch = FetchType.EAGER, optional = false)
    @JoinColumn(name = "id_menu", nullable = false)
    private Menu menu;

    // Domain Method
    public double hitungSubtotal() {
        if (this.menu != null) {
            this.subtotal = this.menu.getHarga() * this.jumlah;
            return this.subtotal;
        }
        return 0.0;
    }
}
