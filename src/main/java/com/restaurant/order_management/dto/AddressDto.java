package com.restaurant.order_management.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class AddressDto {

    private Long id;
    private String street;
    private String city;
    private String state;
    private String country;
    private String zipCode;
}
