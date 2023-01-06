package com.iiht.fse4.skilltrackermaintain.controller;

import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;

@RestController
@RequestMapping("/skill-tracker-maintain")
public class MaintainController {

    
    /**
     *
     * @return - the basic string value
     */
    @GetMapping("/maintainGetData")
    public String getData(){
        return "Hello AddUpdate Microservices";
    }

}
