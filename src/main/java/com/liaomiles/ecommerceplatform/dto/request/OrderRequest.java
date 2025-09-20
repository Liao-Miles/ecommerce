package com.liaomiles.ecommerceplatform.dto.request;

import java.math.BigDecimal;

public class OrderRequest {
    public Long userId;
    public BigDecimal totalAmount;
    public String shippingName;
    public String shippingPhone;
    public String shippingAddress;
    public String shippingNote;
    public String shippingMethod;
    // 其他欄位如有需要可再擴充
}
