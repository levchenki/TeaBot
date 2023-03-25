package com.uqii.teabot.models;

import jakarta.persistence.CascadeType;
import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Min;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "teas")
@NoArgsConstructor
public class Tea {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "tea_id", nullable = false)
  private Long id;

  @Column(name = "name")
  private String name;

  @Column(name = "description")
  private String description;

  @Column(name = "price")
  @Min(value = 1)
  private int price;

  @Column(name = "category")
  @Enumerated(EnumType.STRING)
  private Category category;

  @OneToMany(mappedBy = "tea", cascade = CascadeType.REMOVE, orphanRemoval = true)
  private List<Evaluation> evaluations;

  public Tea(String name, String description, int price, Category category) {
    this.name = name;
    this.description = description;
    this.price = price;
    this.category = category;
  }
}