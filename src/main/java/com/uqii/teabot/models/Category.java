package com.uqii.teabot.models;

import lombok.Getter;
import lombok.Setter;

import javax.persistence.*;
import java.util.List;

@Entity
@Getter
@Setter
@Table(name = "categories")
public class Category {
	
	@Id
	@GeneratedValue(strategy = GenerationType.IDENTITY)
	@Column(name = "category_id")
	private Long id;
	
	@Column(name = "name")
	private String name;
	
	@Column(name = "subcategory")
	private boolean subcategory;
	
	@OneToMany(mappedBy = "category")
	private List<Tea> teas;
}
