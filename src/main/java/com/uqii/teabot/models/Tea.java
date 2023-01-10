package com.uqii.teabot.models;

import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Table(name = "teas")
@Getter
@Setter
@NoArgsConstructor
public class Tea {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "tea_id")
	private Long id;
	
	@Column(name = "name", nullable = false)
	private String name;
	
	@Column(name = "description")
	private String description;
	
	@Column(name = "price")
	private Long price;
	
	@ManyToOne
	@JoinColumn(name = "category_id", referencedColumnName = "category_id", nullable = false)
	private Category category;
	
	@OneToMany(mappedBy = "tea", cascade = CascadeType.REMOVE)
	private List<Evaluation> evaluations;
	
	public Tea(String name, String description, Long price, Category category) {
		this.name = name;
		this.description = description;
		this.price = price;
		this.category = category;
	}
}