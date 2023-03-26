package com.uqii.teabot.services;

import com.uqii.teabot.models.Evaluation;
import com.uqii.teabot.models.Tea;
import com.uqii.teabot.models.User;
import com.uqii.teabot.repositories.EvaluationRepository;
import com.uqii.teabot.repositories.TeaRepository;
import com.uqii.teabot.repositories.UserRepository;
import java.util.List;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class EvaluationService {

  private final EvaluationRepository evaluationRepository;
  private final UserRepository userRepository;
  private final TeaRepository teaRepository;

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

  @Transactional
  public void createOrUpdateEvaluation(Long userId, Long teaId, Double rating, String comment) {
    getEvaluation(userId, teaId).ifPresentOrElse((evaluation -> {
      evaluation.setComment(comment);
      evaluation.setRating(rating);
      saveEvaluation(evaluation);
    }), () -> {
      User user = userRepository.findById(userId)
          .orElseThrow(() -> new NoSuchElementException("No user with id " + userId));
      Tea tea = teaRepository.findById(teaId)
          .orElseThrow(() -> new NoSuchElementException("No tea with id " + teaId));
      saveEvaluation(new Evaluation(rating, comment, tea, user));
    });
  }
}
