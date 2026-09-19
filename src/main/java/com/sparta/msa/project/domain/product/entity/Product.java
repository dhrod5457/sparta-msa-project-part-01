package com.sparta.msa.project.domain.product.entity;

import com.sparta.msa.project.domain.category.entity.Category;
import com.sparta.msa.project.global.entity.BaseEntity;
import com.sparta.msa.project.global.exception.DomainException;
import com.sparta.msa.project.global.exception.DomainExceptionCode;
import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.FetchType;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "products")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Product extends BaseEntity {

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

  @Enumerated(EnumType.STRING)
  @Column(nullable = false, length = 30)
  private ProductStatus status;

  @ManyToOne(fetch = FetchType.LAZY, optional = false)
  @JoinColumn(name = "category_id", nullable = false)
  private Category category;

  @Getter(AccessLevel.NONE)
  @OneToMany(
      mappedBy = "product",
      fetch = FetchType.LAZY,
      cascade = CascadeType.ALL,
      orphanRemoval = true
  )
  private List<ProductOption> options = new ArrayList<>();

  private Product(String name, Long price, String description, Category category) {
    validateName(name);
    validatePrice(price);
    validateCategory(category);

    this.name = name;
    this.price = price;
    this.description = description;
    this.category = category;
    this.status = ProductStatus.STOP_SALE;
  }

  public static Product create(String name, Long price, String description, Category category) {
    return new Product(name, price, description, category);
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

  public void changeCategory(Category category) {
    validateCategory(category);
    this.category = category;
  }

  public void startSale() {
    this.status = hasAvailableStock()
        ? ProductStatus.FOR_SALE
        : ProductStatus.OUT_OF_STOCK;
  }

  public void stopSale() {
    this.status = ProductStatus.STOP_SALE;
  }

  public ProductOption addOption(String name, Long additionalPrice, Integer stock) {
    validateDuplicateOptionName(name);

    ProductOption option = ProductOption.create(this, name, additionalPrice, stock);
    options.add(option);
    synchronizeSaleStatusWithStock();
    return option;
  }

  public void changeOptionName(ProductOption option, String name) {
    ProductOption ownedOption = findOwnedOption(option);
    validateDuplicateOptionName(name, ownedOption);
    ownedOption.changeName(name);
  }

  public void changeOptionAdditionalPrice(ProductOption option, Long additionalPrice) {
    findOwnedOption(option).changeAdditionalPrice(additionalPrice);
  }

  public void changeOptionStock(ProductOption option, Integer stock) {
    findOwnedOption(option).changeStock(stock);
    synchronizeSaleStatusWithStock();
  }

  public void removeOption(ProductOption option) {
    ProductOption ownedOption = findOwnedOption(option);

    options.remove(ownedOption);
    ownedOption.detachFromProduct();

    synchronizeSaleStatusWithStock();
  }

  public List<ProductOption> getOptions() {
    return Collections.unmodifiableList(options);
  }

  private void synchronizeSaleStatusWithStock() {
    if (status == ProductStatus.STOP_SALE) {
      return;
    }

    this.status = hasAvailableStock()
        ? ProductStatus.FOR_SALE
        : ProductStatus.OUT_OF_STOCK;
  }

  private boolean hasAvailableStock() {
    return options.stream()
        .anyMatch(option -> option.getStock() > 0);
  }

  private void validateDuplicateOptionName(String name) {
    validateDuplicateOptionName(name, null);
  }

  private void validateDuplicateOptionName(String name, ProductOption excludedOption) {
    boolean duplicated = options.stream()
        .filter(option -> option != excludedOption)
        .anyMatch(option -> option.getName().equals(name));

    if (duplicated) {
      throw new DomainException(DomainExceptionCode.PRODUCT_OPTION_NAME_DUPLICATED);
    }
  }

  // 같은 id 를 가진 detached 객체를 넘겨받아도 컬렉션이 들고 있는 인스턴스를 바꾸도록 그것을 찾아 돌려준다
  private ProductOption findOwnedOption(ProductOption option) {
    if (option == null) {
      throw new DomainException(DomainExceptionCode.PRODUCT_OPTION_NOT_FOUND);
    }

    return options.stream()
        .filter(ownedOption -> ownedOption.equals(option))
        .findFirst()
        .orElseThrow(() -> new DomainException(DomainExceptionCode.PRODUCT_OPTION_NOT_FOUND));
  }

  private static void validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new DomainException(DomainExceptionCode.PRODUCT_NAME_REQUIRED);
    }

    if (name.length() > MAX_NAME_LENGTH) {
      throw new DomainException(DomainExceptionCode.PRODUCT_NAME_TOO_LONG);
    }
  }

  private static void validatePrice(Long price) {
    if (price == null) {
      throw new DomainException(DomainExceptionCode.PRODUCT_PRICE_REQUIRED);
    }

    if (price < 0) {
      throw new DomainException(DomainExceptionCode.PRODUCT_PRICE_NEGATIVE);
    }
  }

  private static void validateCategory(Category category) {
    if (category == null) {
      throw new DomainException(DomainExceptionCode.PRODUCT_CATEGORY_REQUIRED);
    }
  }
}
