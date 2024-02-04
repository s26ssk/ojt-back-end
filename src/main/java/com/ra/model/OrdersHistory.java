package com.ra.model;

import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;

import java.util.Date;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
public class OrdersHistory {
    @Id
    @GeneratedValue(strategy = GenerationType.IDENTITY)
    private Long hid;
    @Column(name = "order_code", insertable = false, updatable = false)
    private String orderCode;
    @ManyToOne
    @JoinColumn(name = "uid")
    @JsonIgnore
    private Users user;
    @ManyToOne
    @JoinColumn(name = "order_code", referencedColumnName = "orderCode")
    @JsonIgnore
    private Orders orders;
    private OrderStatus status;
    private Date updateAt;
    private String comment;
}
