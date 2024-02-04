package com.ra.security.principal;

import com.ra.model.Users;
import com.ra.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Component;

@Component
public class UserDetailService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;

    @Override
    public UserDetails loadUserByUsername(String emailOrPhone) throws UsernameNotFoundException {
        Users user = userRepository.findByEmail(emailOrPhone);
        if (user == null) {
            user = userRepository.findByPhone(emailOrPhone);
        }
        if (user == null) {
            throw new UsernameNotFoundException("User Not found");
        }
        return UserPrinciple.build(user);
    }
}
