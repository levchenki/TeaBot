package com.uqii.teabot.reporitories;

import com.uqii.teabot.models.Category;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;

public interface CategoryRepository extends JpaRepository<Category, Long> {
	
	List<Category> findAllBySubcategory(boolean subcategory);
}