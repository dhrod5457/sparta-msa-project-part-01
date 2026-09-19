package com.sparta.msa.project.domain.product.entity;

import static org.assertj.core.api.Assertions.assertThat;
import static org.assertj.core.api.Assertions.assertThatThrownBy;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;

class ProductTest {

  @Test
  @DisplayName("정상적인 상품을 생성할 수 있다")
  void createProduct() {
    Product product = Product.create("옥스포드 셔츠", 89_000L, "클래식 옥스포드 셔츠");

    assertThat(product.getName()).isEqualTo("옥스포드 셔츠");
    assertThat(product.getPrice()).isEqualTo(89_000L);
    assertThat(product.getDescription()).isEqualTo("클래식 옥스포드 셔츠");
  }

  @Test
  @DisplayName("상품명은 null일 수 없다")
  void createProductWithNullName() {
    assertThatThrownBy(() -> Product.create(null, 10_000L, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("상품명은 필수입니다.");
  }

  @Test
  @DisplayName("상품명은 공백일 수 없다")
  void createProductWithBlankName() {
    assertThatThrownBy(() -> Product.create("   ", 10_000L, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("상품명은 필수입니다.");
  }

  @Test
  @DisplayName("상품명은 200자를 초과할 수 없다")
  void createProductWithTooLongName() {
    String tooLongName = "a".repeat(201);

    assertThatThrownBy(() -> Product.create(tooLongName, 10_000L, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("상품명은 200자를 초과할 수 없습니다.");
  }

  @Test
  @DisplayName("가격은 0원도 허용한다")
  void createFreeProduct() {
    Product product = Product.create("무료 상품", 0L, null);

    assertThat(product.getPrice()).isZero();
  }

  @Test
  @DisplayName("상품 가격은 음수일 수 없다")
  void createProductWithNegativePrice() {
    assertThatThrownBy(() -> Product.create("상품", -1L, null))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("상품 가격은 0 이상이어야 합니다.");
  }

  @Test
  @DisplayName("상품명을 변경할 수 있다")
  void changeName() {
    Product product = Product.create("기존 상품명", 10_000L, null);

    product.changeName("변경 상품명");

    assertThat(product.getName()).isEqualTo("변경 상품명");
  }

  @Test
  @DisplayName("잘못된 상품명으로 변경할 수 없다")
  void changeNameWithInvalidName() {
    Product product = Product.create("상품", 10_000L, null);

    assertThatThrownBy(() -> product.changeName(" "))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("상품명은 필수입니다.");
  }

  @Test
  @DisplayName("상품 가격을 변경할 수 있다")
  void changePrice() {
    Product product = Product.create("상품", 10_000L, null);

    product.changePrice(20_000L);

    assertThat(product.getPrice()).isEqualTo(20_000L);
  }

  @Test
  @DisplayName("상품 가격을 음수로 변경할 수 없다")
  void changePriceWithNegativePrice() {
    Product product = Product.create("상품", 10_000L, null);

    assertThatThrownBy(() -> product.changePrice(-1L))
        .isInstanceOf(IllegalArgumentException.class)
        .hasMessage("상품 가격은 0 이상이어야 합니다.");
  }

  @Test
  @DisplayName("상품 설명을 변경할 수 있다")
  void changeDescription() {
    Product product = Product.create("상품", 10_000L, "기존 설명");

    product.changeDescription("변경 설명");

    assertThat(product.getDescription()).isEqualTo("변경 설명");
  }
}
