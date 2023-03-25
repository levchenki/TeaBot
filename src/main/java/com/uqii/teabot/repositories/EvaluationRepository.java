package com.uqii.teabot.repositories;

import com.uqii.teabot.models.Evaluation;
import java.util.List;
import java.util.Optional;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.stereotype.Repository;

@Repository
public interface EvaluationRepository extends JpaRepository<Evaluation, Long> {

  Optional<Evaluation> findByUserIdAndTeaId(Long userId, Long teaId);

  List<Evaluation> findAllByTeaId(Long teaId);
}
