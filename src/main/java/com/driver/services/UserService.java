package com.driver.services;

import com.driver.model.User;
import org.springframework.stereotype.Service;

@Service
public interface UserService {

    public User register(String username, String password, String countryName) throws Exception;

    public User subscribe(Integer userId, Integer serviceProviderId);
}