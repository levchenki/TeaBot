package com.uqii.teabot.services;

import com.uqii.teabot.models.Category;
import com.uqii.teabot.models.Evaluation;
import com.uqii.teabot.models.Tea;
import com.uqii.teabot.repositories.TeaRepository;
import java.util.List;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class TeaService {

  TeaRepository teaRepository;

  public Optional<Tea> getTea(Long teaId) {
    return teaRepository.findById(teaId);
  }

  public List<Tea> getTeaList(Category category, int page, int size) {
    Pageable pageable = PageRequest.of(page, size);
    return teaRepository.findAllByCategoryOrderByNameAsc(category, pageable).getContent();
  }

  public List<Tea> getEvaluatedTeaList(Category category, long userId, int page, int size) {
    List<Tea> teas = getTeaList(category, page, size);

    for (var tea : teas) {
      List<Evaluation> evaluations = tea.getEvaluations();
      evaluations.removeIf(evaluation -> evaluation.getUser().getId() != userId);
    }
    return List.copyOf(teas);
  }

  public long getCountByCategory(Category category) {
    return teaRepository.countByCategory(category);
  }


  @Transactional
  public void saveTea(Tea tea) {
    teaRepository.save(tea);
  }

  @Transactional
  public void deleteTea(long teaId) {
    teaRepository.deleteById(teaId);
  }

  public boolean isTeaExists(String name) {
    return teaRepository.existsByNameIgnoreCase(name);
  }
}
