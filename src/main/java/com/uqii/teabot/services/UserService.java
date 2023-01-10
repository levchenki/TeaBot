package com.uqii.teabot.services;

import com.uqii.teabot.models.BotState;
import com.uqii.teabot.models.User;
import com.uqii.teabot.reporitories.UserRepository;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;
import java.util.Optional;

@Service
@AllArgsConstructor
@Transactional(readOnly = true)
public class UserService {
	UserRepository userRepository;
	
	
	public Optional<User> getOneUser(long id) {
		return userRepository.findById(id);
	}
	
	public List<User> getAllUsers() {
		return userRepository.findAll();
	}
	
	@Transactional
	public void createUser(User user) {
		userRepository.save(user);
	}
	
	@Transactional
	public void setStateUser(long id, BotState state) {
		getOneUser(id).ifPresent(user -> {
			user.setState(state);
			userRepository.save(user);
		});
	}
	
	public boolean isAdminUser(long id) {
		Optional<User> optionalUser = getOneUser(id);
		return optionalUser.map(User::isAdmin).orElseThrow();
	}
	
	@Transactional
	public void setTeaUser(long userId, long teaId) {
		getOneUser(userId).ifPresent(user -> {
			user.setState(BotState.GET_TEA);
			user.setChosenTeaId(teaId);
			userRepository.save(user);
		});
	}
	
	@Transactional
	public void deleteUser(long id) {
		userRepository.deleteById(id);
	}
}