package com.uqii.teabot.reporitories;

import com.uqii.teabot.models.User;
import org.springframework.data.jpa.repository.JpaRepository;

public interface UserRepository extends JpaRepository<User, Long> {
}