package com.yourorg.securesaas.api.users;

import com.yourorg.securesaas.app.users.UserService;
import com.yourorg.securesaas.domain.auth.User;
import com.yourorg.securesaas.infra.security.AuthenticatedPrincipal;
import org.springframework.security.core.Authentication;
import org.springframework.security.core.context.SecurityContextHolder;
import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/users")
public class UserController {

  private final UserService userService;

  public UserController(UserService userService) {
    this.userService = userService;
  }

  @GetMapping("/me")
  public UserResponse me() {
    Authentication authentication = SecurityContextHolder.getContext().getAuthentication();
    if (authentication == null || !(authentication.getPrincipal() instanceof AuthenticatedPrincipal)) {
      throw new IllegalStateException("unauthenticated");
    }
    AuthenticatedPrincipal principal = (AuthenticatedPrincipal) authentication.getPrincipal();
    User user = userService.getUser(principal.getUserId());
    return new UserResponse(user.getId(), user.getEmail());
  }
}
