package com.uqii.teabot.services;

import com.uqii.teabot.models.User;
import com.uqii.teabot.models.UserState;
import com.uqii.teabot.repositories.UserRepository;
import java.util.NoSuchElementException;
import java.util.Optional;
import lombok.AllArgsConstructor;
import org.springframework.stereotype.Service;
import org.springframework.transaction.annotation.Transactional;

@Service
@Transactional(readOnly = true)
@AllArgsConstructor
public class UserService {

  private final UserRepository userRepository;

  public Optional<User> getUser(Long userId) {
    return userRepository.findById(userId);
  }

  @Transactional
  public void setUserState(Long userId, UserState state) {
    User user = getUser(userId).orElseThrow(
        () -> new NoSuchElementException("No user with id " + userId));
    user.setState(state);
    userRepository.save(user);
  }

  public boolean isUserAdmin(Long userId) {
    User user = getUser(userId).orElseThrow(
        () -> new NoSuchElementException("No user with id " + userId));
    return user.isAdmin();
  }

  public boolean isUserExist(Long userId) {
    return userRepository.existsById(userId);
  }

  @Transactional
  public void saveUser(User user) {
    userRepository.save(user);
  }
}
