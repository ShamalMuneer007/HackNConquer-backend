package org.hackncrypt.userservice.service.security;

import org.hackncrypt.userservice.model.entities.User;
import org.hackncrypt.userservice.repositories.UserRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;
import org.springframework.stereotype.Service;

import java.util.LinkedList;
import java.util.Queue;

@Service
public class CustomUserDetailsService implements UserDetailsService {
    @Autowired
    private UserRepository userRepository;
    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        if(!userRepository.existsByUsername(username)){
            throw new UsernameNotFoundException("Username does not exists !");
        }
        User user = userRepository.findByUsername(username);
        return new CustomUserDetails(user);
    }
}

