package com.uqii.teabot.services;

import com.uqii.teabot.models.Evaluation;
import com.uqii.teabot.repositories.EvaluationRepository;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class EvaluationService {

  EvaluationRepository evaluationRepository;

  public Optional<Evaluation> getEvaluation(Long userId, Long teaId) {
    return evaluationRepository.findByUserIdAndTeaId(userId, teaId);
  }

  public Double getAverageRating(Long teaId) {
    List<Evaluation> evaluations = evaluationRepository.findAllByTeaId(teaId);
    return evaluations.stream().mapToDouble(Evaluation::getRating).average().orElse(0);
  }

  public boolean isTeaEvaluatedByUser(Long userId, Long teaId) {
    return getEvaluation(userId, teaId).isPresent();
  }

  @Transactional
  public void saveEvaluation(Evaluation evaluation) {
    evaluationRepository.save(evaluation);
  }
}
