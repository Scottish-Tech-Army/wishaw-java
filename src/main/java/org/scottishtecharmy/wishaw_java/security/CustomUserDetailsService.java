package org.scottishtecharmy.wishaw_java.security;

import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.repository.UserAccountRepository;
import lombok.RequiredArgsConstructor;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
@RequiredArgsConstructor
public class CustomUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount account = userAccountRepository.findByUsername(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found: " + username));

        if (!account.isActive()) {
            throw new UsernameNotFoundException("User account is disabled: " + username);
        }

        return new User(
                account.getUsername(),
                account.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + account.getRole().name()))
        );
    }
}
