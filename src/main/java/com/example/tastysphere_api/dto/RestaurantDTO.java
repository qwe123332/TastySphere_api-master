package com.example.tastysphere_api.dto;

import lombok.Data;

@Data
public class RestaurantDTO {

    private Long id;
    private String name;
    private String address;
    private String phoneNumber;
    private String website;
    private String description;
    private Double rating;
    private Integer reviewCount;

    // Constructors, getters, and setters




}
