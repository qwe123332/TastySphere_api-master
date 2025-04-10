package com.example.tastysphere_api.enums;

public class Enums {
    public enum UserStatus {
        ACTIVE, INACTIVE, BANNED
    }

    public enum MerchantStatus {
        PENDING, ACTIVE, SUSPENDED
    }

    public enum ProductStatus {
        ACTIVE, INACTIVE, OUT_OF_STOCK
    }

    public enum OrderStatus {
        PENDING, CONFIRMED, CANCELLED, COMPLETED
    }

    public enum PaymentStatus {
        UNPAID, PAID, REFUNDED
    }

    public enum PaymentMethod {
        CASH, CARD, ONLINE
    }

    public enum DeliveryStatus {
        PENDING, IN_PROGRESS, DELIVERED
    }

    public enum PostVisibility {
        PUBLIC, PRIVATE, FRIENDS_ONLY
    }
}