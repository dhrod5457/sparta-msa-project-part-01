package com.sparta.msa.project.global.exception;

import lombok.AccessLevel;
import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.experimental.FieldDefaults;
import org.springframework.http.HttpStatus;

@Getter
@AllArgsConstructor
@FieldDefaults(level = AccessLevel.PRIVATE)
public enum DomainExceptionCode {

  INVALID_TOKEN(HttpStatus.UNAUTHORIZED, "잘못된 토큰입니다."),
  EXPIRED_TOKEN(HttpStatus.UNAUTHORIZED, "만료된 토큰입니다."),
  MISSING_TOKEN(HttpStatus.UNAUTHORIZED, "토큰이 누락되었습니다."),
  UNAUTHORIZED_ACCESS(HttpStatus.UNAUTHORIZED, "인증되지 않은 접근입니다."),
  JSON_PROCESSING_ERROR(HttpStatus.INTERNAL_SERVER_ERROR, "Json 데이터 처리 중 에러가 발생하였습니다."),

  PRODUCT_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "상품명은 필수입니다."),
  PRODUCT_NAME_TOO_LONG(HttpStatus.BAD_REQUEST, "상품명은 200자를 초과할 수 없습니다."),
  PRODUCT_PRICE_REQUIRED(HttpStatus.BAD_REQUEST, "상품 가격은 필수입니다."),
  PRODUCT_PRICE_NEGATIVE(HttpStatus.BAD_REQUEST, "상품 가격은 0 이상이어야 합니다."),
  PRODUCT_CATEGORY_REQUIRED(HttpStatus.BAD_REQUEST, "상품 카테고리는 필수입니다."),

  PRODUCT_OPTION_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "옵션명은 필수입니다."),
  PRODUCT_OPTION_NAME_TOO_LONG(HttpStatus.BAD_REQUEST, "옵션명은 100자를 초과할 수 없습니다."),
  PRODUCT_OPTION_ADDITIONAL_PRICE_REQUIRED(HttpStatus.BAD_REQUEST, "옵션 추가 금액은 필수입니다."),
  PRODUCT_OPTION_ADDITIONAL_PRICE_NEGATIVE(HttpStatus.BAD_REQUEST, "옵션 추가 금액은 0 이상이어야 합니다."),
  PRODUCT_OPTION_STOCK_REQUIRED(HttpStatus.BAD_REQUEST, "옵션 재고량은 필수입니다."),
  PRODUCT_OPTION_STOCK_NEGATIVE(HttpStatus.BAD_REQUEST, "옵션 재고량은 0 이상이어야 합니다."),
  PRODUCT_OPTION_NAME_DUPLICATED(HttpStatus.BAD_REQUEST, "동일한 옵션명이 이미 존재합니다."),
  PRODUCT_OPTION_NOT_FOUND(HttpStatus.BAD_REQUEST, "상품에 존재하지 않는 옵션입니다."),

  CATEGORY_NAME_REQUIRED(HttpStatus.BAD_REQUEST, "카테고리명은 필수입니다."),
  CATEGORY_NAME_TOO_LONG(HttpStatus.BAD_REQUEST, "카테고리명은 100자를 초과할 수 없습니다."),
  CATEGORY_CHILD_REQUIRED(HttpStatus.BAD_REQUEST, "자식 카테고리는 필수입니다."),
  CATEGORY_SELF_REFERENCE(HttpStatus.BAD_REQUEST, "카테고리는 자기 자신을 자식으로 등록할 수 없습니다."),
  CATEGORY_CYCLE_NOT_ALLOWED(HttpStatus.BAD_REQUEST, "카테고리 계층에는 순환 관계를 만들 수 없습니다.");

  final HttpStatus status;
  final String message;
}
