package com.uqii.teabot.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue;
import jakarta.persistence.GenerationType;
import jakarta.persistence.Id;
import jakarta.persistence.JoinColumn;
import jakarta.persistence.ManyToOne;
import jakarta.persistence.Table;
import jakarta.validation.constraints.Max;
import jakarta.validation.constraints.Min;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "evaluations")
@NoArgsConstructor
public class Evaluation {

  @Id
  @GeneratedValue(strategy = GenerationType.IDENTITY)
  @Column(name = "evaluation_id", nullable = false)
  private Long id;

  @Column(name = "rating")
  @Min(value = 1)
  @Max(value = 10)
  private Double rating;

  @Column(name = "comment")
  private String comment;

  @ManyToOne
  @JoinColumn(name = "tea_id", referencedColumnName = "tea_id")
  private Tea tea;

  @ManyToOne
  @JoinColumn(name = "user_id", referencedColumnName = "user_id")
  private User user;

  public Evaluation(Double rating, String comment, Tea tea, User user) {
    this.rating = rating;
    this.comment = comment;
    this.tea = tea;
    this.user = user;
  }
}
