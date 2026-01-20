package com.nequi.franchises.infrastructure.rest.dto;

import java.util.UUID;

public record TopProductByBranchResponse(
    UUID branchId,
    String branchName,
    UUID productId,
    String productName,
    int stock
) {}
