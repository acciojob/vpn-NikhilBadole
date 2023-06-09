package com.driver.services.impl;

import com.driver.model.Country;
import com.driver.model.CountryName;
import com.driver.model.ServiceProvider;
import com.driver.model.User;
import com.driver.repository.CountryRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.UserService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

@Service
public class UserServiceImpl implements UserService {

    @Autowired
    UserRepository userRepository3;
    @Autowired
    ServiceProviderRepository serviceProviderRepository3;
    @Autowired
    CountryRepository countryRepository3;

    @Override
    public User register(String username, String password, String countryName) throws Exception{
        User user = new User();
        user.setUsername(username);
        user.setPassword(password);

        String capitalCountryName = countryName.toUpperCase();
        if (!capitalCountryName.equals("IND") && !capitalCountryName.equals("USA") && !capitalCountryName.equals("AUS") && !capitalCountryName.equals("CHI") && !capitalCountryName.equals("JPN")) {
            throw new Exception("Country not found");
        }

        CountryName countryname = CountryName.valueOf(capitalCountryName);
        Country country = new Country();
        country.setCountryName(countryname);
        country.setCode(countryname.toCode());

        user.setOriginalIp(country.getCode() + "." + user.getId());
        country.setUser(user);
        user.setOriginalCountry(country);
        user.setConnected(false);
        //user.setMaskedIp(null);

        userRepository3.save(user);
        return user;
    }

    @Override
    public User subscribe(Integer userId, Integer serviceProviderId) {
        User user = userRepository3.findById(userId).get();
        ServiceProvider serviceProvider = serviceProviderRepository3.findById(serviceProviderId).get();

        user.getServiceProviderList().add(serviceProvider);
        serviceProvider.getUsers().add(user);

        serviceProviderRepository3.save(serviceProvider);  //save both user and serviceProvider
        return user;
    }
}
