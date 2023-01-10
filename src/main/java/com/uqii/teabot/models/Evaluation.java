package com.uqii.teabot.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;

@Entity
@Getter
@Setter
@Table(name = "evaluations")
@NoArgsConstructor
public class Evaluation {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "evaluation_id", nullable = false)
	private Long id;
	
	@Column(name = "rating")
	private float rating;
	
	@Column(name = "comment")
	private String comment;
	
	@ManyToOne
	@JoinColumn(name = "user_id", referencedColumnName = "user_id")
	private User user;
	
	@ManyToOne
	@JoinColumn(name = "tea_id", referencedColumnName = "tea_id")
	private Tea tea;
	
	public Evaluation(float rating, String comment, User user, Tea tea) {
		this.rating = rating;
		this.user = user;
		this.tea = tea;
		this.comment = comment;
	}
}