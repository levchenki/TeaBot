package com.uqii.teabot.services;

import com.uqii.teabot.models.Category;
import java.util.Arrays;
import java.util.List;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
public class CategoryService {

  public List<Category> getSubcategories() {
    return Arrays.stream(Category.values()).filter(Category::getIsSubcategory).toList();
  }

  public List<Category> getCategories() {
    return Arrays.stream(Category.values()).filter(c -> !c.getIsSubcategory()).toList();
  }

  public boolean isSubcategory(Category category) {
    return category.getIsSubcategory();
  }
}
