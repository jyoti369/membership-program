package com.firstclub.membership.entity;

import jakarta.persistence.*;
import lombok.*;
import java.math.BigDecimal;
import java.time.LocalDateTime;

@Entity
@Table(name = "orders")
@Data
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class Order {

    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long id;

    @ManyToOne(fetch = FetchType.LAZY)
    @JoinColumn(name = "user_id", nullable = false)
    @ToString.Exclude
    private User user;

    @Column(nullable = false, precision = 10, scale = 2)
    private BigDecimal orderValue;

    @Column(name = "order_date", nullable = false)
    private LocalDateTime orderDate;

    @Column
    private String category;

    @Column(nullable = false)
    private Boolean freeDeliveryApplied = false;

    @Column(precision = 5, scale = 2)
    private BigDecimal discountPercentage;

    @Column(precision = 10, scale = 2)
    private BigDecimal discountAmount;

    @PrePersist
    protected void onCreate() {
        if (orderDate == null) {
            orderDate = LocalDateTime.now();
        }
    }
}
