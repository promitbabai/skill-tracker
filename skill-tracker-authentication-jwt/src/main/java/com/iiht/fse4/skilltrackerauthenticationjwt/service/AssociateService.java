package com.iiht.fse4.skilltrackerauthenticationjwt.service;

import com.iiht.fse4.skilltrackerauthenticationjwt.entity.Associate;
import com.iiht.fse4.skilltrackerauthenticationjwt.repo.AssociateRepository;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;

import java.util.ArrayList;

public class AssociateService  implements UserDetailsService {

    @Autowired
    private AssociateRepository repository;

    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        Associate associateFromDB = repository.findByAssociateid(username);
        return new org.springframework.security.core.userdetails.User(associateFromDB.getName(), associateFromDB.getPassword(), new ArrayList<>());
    }



}
