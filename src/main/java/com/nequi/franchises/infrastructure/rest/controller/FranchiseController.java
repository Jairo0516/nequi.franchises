package com.nequi.franchises.infrastructure.rest.controller;

import com.nequi.franchises.application.dto.TopProductByBranch;
import com.nequi.franchises.application.usecases.FranchiseUseCases;
import com.nequi.franchises.domain.model.Branch;
import com.nequi.franchises.domain.model.Franchise;
import com.nequi.franchises.domain.model.Product;
import com.nequi.franchises.infrastructure.rest.dto.BranchResponse;
import com.nequi.franchises.infrastructure.rest.dto.CreateBranchRequest;
import com.nequi.franchises.infrastructure.rest.dto.CreateFranchiseRequest;
import com.nequi.franchises.infrastructure.rest.dto.CreateProductRequest;
import com.nequi.franchises.infrastructure.rest.dto.FranchiseResponse;
import com.nequi.franchises.infrastructure.rest.dto.ProductResponse;
import com.nequi.franchises.infrastructure.rest.dto.TopProductByBranchResponse;
import com.nequi.franchises.infrastructure.rest.dto.UpdateNameRequest;
import com.nequi.franchises.infrastructure.rest.dto.UpdateStockRequest;
import jakarta.validation.Valid;
import org.springframework.http.HttpStatus;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.DeleteMapping;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.PatchMapping;
import org.springframework.web.bind.annotation.PathVariable;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

import java.util.List;
import java.util.UUID;

@RestController
@RequestMapping("/api/franchises")
public class FranchiseController {

    private final FranchiseUseCases useCases;

    public FranchiseController(FranchiseUseCases useCases) {
        this.useCases = useCases;
    }

    // 1) Create franchise
    @PostMapping
    public ResponseEntity<FranchiseResponse> createFranchise(@Valid @RequestBody CreateFranchiseRequest request) {
        Franchise created = useCases.createFranchise(request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    // Extra) Rename franchise
    @PatchMapping("/{franchiseId}")
    public FranchiseResponse renameFranchise(
            @PathVariable UUID franchiseId,
            @Valid @RequestBody UpdateNameRequest request
    ) {
        Franchise updated = useCases.renameFranchise(franchiseId, request.name());
        return toResponse(updated);
    }

    // 2) Add branch to franchise
    @PostMapping("/{franchiseId}/branches")
    public ResponseEntity<BranchResponse> addBranch(
            @PathVariable UUID franchiseId,
            @Valid @RequestBody CreateBranchRequest request
    ) {
        Branch created = useCases.addBranch(franchiseId, request.name());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    // Extra) Rename branch
    @PatchMapping("/{franchiseId}/branches/{branchId}")
    public BranchResponse renameBranch(
            @PathVariable UUID franchiseId,
            @PathVariable UUID branchId,
            @Valid @RequestBody UpdateNameRequest request
    ) {
        Branch updated = useCases.renameBranch(franchiseId, branchId, request.name());
        return toResponse(updated);
    }

    // 3) Add product to branch (under franchise)
    @PostMapping("/{franchiseId}/branches/{branchId}/products")
    public ResponseEntity<ProductResponse> addProduct(
            @PathVariable UUID franchiseId,
            @PathVariable UUID branchId,
            @Valid @RequestBody CreateProductRequest request
    ) {
        Product created = useCases.addProduct(franchiseId, branchId, request.name(), request.stock());
        return ResponseEntity.status(HttpStatus.CREATED).body(toResponse(created));
    }

    // Extra) Rename product
    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}")
    public ProductResponse renameProduct(
            @PathVariable UUID franchiseId,
            @PathVariable UUID branchId,
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateNameRequest request
    ) {
        Product updated = useCases.renameProduct(franchiseId, branchId, productId, request.name());
        return toResponse(updated);
    }

    // 4) Remove product from branch
    @DeleteMapping("/{franchiseId}/branches/{branchId}/products/{productId}")
    public ResponseEntity<Object> removeProduct(
            @PathVariable UUID franchiseId,
            @PathVariable UUID branchId,
            @PathVariable UUID productId
    ) {
        useCases.removeProduct(franchiseId, branchId, productId);
        return ResponseEntity.ok(null);
    }

    // 5) Update stock
    @PatchMapping("/{franchiseId}/branches/{branchId}/products/{productId}/stock")
    public ProductResponse updateStock(
            @PathVariable UUID franchiseId,
            @PathVariable UUID branchId,
            @PathVariable UUID productId,
            @Valid @RequestBody UpdateStockRequest request
    ) {
        Product updated = useCases.updateProductStock(franchiseId, branchId, productId, request.stock());
        return toResponse(updated);
    }

    // 6) Top product by branch for a franchise
    @GetMapping("/{franchiseId}/top-products-by-branch")
    public ResponseEntity<Object> topProductsByBranch(@PathVariable UUID franchiseId) {
        return ResponseEntity.ok(useCases.getTopProductsByBranch(franchiseId));
    }

    private FranchiseResponse toResponse(Franchise franchise) {
        return new FranchiseResponse(franchise.id(), franchise.name());
    }

    private BranchResponse toResponse(Branch branch) {
        return new BranchResponse(branch.id(), branch.franchiseId(), branch.name());
    }

    private ProductResponse toResponse(Product product) {
        return new ProductResponse(product.id(), product.branchId(), product.name(), product.stock());
    }
}
