package com.sparta.msa.project.domain.category.entity;

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
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.ArrayList;
import java.util.Collections;
import java.util.HashSet;
import java.util.List;
import java.util.Objects;
import java.util.Set;
import lombok.AccessLevel;
import lombok.Getter;
import lombok.NoArgsConstructor;

@Getter
@Entity
@Table(name = "categories")
@NoArgsConstructor(access = AccessLevel.PROTECTED)
public class Category extends BaseEntity {

  private static final int MAX_NAME_LENGTH = 100;

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  private Long id;

  @Column(nullable = false, length = MAX_NAME_LENGTH)
  private String name;

  @ManyToOne(fetch = FetchType.LAZY)
  @JoinColumn(name = "parent_id")
  private Category parent;

  @Getter(AccessLevel.NONE)
  @OneToMany(mappedBy = "parent", fetch = FetchType.LAZY)
  private List<Category> children = new ArrayList<>();

  private Category(String name) {
    validateName(name);
    this.name = name;
  }

  public static Category create(String name) {
    return new Category(name);
  }

  public void changeName(String name) {
    validateName(name);
    this.name = name;
  }

  public void addChild(Category child) {
    if (child == null) {
      throw new DomainException(DomainExceptionCode.CATEGORY_CHILD_REQUIRED);
    }

    validateHierarchy(child);

    Category currentParent = child.getParent();
    if (isSameCategory(currentParent)) {
      if (!children.contains(child)) {
        children.add(child);
      }
      return;
    }

    if (currentParent != null) {
      currentParent.detachChild(child);
    }

    child.attachTo(this);
    children.add(child);
  }

  public List<Category> getChildren() {
    return Collections.unmodifiableList(children);
  }

  // 지연 로딩 프록시는 메서드 호출만 실제 엔티티로 넘긴다.
  // 다른 Category 의 상태를 바꿀 때 필드로 바로 건드리지 않고 이 둘을 거친다
  protected void detachChild(Category child) {
    children.remove(child);
  }

  protected void attachTo(Category parent) {
    this.parent = parent;
  }

  private void validateHierarchy(Category child) {
    // 같은 행을 다시 불러온 인스턴스도 자기 자신이므로 동일성만으로 가르지 않는다
    if (isSameCategory(child)) {
      throw new DomainException(DomainExceptionCode.CATEGORY_SELF_REFERENCE);
    }

    // 부모를 따라 위로 올라가는데, 이미 순환이 있는 계층이면 그 순회가 끝나지 않는다.
    // 지나온 노드를 기억해 두고 다시 만나면 끊는다
    Set<Category> visited = new HashSet<>();
    visited.add(this);

    // 위로 올라가며 만나는 부모는 지연 로딩 프록시다. 필드로 바로 읽으면 빈 값을 보게 되므로 getter 로 읽는다
    Category current = getParent();
    while (current != null) {
      if (current.equals(child) || !visited.add(current)) {
        throw new DomainException(DomainExceptionCode.CATEGORY_CYCLE_NOT_ALLOWED);
      }
      current = current.getParent();
    }
  }

  private boolean isSameCategory(Category other) {
    return other == this || equals(other);
  }

  @Override
  public boolean equals(Object object) {
    if (this == object) {
      return true;
    }

    if (!(object instanceof Category other)) {
      return false;
    }

    Long thisId = getId();
    return thisId != null && Objects.equals(thisId, other.getId());
  }

  @Override
  public int hashCode() {
    return Category.class.hashCode();
  }

  private static void validateName(String name) {
    if (name == null || name.isBlank()) {
      throw new DomainException(DomainExceptionCode.CATEGORY_NAME_REQUIRED);
    }

    if (name.length() > MAX_NAME_LENGTH) {
      throw new DomainException(DomainExceptionCode.CATEGORY_NAME_TOO_LONG);
    }
  }
}
