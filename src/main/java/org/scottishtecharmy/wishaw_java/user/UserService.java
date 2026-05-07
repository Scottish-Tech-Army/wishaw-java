package org.scottishtecharmy.wishaw_java.user;

import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.scottishtecharmy.wishaw_java.centre.Centre;
import org.scottishtecharmy.wishaw_java.centre.CentreRepository;
import org.scottishtecharmy.wishaw_java.user.dto.CreateUserRequest;
import org.scottishtecharmy.wishaw_java.user.dto.UpdateUserRequest;
import org.scottishtecharmy.wishaw_java.user.dto.UserResponse;
import org.springframework.security.crypto.password.PasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.List;

@Slf4j
@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final CentreRepository centreRepository;
    private final PasswordEncoder passwordEncoder;

    public List<UserResponse> findAll() {
        log.debug("Fetching all users");
        return userRepository.findAll().stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse findById(Long id) {
        log.debug("Fetching user by id={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User not found: id={}", id);
                    return new IllegalArgumentException("User not found: " + id);
                });
        return toResponse(user);
    }

    public List<UserResponse> findByCentre(Long centreId) {
        log.debug("Fetching users for centreId={}", centreId);
        return userRepository.findByCentreId(centreId).stream()
                .map(this::toResponse)
                .toList();
    }

    public UserResponse create(CreateUserRequest request) {
        log.info("Creating user: username='{}'", request.username());
        if (userRepository.existsByUsername(request.username())) {
            log.warn("User creation failed — username '{}' already exists", request.username());
            throw new IllegalArgumentException("Username already exists");
        }

        User user = new User();
        user.setUsername(request.username());
        user.setPassword(passwordEncoder.encode(request.password()));
        user.setDisplayName(request.displayName());
        user.setRole(request.role() != null ? Role.valueOf(request.role()) : Role.USER);

        if (request.centreId() != null) {
            Centre centre = centreRepository.findById(request.centreId())
                    .orElseThrow(() -> new IllegalArgumentException("Centre not found"));
            user.setCentre(centre);
        }

        return toResponse(userRepository.save(user));
    }

    public UserResponse update(Long id, UpdateUserRequest request) {
        log.info("Updating user: id={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("User update failed — not found: id={}", id);
                    return new IllegalArgumentException("User not found: " + id);
                });

        if (request.displayName() != null) {
            user.setDisplayName(request.displayName());
        }
        if (request.role() != null) {
            user.setRole(Role.valueOf(request.role()));
        }
        if (request.centreId() != null) {
            Centre centre = centreRepository.findById(request.centreId())
                    .orElseThrow(() -> new IllegalArgumentException("Centre not found"));
            user.setCentre(centre);
        }

        return toResponse(userRepository.save(user));
    }

    public void delete(Long id) {
        log.info("Deleting user: id={}", id);
        if (!userRepository.existsById(id)) {
            log.warn("User deletion failed — not found: id={}", id);
            throw new IllegalArgumentException("User not found: " + id);
        }
        userRepository.deleteById(id);
        log.info("User deleted: id={}", id);
    }

    public void changePassword(Long id, String currentPassword, String newPassword) {
        log.info("Password change attempt for userId={}", id);
        User user = userRepository.findById(id)
                .orElseThrow(() -> {
                    log.warn("Password change failed — user not found: id={}", id);
                    return new IllegalArgumentException("User not found: " + id);
                });

        if (!passwordEncoder.matches(currentPassword, user.getPassword())) {
            log.warn("Password change failed — incorrect current password for userId={}", id);
            throw new IllegalArgumentException("Current password is incorrect");
        }

        user.setPassword(passwordEncoder.encode(newPassword));
        userRepository.save(user);
        log.info("Password changed successfully for userId={}", id);
    }

    public UserResponse toResponse(User user) {
        return new UserResponse(
                user.getId(),
                user.getUsername(),
                user.getDisplayName(),
                user.getRole().name(),
                user.getCentre() != null ? user.getCentre().getId() : null,
                user.getCentre() != null ? user.getCentre().getName() : null,
                user.getProfileImageUrl(),
                user.getCreatedAt()
        );
    }
}
