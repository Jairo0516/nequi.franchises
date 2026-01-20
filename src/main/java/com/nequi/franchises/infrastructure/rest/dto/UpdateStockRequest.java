package com.nequi.franchises.infrastructure.rest.dto;

import jakarta.validation.constraints.PositiveOrZero;

public record UpdateStockRequest(
    @PositiveOrZero(message = "stock must be >= 0")
    int stock
) {}
