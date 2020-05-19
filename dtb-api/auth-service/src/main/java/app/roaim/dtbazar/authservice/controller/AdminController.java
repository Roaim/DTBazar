package app.roaim.dtbazar.authservice.controller;

import app.roaim.dtbazar.authservice.domain.User;
import app.roaim.dtbazar.authservice.service.AuthService;
import lombok.AllArgsConstructor;
import org.springframework.web.bind.annotation.*;
import reactor.core.publisher.Flux;
import reactor.core.publisher.Mono;

@RestController
@RequestMapping("/admin")
@AllArgsConstructor
public class AdminController {
    private final AuthService authService;

    @GetMapping("/user")
    Flux<User> getUserList(
            @RequestParam(required = false) Boolean enabled,
            @RequestParam(defaultValue = "0") int page, @RequestParam(defaultValue = "20") int size) {
        return authService.getUserList(page, size, enabled);
    }

    @GetMapping("/user/{userId}")
    Mono<User> getUser(@PathVariable String userId) {
        return authService.getUserById(userId);
    }

    @PatchMapping("/user/{userId}")
    Mono<User> updateUserStatus(
            @PathVariable String userId, @RequestParam(defaultValue = "true") boolean enable,
            @RequestParam(defaultValue = "false") boolean admin) {
        return authService.updateUserStatus(userId, enable, admin);
    }
}
