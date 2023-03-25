package com.uqii.teabot.repositories;

import com.uqii.teabot.models.Category;
import com.uqii.teabot.models.Tea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeaRepository extends JpaRepository<Tea, Long> {

  Page<Tea> findAllByCategoryOrderByNameAsc(Category category, Pageable pageable);

  long countByCategory(Category category);

  boolean existsByNameIgnoreCase(String name);
}
