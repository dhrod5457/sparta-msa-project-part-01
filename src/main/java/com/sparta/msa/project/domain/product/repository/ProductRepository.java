package com.sparta.msa.project.domain.product.repository;

import com.sparta.msa.project.domain.product.entity.Product;
import org.springframework.data.jpa.repository.JpaRepository;

public interface ProductRepository extends JpaRepository<Product, Long> {
}
