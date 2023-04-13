package com.iiht.fse4.skilltrackerbatch.batch;

import com.iiht.fse4.skilltrackerbatch.entity.Associate;
import org.springframework.batch.item.ItemProcessor;
import org.springframework.stereotype.Component;

import java.util.Date;
import java.util.HashMap;
import java.util.Map;

@Component
public class AssociateProcessor implements ItemProcessor<Associate, Associate> {

  //  private static final Map<String, String> DEPT_NAMES =  new HashMap<>();

//    public Processor() {
//        DEPT_NAMES.put("001", "Technology");
//        DEPT_NAMES.put("002", "Operations");
//        DEPT_NAMES.put("003", "Accounts");
//    }

    @Override
    public Associate process(Associate associate) throws Exception {
        System.out.println("Inside Process Method");
        //set simple time and date into the LastUpdated Filed
        associate.setLastupdated((new java.util.Date()).toString());
        System.out.println("Added the Last Updated Time" + associate.toString());
        return associate;
    }
}
