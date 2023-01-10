package com.uqii.teabot.reporitories;

import com.uqii.teabot.models.Tea;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.Pageable;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface TeaRepository extends JpaRepository<Tea, Long> {
	
	Page<Tea> findAllByCategoryIdOrderByNameAsc(long categoryId, Pageable pageable);
	
	long countByCategoryId(long categoryId);
}