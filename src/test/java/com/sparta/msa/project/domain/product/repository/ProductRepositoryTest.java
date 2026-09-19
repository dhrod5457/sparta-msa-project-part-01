package com.sparta.msa.project.domain.product.repository;

import static org.assertj.core.api.Assertions.assertThat;

import com.sparta.msa.project.domain.product.entity.Product;
import com.sparta.msa.project.global.config.JpaAuditingConfig;
import jakarta.persistence.EntityManager;
import java.time.LocalDateTime;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.jdbc.AutoConfigureTestDatabase;
import org.springframework.boot.test.autoconfigure.orm.jpa.DataJpaTest;
import org.springframework.context.annotation.Import;

@DataJpaTest
@AutoConfigureTestDatabase(replace = AutoConfigureTestDatabase.Replace.NONE)
@Import(JpaAuditingConfig.class)
class ProductRepositoryTest {

  @Autowired
  private ProductRepository productRepository;

  @Autowired
  private EntityManager entityManager;

  @Test
  @DisplayName("상품을 저장하고 조회할 수 있으며 생성/수정 시간이 자동 기록된다")
  void saveAndFindProduct() {
    Product product = Product.create("옥스포드 셔츠", 89_000L, "클래식 옥스포드 셔츠");

    Product savedProduct = productRepository.saveAndFlush(product);
    entityManager.clear();

    Product foundProduct = productRepository.findById(savedProduct.getId()).orElseThrow();

    assertThat(savedProduct.getId()).isNotNull();
    assertThat(foundProduct.getName()).isEqualTo("옥스포드 셔츠");
    assertThat(foundProduct.getPrice()).isEqualTo(89_000L);
    assertThat(foundProduct.getDescription()).isEqualTo("클래식 옥스포드 셔츠");
    assertThat(foundProduct.getCreatedAt()).isNotNull();
    assertThat(foundProduct.getUpdatedAt()).isNotNull();
  }

  @Test
  @DisplayName("상품을 수정하면 updatedAt이 갱신된다")
  void updateProductUpdatesModifiedTime() throws InterruptedException {
    Product product = productRepository.saveAndFlush(
        Product.create("옥스포드 셔츠", 89_000L, "클래식 옥스포드 셔츠")
    );
    LocalDateTime previousUpdatedAt = product.getUpdatedAt();

    Thread.sleep(10L);

    product.changePrice(79_000L);
    productRepository.flush();
    entityManager.clear();

    Product updatedProduct = productRepository.findById(product.getId()).orElseThrow();

    assertThat(updatedProduct.getPrice()).isEqualTo(79_000L);
    assertThat(updatedProduct.getUpdatedAt()).isAfter(previousUpdatedAt);
  }
}
