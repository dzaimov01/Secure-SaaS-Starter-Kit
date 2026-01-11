package com.yourorg.securesaas.app.users;

import com.yourorg.securesaas.domain.auth.User;
import com.yourorg.securesaas.infra.db.UserRepository;
import java.util.UUID;
import org.springframework.stereotype.Service;

@Service
public class UserService {

  private final UserRepository userRepository;

  public UserService(UserRepository userRepository) {
    this.userRepository = userRepository;
  }

  public User getUser(UUID userId) {
    return userRepository.findById(userId).orElseThrow();
  }
}
