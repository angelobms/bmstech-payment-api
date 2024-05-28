package br.com.bmstech.payment.application.controllers;

import br.com.bmstech.payment.domain.dto.LoginRequestDTO;
import br.com.bmstech.payment.domain.dto.LoginResponseDTO;
import br.com.bmstech.payment.infra.security.TokenService;
import br.com.bmstech.payment.domain.repositories.UserRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.http.ResponseEntity;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

/**
 * @author Angelo Brandão (angelobms@gmail.com)
 * @version 1.0
 */
@RestController
@RequestMapping("/auth")
@RequiredArgsConstructor
public class AuthController {

    private final UserRepository userRepository;
    private final PasswordEncoder passwordEncoder;
    private final TokenService tokenService;

    @PostMapping("/login")
    public ResponseEntity<LoginResponseDTO> login(@RequestBody LoginRequestDTO loginRequestDTO) {
        var user = this.userRepository.findByEmail(loginRequestDTO.email())
                .orElseThrow(() -> new RuntimeException("User not found"));
        if (!passwordEncoder.matches(loginRequestDTO.password(), user.getPassword())) {
            return ResponseEntity.badRequest().build();
        }
        return ResponseEntity.ok(new LoginResponseDTO(user.getName(), this.tokenService.generateToken(user)));
    }

}
