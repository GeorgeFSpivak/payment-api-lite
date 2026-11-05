package com.paymentapilite.domain.user;


import java.math.BigDecimal;

public record UserResponseDTO(
        Long id,
        String fullName,
        UserType userType,
        BigDecimal balance
) {
}
