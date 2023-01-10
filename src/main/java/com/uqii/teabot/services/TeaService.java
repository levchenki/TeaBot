package com.uqii.teabot.services;

import com.uqii.teabot.models.Evaluation;
import com.uqii.teabot.models.Tea;
import com.uqii.teabot.reporitories.TeaRepository;
import lombok.AllArgsConstructor;
import org.springframework.data.domain.PageRequest;
import org.springframework.data.domain.Pageable;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class TeaService {
	
	private TeaRepository teaRepository;
	
	public List<Tea> getAllTeas(long categoryId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		
		return teaRepository.findAllByCategoryIdOrderByNameAsc(categoryId, pageable).getContent();
	}
	
	public List<Tea> getEvaluatedTeas(long categoryId, long userId, int page, int size) {
		Pageable pageable = PageRequest.of(page, size);
		
		List<Tea> teas = teaRepository.findAllByCategoryIdOrderByNameAsc(categoryId, pageable).getContent();
		// todo detach
		for (var tea: teas) {
			List<Evaluation> evaluations = tea.getEvaluations();
			evaluations.removeIf(e -> e.getUser().getId() != userId);
		}
		
		return teas;
	}
	
	public Optional<Tea> getOneTea(long id) {
		return teaRepository.findById(id);
	}
	
	public long getCount(long categoryId) {
		return teaRepository.countByCategoryId(categoryId);
	}
	
	public long getAllCount() {
		return teaRepository.count();
	}
	
	@Transactional
	public void saveTea(Tea tea) {
		teaRepository.save(tea);
	}
	
	@Transactional
	public void deleteTea(long teaId) {
		teaRepository.deleteById(teaId);
	}
}