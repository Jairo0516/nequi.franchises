package com.nequi.franchises.infrastructure.persistence.jpa.mapper;

import com.nequi.franchises.domain.model.Product;
import com.nequi.franchises.infrastructure.persistence.jpa.entity.ProductEntity;

public final class ProductJpaMapper {

    private ProductJpaMapper() {
    }

    public static ProductEntity toEntity(Product d) {
        ProductEntity e = new ProductEntity(d.id(), d.branchId(), d.name(), d.stock());
        e.setVersion(d.version());
        e.setDeleted(d.deleted());
        e.setDeletedAt(d.deletedAt());
        return e;
    }

    public static Product toDomain(ProductEntity e) {
        return new Product(
                e.getId(),
                e.getBranchId(),
                e.getName(),
                e.getStock(),
                e.getVersion(),
                e.isDeleted(),
                e.getDeletedAt()
        );
    }

}
