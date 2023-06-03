package com.driver.services.impl;

import com.driver.model.*;
import com.driver.repository.ConnectionRepository;
import com.driver.repository.ServiceProviderRepository;
import com.driver.repository.UserRepository;
import com.driver.services.ConnectionService;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import java.util.List;

@Service
public class ConnectionServiceImpl implements ConnectionService {
    @Autowired
    UserRepository userRepository2;
    @Autowired
    ServiceProviderRepository serviceProviderRepository2;
    @Autowired
    ConnectionRepository connectionRepository2;

    @Override
    public User connect(int userId, String countryName) throws Exception{
        User user = userRepository2.findById(userId).get();
        countryName = countryName.toUpperCase();

        if(user.getMaskedIp() != null){
            throw new Exception("Already connected");
        }
        else if(user.getOriginalCountry().getCountryName().toString().equals(countryName)){
            return user;
        }
        else {
            if (user.getServiceProviderList() == null) {
                throw new Exception("Unable to connect");
            }

            List<ServiceProvider> serviceProviders = user.getServiceProviderList();
            int smallestId = Integer.MAX_VALUE;
            Country country = null;
            ServiceProvider serviceProvider = null;

            for (ServiceProvider currServiceProvider : serviceProviders) {
                List<Country> countries = currServiceProvider.getCountryList();
                for (Country currCountry : countries) {
                    String currCountryName = currCountry.getCountryName().toString();
                    if (currCountryName.equalsIgnoreCase(countryName) && smallestId > currServiceProvider.getId()) {
                        serviceProvider = currServiceProvider;
                        country = currCountry;
                        smallestId = currServiceProvider.getId();
                    }
                }
            }

            if (serviceProvider != null) {
                Connection connection = new Connection();
                connection.setUser(user);
                connection.setServiceProvider(serviceProvider);

                String countryCode = country.getCode();
                String maskedId = countryCode + "." + serviceProvider.getId() + "." + userId;
                user.setMaskedIp(maskedId);
                user.setConnected(true);
                user.getConnectionList().add(connection);

                serviceProvider.getConnectionList().add(connection);

                userRepository2.save(user);
                serviceProviderRepository2.save(serviceProvider);
            }
        }
        return user;
    }
    @Override
    public User disconnect(int userId) throws Exception {
        User user = userRepository2.findById(userId).get();
        if(user.getConnected() == false){
            throw new Exception("Already disconnected");
        }

        user.setConnected(false);
        user.setMaskedIp(null);

        userRepository2.save(user);
        return user;
    }
    @Override
    public User communicate(int senderId, int receiverId) throws Exception {

        User sender = userRepository2.findById(senderId).get();
        User receiver = userRepository2.findById(receiverId).get();

        if (receiver.getMaskedIp() != null){
            String str = receiver.getMaskedIp();
            String countrycode = str.substring(0,3);

            if (countrycode.equals(sender.getOriginalCountry().getCode())){
                return sender;
            }
            else {
                String countryName = "";

                if (countrycode.equalsIgnoreCase(CountryName.IND.toCode()))
                    countryName = CountryName.IND.toString();
                if (countrycode.equalsIgnoreCase(CountryName.USA.toCode()))
                    countryName = CountryName.USA.toString();
                if (countrycode.equalsIgnoreCase(CountryName.JPN.toCode()))
                    countryName = CountryName.JPN.toString();
                if (countrycode.equalsIgnoreCase(CountryName.CHI.toCode()))
                    countryName = CountryName.CHI.toString();
                if (countrycode.equalsIgnoreCase(CountryName.AUS.toCode()))
                    countryName = CountryName.AUS.toString();

                User updatedSender = connect(senderId,countryName);
                if (!updatedSender.getConnected()){
                    throw new Exception("Cannot establish communication");

                }
                else return updatedSender;
            }

        }
        else{
            if(receiver.getOriginalCountry().equals(sender.getOriginalCountry())){
                return sender;
            }
            String countryName = receiver.getOriginalCountry().getCountryName().toString();
            User updatedSender1 = connect(senderId,countryName);
            if (!updatedSender1.getConnected()){
                throw new Exception("Cannot establish communication");
            }
            else return updatedSender1;
        }
    }
}
