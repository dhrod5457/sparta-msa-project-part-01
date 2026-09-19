package com.sparta.msa.project.domain.product.entity;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EntityListeners;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.Table;
import java.time.LocalDateTime;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;
import org.springframework.data.annotation.CreatedDate;
import org.springframework.data.annotation.LastModifiedDate;
import org.springframework.data.jpa.domain.support.AuditingEntityListener;

@Getter
@Entity
@Table(name = "products")
@EntityListeners(AuditingEntityListener.class)
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product {

  private static final int MAX_NAME_LENGTH = 200;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = MAX_NAME_LENGTH)
  private String name;

  @Column(nullable = false)
  private Long price;

  @Column(columnDefinition = "TEXT")
  private String description;

  @CreatedDate
  @Column(name = "created_at", nullable = false, updatable = false)
  private LocalDateTime createdAt;

  @LastModifiedDate
  @Column(name = "updated_at", nullable = false)
  private LocalDateTime updatedAt;

  private Product(String name, Long price, String description) {
    validateName(name);
    validatePrice(price);

    this.name = name;
    this.price = price;
    this.description = description;
  }

  public static Product create(String name, Long price, String description) {
    return new Product(name, price, description);
  }

  public void changeName(String name) {
    validateName(name);
    this.name = name;
  }

  public void changePrice(Long price) {
    validatePrice(price);
    this.price = price;
  }

  public void changeDescription(String description) {
    this.description = description;
  }

  private static void validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new IllegalArgumentException("상품명은 필수입니다.");
    }

    if (name.length() > MAX_NAME_LENGTH) {
      throw new IllegalArgumentException("상품명은 200자를 초과할 수 없습니다.");
    }
  }

  private static void validatePrice(Long price) {
    if (price == null) {
      throw new IllegalArgumentException("상품 가격은 필수입니다.");
    }

    if (price < 0) {
      throw new IllegalArgumentException("상품 가격은 0 이상이어야 합니다.");
    }
  }
}
