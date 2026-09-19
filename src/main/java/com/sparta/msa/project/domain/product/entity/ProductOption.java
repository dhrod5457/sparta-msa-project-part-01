package com.sparta.msa.project.domain.product.entity;

import com.sparta.msa.project.global.entity.BaseEntity;
import com.sparta.msa.project.global.exception.DomainException;
import com.sparta.msa.project.global.exception.DomainExceptionCode;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import java.util.Objects;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "product_options")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class ProductOption extends BaseEntity {

  private static final int MAX_NAME_LENGTH = 100;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "product_id", nullable = false)
  private Product product;

  @Column(nullable = false, length = MAX_NAME_LENGTH)
  private String name;

  @Column(name = "additional_price", nullable = false)
  private Long additionalPrice;

  @Column(nullable = false)
  private Integer stock;

  private ProductOption(Product product, String name, Long additionalPrice, Integer stock) {
    validateName(name);
    validateAdditionalPrice(additionalPrice);
    validateStock(stock);

    this.product = product;
    this.name = name;
    this.additionalPrice = additionalPrice;
    this.stock = stock;
  }

  static ProductOption create(Product product, String name, Long additionalPrice, Integer stock) {
    return new ProductOption(product, name, additionalPrice, stock);
  }

  void changeName(String name) {
    validateName(name);
    this.name = name;
  }

  void changeAdditionalPrice(Long additionalPrice) {
    validateAdditionalPrice(additionalPrice);
    this.additionalPrice = additionalPrice;
  }

  void changeStock(Integer stock) {
    validateStock(stock);
    this.stock = stock;
  }

  void detachFromProduct() {
    this.product = null;
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }

    if (!(object instanceof ProductOption other)) {
      return false;
    }

    Long thisId = getId();
    return thisId != null && Objects.equals(thisId, other.getId());
  }

  @Override
  public int hashCode() {
    return ProductOption.class.hashCode();
  }

  private static void validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new DomainException(DomainExceptionCode.PRODUCT_OPTION_NAME_REQUIRED);
    }

    if (name.length() > MAX_NAME_LENGTH) {
      throw new DomainException(DomainExceptionCode.PRODUCT_OPTION_NAME_TOO_LONG);
    }
  }

  private static void validateAdditionalPrice(Long additionalPrice) {
    if (additionalPrice == null) {
      throw new DomainException(DomainExceptionCode.PRODUCT_OPTION_ADDITIONAL_PRICE_REQUIRED);
    }

    if (additionalPrice < 0) {
      throw new DomainException(DomainExceptionCode.PRODUCT_OPTION_ADDITIONAL_PRICE_NEGATIVE);
    }
  }

  private static void validateStock(Integer stock) {
    if (stock == null) {
      throw new DomainException(DomainExceptionCode.PRODUCT_OPTION_STOCK_REQUIRED);
    }

    if (stock < 0) {
      throw new DomainException(DomainExceptionCode.PRODUCT_OPTION_STOCK_NEGATIVE);
    }
  }
}
