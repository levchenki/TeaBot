package com.uqii.teabot.services;

import com.uqii.teabot.models.Category;
import com.uqii.teabot.reporitories.CategoryRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class CategoryService {
	
	private CategoryRepository categoryRepository;
	
	public List<Category> getCategories() {
		return categoryRepository.findAllBySubcategory(false);
	}
	
	public List<Category> getSubcategories() {
		return categoryRepository.findAllBySubcategory(true);
	}
	
	public long getCount() {
		return categoryRepository.count();
	}
	
	public Optional<Category> getById(long id) {
		return categoryRepository.findById(id);
	}
}
