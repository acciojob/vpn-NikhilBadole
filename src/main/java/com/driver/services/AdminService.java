package com.driver.services;

import com.driver.model.Admin;
import com.driver.model.ServiceProvider;
import org.springframework.stereotype.Service;

@Service
public interface AdminService {

    public Admin register(String username, String password);

    public Admin addServiceProvider(int adminId, String providerName);

    public ServiceProvider addCountry(int serviceProviderId, String countryName) throws Exception;
}