package org.scottishtecharmy.wishaw_java.security;

import org.scottishtecharmy.wishaw_java.entity.UserAccount;
import org.scottishtecharmy.wishaw_java.repository.UserAccountRepository;
import org.springframework.security.core.authority.SimpleGrantedAuthority;
import org.springframework.security.core.userdetails.User;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class SecurityUserDetailsService implements UserDetailsService {

    private final UserAccountRepository userAccountRepository;

    public SecurityUserDetailsService(UserAccountRepository userAccountRepository) {
        this.userAccountRepository = userAccountRepository;
    }

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserAccount userAccount = userAccountRepository.findByEmailIgnoreCase(username)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return toUserDetails(userAccount);
    }

    public UserDetails loadUserById(String id) {
        UserAccount userAccount = userAccountRepository.findById(id)
                .orElseThrow(() -> new UsernameNotFoundException("User not found"));
        return toUserDetails(userAccount);
    }

    private UserDetails toUserDetails(UserAccount userAccount) {
        return new User(
                userAccount.getEmail(),
                userAccount.getPasswordHash(),
                List.of(new SimpleGrantedAuthority("ROLE_" + userAccount.getRole().name()))
        );
    }
}
