package com.iiht.fse4.skilltrackersearch.controller;


import org.springframework.web.bind.annotation.GetMapping;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;


@RestController
@RequestMapping("/skill-tracker-search")
public class SearchController {

    /**
     *
     * @return - Return Value
     */
    @GetMapping("/searchGetData")
    public String getData(){
        return "Hello Search Microservices from Gateway";
    }
}
