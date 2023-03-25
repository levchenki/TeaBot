package com.uqii.teabot.models;

import jakarta.persistence.Column;
import jakarta.persistence.Entity;
import jakarta.persistence.EnumType;
import jakarta.persistence.Enumerated;
import jakarta.persistence.Id;
import jakarta.persistence.OneToMany;
import jakarta.persistence.Table;
import java.util.List;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

@Getter
@Setter
@Entity
@Table(name = "users")
@NoArgsConstructor
public class User {

  @Id
  @Column(name = "user_id", nullable = false)
  private Long id;

  @Column(name = "username")
  private String username;

  @Column(name = "state")
  @Enumerated(EnumType.STRING)
  private UserState state;

  @OneToMany(mappedBy = "user")
  private List<Evaluation> evaluations;

  @Column(name = "is_admin")
  private boolean isAdmin;

  public User(Long id, String username) {
    this.id = id;
    this.username = username;
    this.state = UserState.START;
  }
}