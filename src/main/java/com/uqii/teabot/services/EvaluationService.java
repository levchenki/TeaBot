package com.uqii.teabot.services;

import com.uqii.teabot.models.Evaluation;
import com.uqii.teabot.reporitories.EvaluationRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class EvaluationService {
	
	EvaluationRepository evaluationRepository;
	
	public Optional<Evaluation> getOneEvaluation(long userId, long teaId) {
		return evaluationRepository.findEvaluationByUserIdAndTeaId(userId, teaId);
	}
	
	public double getAverageRating(long teaId) {
		List<Evaluation> evaluations = evaluationRepository.findEvaluationsByTeaId(teaId);
		return evaluations.stream().mapToDouble(Evaluation::getRating).average().orElse(0);
	}
	
	@Transactional
	public void saveEvaluation(Evaluation evaluation) {
		evaluationRepository.save(evaluation);
	}
}
