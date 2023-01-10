package com.uqii.teabot.reporitories;

import com.uqii.teabot.models.Evaluation;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;
import java.util.Optional;

public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {
	
	Optional<Evaluation> findEvaluationByUserIdAndTeaId(long userId, long teaId);
	
	List<Evaluation> findEvaluationsByTeaId(long teaId);
}