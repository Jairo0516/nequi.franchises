package com.nequi.franchises.infrastructure.rest.dto;

import jakarta.validation.constraints.NotBlank;

public record CreateBranchRequest(
    @NotBlank(message = "name is required")
    String name
) {}
