package com.nequi.franchises.infrastructure.rest.dto;

import io.swagger.v3.oas.annotations.media.Schema;

import java.util.UUID;

public record TopProductByBranchResponse(
        @Schema(example = "11111111-1111-1111-1111-111111111111")
        UUID branchId,

        @Schema(example = "Sucursal Centro")
        String branchName,

        @Schema(example = "22222222-2222-2222-2222-222222222222")
        UUID productId,

        @Schema(example = "CocaCola")
        String productName,

        @Schema(example = "25")
        int stock
) {
}
