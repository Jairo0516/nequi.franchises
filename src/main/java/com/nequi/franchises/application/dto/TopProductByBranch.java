package com.nequi.franchises.application.dto;

import java.util.UUID;

public record TopProductByBranch(
        UUID branchId,
        String branchName,
        UUID productId,
        String productName,
        int stock
) {
}
