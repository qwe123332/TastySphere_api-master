package com.example.tastysphere_api.controller;

public class GrantedAuthority {
    private String authority;

    public GrantedAuthority(String authority) {
        this.authority = authority;
    }

    public String getAuthority() {
        return authority;
    }
}
