package com.example.pilates_studio.service;

import com.example.pilates_studio.model.UserPrincipal;
import com.example.pilates_studio.model.Users;
import com.example.pilates_studio.repository.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

@Service
public class MyUserDetailsService implements UserDetailsService {

    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Users user = userRepository.findByUsername(username);
        if(user == null){
            throw new UsernameNotFoundException("User not found");
        }
        return new UserPrincipal(user);
    }
}
