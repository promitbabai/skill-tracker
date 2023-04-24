package com.iiht.fse4.skilltrackerauth.service;

import com.iiht.fse4.skilltrackerauth.exception.*;
import com.iiht.fse4.skilltrackerauth.entity.Associate;
import com.iiht.fse4.skilltrackerauth.entity.Mapping;
import com.iiht.fse4.skilltrackerauth.entity.Skills;
import com.iiht.fse4.skilltrackerauth.model.Profile;
import com.iiht.fse4.skilltrackerauth.model.Response;

import com.iiht.fse4.skilltrackerauth.model.SkillsFromUI;
import com.iiht.fse4.skilltrackerauth.repo.AssociateRepository;
import com.iiht.fse4.skilltrackerauth.repo.MappingRepository;
import com.iiht.fse4.skilltrackerauth.repo.SkillsRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.security.core.userdetails.UserDetails;
import org.springframework.security.core.userdetails.UserDetailsService;
import org.springframework.security.core.userdetails.UsernameNotFoundException;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class AssociateService implements UserDetailsService {


    @Autowired
    private AssociateRepository associateRepo;

    @Autowired
    private MappingRepository mappingRepo;

    @Autowired
    private SkillsRepository skillsRepo;


    @Override
    public UserDetails loadUserByUsername(String username) throws UsernameNotFoundException {
        System.out.println("Inside Service Layer - loadUserByUsername()");

        System.out.println("Username = " + username);
        Associate associateFromDB = associateRepo.findByUsername(username);
        System.out.println("Data Reieved from DB = " + associateFromDB.toString());
        System.out.println("----------- FLOW END - Now returning = ");
        return new org.springframework.security.core.userdetails.User(associateFromDB.getName(), associateFromDB.getPassword(), new ArrayList<>());
    }
























}
