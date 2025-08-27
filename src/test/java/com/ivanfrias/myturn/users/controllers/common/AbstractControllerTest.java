package com.ivanfrias.myturn.users.controllers.common;

import static org.mockito.Mockito.when;

import com.ivanfrias.myturn.security.services.JwtService;
import io.jsonwebtoken.Claims;
import io.jsonwebtoken.impl.DefaultClaims;
import jakarta.servlet.http.HttpServletRequest;
import org.junit.jupiter.api.BeforeEach;
import org.mockito.Mock;

public abstract class AbstractControllerTest {

  protected static final String TOKEN = "faketoken";

  @Mock protected JwtService jwtService;

  @Mock protected HttpServletRequest request;

  protected Claims claims;

  @BeforeEach
  void setupCommonMocks() {}

  protected void mockAuthenticatedUser(Long userId, String role) {
    claims = new DefaultClaims();
    claims.put("user_id", userId);
    if (role != null) {
      claims.put("role", role);
    }

    when(request.getHeader("Authorization")).thenReturn("Bearer " + TOKEN);
    when(jwtService.extractAllClaims(TOKEN)).thenReturn(claims);
  }
}
