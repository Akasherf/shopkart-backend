package com.ecommerce_backend.dto;

import lombok.Getter;
import lombok.Setter;

@Getter
@Setter
public class AdminOrderDashboardResponse {

    private long total;
    private long placed;
    private long confirmed;
    private long shipped;
    private long delivered;
    private long cancelled;
}
