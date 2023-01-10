package com.uqii.teabot.models;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "users")
@AllArgsConstructor
@NoArgsConstructor
public class User {
	
	@Id
	@Column(name = "user_id", nullable = false)
	private Long id;
	
	@Column(name = "username")
	private String username;
	
	@Enumerated(EnumType.STRING)
	@Column(name = "state")
	private BotState state;
	
	@Column(name = "chosen_tea")
	private Long chosenTeaId;
	
	@OneToMany(mappedBy = "user")
	private List<Evaluation> evaluations;
	
	@Column(name = "is_admin")
	private boolean isAdmin;
	
	public User(long id, String username) {
		this.id = id;
		this.username = username;
		this.state = BotState.START;
	}
	
	public void setState(BotState state) {
		this.state = state;
		if (state != BotState.GET_TEA && state != BotState.EVALUATING_TEA
				&& state != BotState.EDITING_TEA && state != BotState.DELETING_TEA)
			chosenTeaId = null;
	}
}
