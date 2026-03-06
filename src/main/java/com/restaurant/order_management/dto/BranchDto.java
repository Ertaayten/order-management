package com.restaurant.order_management.dto;

import lombok.*;

@Getter
@Setter
@NoArgsConstructor
@AllArgsConstructor
@Builder
public class BranchDto {

    private Long id;
    private String name;
    private String phone;
    private String email;
    private AddressDto address;
}
