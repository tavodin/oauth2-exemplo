package io.github.tavodin.oauth2study.config.implementations;

import io.github.tavodin.oauth2study.projections.UserProjection;
import io.github.tavodin.oauth2study.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.Set;

@Service
public class UserServiceImp implements UserDetailsService {

    @Autowired
    private UserRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        UserProjection user = repository.findByEmail(username)
                .orElseThrow(() -> new UsernameNotFoundException("Username not found!"));

        Set<String> roles = repository.findRoleNameWithUserId(user.getId());

        return new UserDetailsImp(user, roles);
    }
}
