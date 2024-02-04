package com.ra.model;
import com.fasterxml.jackson.annotation.JsonIgnore;
import jakarta.persistence.*;
import lombok.*;
import java.util.Date;
import java.util.Set;

@Entity
@Getter
@Setter
@AllArgsConstructor
@NoArgsConstructor
@Builder
@ToString
public class Orders {
    @Id
    private String orderCode;
    private Date createDate;
    private OrderStatus orderStatus;
    private String providerName;
    private String providerPhone;
    private String providerAddress;
    private String providerEmail;

    private String receiverName;
    private String receiverPhone;
    private String receiverAddress;
    private String receiverEmail;

    private Double providerLatitude;
    private Double providerLongitude;
    private Double receiverLatitude;
    private Double receiverLongitude;

    @ManyToOne
    @JoinColumn(name = "warehouse_code")
    private Warehouse warehouse;

    @ManyToOne
    @JoinColumn(name = "user_id")
    private Users users;
    private Integer failCount;

    @OneToMany
    @JoinColumn(name = "order_code")
    private Set<OrdersHistory> ordersHistories;
}
