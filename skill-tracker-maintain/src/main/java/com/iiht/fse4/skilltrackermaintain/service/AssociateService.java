package com.iiht.fse4.skilltrackermaintain.service;

import com.iiht.fse4.skilltrackermaintain.exception.*;
import com.iiht.fse4.skilltrackermaintain.kafkaconfig.KafkaMessage;
import com.iiht.fse4.skilltrackermaintain.kafkaconfig.KafkaProducerProfile;
import com.iiht.fse4.skilltrackermaintain.entity.Associate;
import com.iiht.fse4.skilltrackermaintain.entity.Mapping;
import com.iiht.fse4.skilltrackermaintain.entity.Skills;
import com.iiht.fse4.skilltrackermaintain.model.Profile;
import com.iiht.fse4.skilltrackermaintain.model.Response;

import com.iiht.fse4.skilltrackermaintain.model.SkillsFromUI;
import com.iiht.fse4.skilltrackermaintain.repo.AssociateRepository;
import com.iiht.fse4.skilltrackermaintain.repo.MappingRepository;
import com.iiht.fse4.skilltrackermaintain.repo.SkillsRepository;
import org.apache.commons.lang.StringUtils;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Service;

import lombok.extern.slf4j.Slf4j;
import org.springframework.transaction.annotation.Transactional;


import java.text.DateFormat;
import java.text.ParseException;
import java.text.SimpleDateFormat;
import java.util.*;

@Slf4j
@Service
public class AssociateService {

    @Autowired
    private AssociateRepository associateRepo;

    @Autowired
    private MappingRepository mappingRepo;

    @Autowired
    private SkillsRepository skillsRepo;

    @Autowired
    private KafkaProducerProfile kafkaMsgEvent;


    Map<String, String> allSkillsMap = new HashMap<String, String>();


    public List<Associate> getAllAssociates (){
        List<Associate> associateList =  associateRepo.findAll();
        return associateList;
    }

    public List<Skills> getAllSkills (){
        System.out.println("Service Layer - getAllSkills");
        List<Skills> skillsList =  skillsRepo.findAll();
        for(Skills skills : skillsList){
            allSkillsMap.put(skills.getSkillid(), skills.getSkillname());
        }
        return skillsList;
    }

    public List<Mapping> getAssociateSkillRatings (final String associateId){
        System.out.println("Service Layer - getAssociateSkillRatings");
        List<Mapping> ratingsList =  mappingRepo.findByAssociateid(associateId);
        return ratingsList;
    }


    /**
     * This method is used to validate the Associate Profile sent from the UI and then
     * persist it in the MySql database and then send the JSON object to the Search Microservice
     * @param profile - The Associate Profile sent from UI
     * @return Response - The Response object holding some date and messgae to be displayed in the UI
     */
    public Profile getUserDetails (final String associateId, final String associatePassword){
        Associate associate =  associateRepo.findByUsername(associateId);
        if(associate != null && associatePassword.equals(associate.getPassword())){
            System.out.println("Matched");

            getAllSkills();
            List<Mapping> mappingsList = mappingRepo.findByAssociateid(associateId);
            Profile profile = populateProfileFromAssociateAndMapping(associate, mappingsList);

            return profile;
        }
        else{
            log.error("Associate credentials do not match");
            System.out.println("NOT Matched");
            throw new CredentialsMismatchException();
        }
    }


    private Profile populateProfileFromAssociateAndMapping(final Associate associate, final List<Mapping> mappingsList){
        Profile profile = new Profile();
        profile.setAssociateid(associate.getUsername());
        profile.setName(associate.getName());
        profile.setEmail(associate.getEmail());
        profile.setMobile(associate.getMobile());
        profile.setLastupdated(associate.getLastupdated());
        profile.setRole(associate.getRole());

        List<SkillsFromUI> techskills = new ArrayList<SkillsFromUI>();
        List<SkillsFromUI> nonTechskills = new ArrayList<SkillsFromUI>();

        for(Mapping mapping : mappingsList){
            String skillId = mapping.getSkillid();
            System.out.println("Skill ID "+skillId);
            if(skillId.charAt(0)=='T'){
                techskills.add(populateMappingToSkillsFromUI(mapping));
                System.out.println("Skill added in Technical Skills "+skillId);
            }else{
                nonTechskills.add(populateMappingToSkillsFromUI(mapping));
                System.out.println("Skill added in Non-Technical Skills "+skillId);
            }

        }

        profile.setTechskills(techskills);
        profile.setNontechskills(nonTechskills);

        return profile;
    }


    private SkillsFromUI populateMappingToSkillsFromUI(Mapping mapping){
        return new SkillsFromUI(mapping.getId(), mapping.getSkillid(), this.allSkillsMap.get(mapping.getSkillid()), mapping.getRating());
    }


    public Associate getAssociateByID (final String associateId){
        System.out.println("Trying to check if the associate exists in database by calling getAssociateByID");
        Associate associate =  associateRepo.findByUsername(associateId);
        return associate;
    }


    /**
     * This method is used to validate the Associate Profile sent from the UI and then
     * persist it in the MySql database and then send the JSON object to the Search Microservice
     * @param profile - The Associate Profile sent from UI
     * @return Response - The Response object holding some date and messgae to be displayed in the UI
     */
    @Transactional
    public Response addProfile (Profile profile){
        Response response = new Response();
        validateInputData(profile);
        Associate associateFromDB = null;
        //check if the Profile is already present in the DB or not
        associateFromDB = getAssociateByID(profile.getAssociateid());
        System.out.println("Obtained Associate From DB is sucesfully checked");
        if(associateFromDB==null){

            response.setStatus(409);
            response.setMessage("Insert into both Associate Table and Mapping Table");

            // STEP - 01 - Save into the Associate Table
            Associate associate = populateEntityAssociate(profile);
            associateRepo.save(associate);

            // STEP - 02 - Check for data in Technical Skills Array andSave into the Mapping Table
            List<Skills> skillsListFromDB = skillsRepo.findAll();
            for(SkillsFromUI skillRowFromUITechList : profile.getTechskills()){
                mappingRepo.save(populateEntityMapping(profile.getAssociateid(), skillRowFromUITechList, skillsListFromDB));
            }

            // STEP - 03 - Check for data in NonTechnical Skills Array and Save into the Mapping Table
            for(SkillsFromUI skills : profile.getNontechskills()){
                mappingRepo.save(populateEntityMapping(profile.getAssociateid(), skills, skillsListFromDB));
            }


//            STEP 4 - check if all records successfully inserted into DB, then send message to KAFKA
            log.info("Sending Profile Object to kafka server");
            System.out.println("Sending Profile Object to kafka server");
            sendKafkaMessage("INSERT", profile);

            //String responseMsgFroUI = "Associate Data with ID = " + associate.getId() + ", saved successfully!!";
            response.setMessage(associate.getId());
            response.setStatus(201);
            log.info("Associate Data with ID = " + associate.getId() + ", saved successfully!!");


        }else{
            //Profile already present in DB cannot be added
            log.error("Profile already present in DB cannot be added");
            throw new AssociateAlreadyPresentInDatabaseException();
        }

        return response;

    }



    private List<Mapping> getAllSkillsByAssociateId(final String associateId){
        return mappingRepo.findByAssociateid(associateId);
    }




    public Response updateProfile ( final Profile profile) {
            Response response = new Response();
            Associate associateFromDB = null;

            //checkIfUpdationIsAllowed(profile);





            // STEP - 01 (NOT REQUIRED) - Save into the Associate Table, as Profile is already present

            // STEP - 02 - Check for data in Technical Skills Array andSave into the Mapping Table
            List<Skills> skillsListFromDB = skillsRepo.findAll();

            // 1) Get the List of Mapping Entities Rows for Nirnayana
        // 2) it will  have UUIDS
        // 3) update only the 3-4 rows that was sent from UI
        // 4) Backup sol - Update all from Backend

            List<Mapping> mapingsList = getAllSkillsByAssociateId(profile.getAssociateid());

            for(SkillsFromUI skillRowFromUITechList : profile.getTechskills()){
                mappingRepo.save(populateExistingMappingObjectFromUI(skillRowFromUITechList.getId(), profile.getAssociateid(), skillRowFromUITechList.getSkillId(), skillRowFromUITechList.getRating()));
//                mappingRepo.save(populateEntityMapping(profile.getAssociateid(), skillRowFromUITechList, skillsListFromDB));
            }

            // STEP - 03 - Check for data in NonTechnical Skills Array and Save into the Mapping Table
            for(SkillsFromUI skillRowFromUINonTechList : profile.getNontechskills()){
                mappingRepo.save(populateExistingMappingObjectFromUI(skillRowFromUINonTechList.getId(), profile.getAssociateid(), skillRowFromUINonTechList.getSkillId(), skillRowFromUINonTechList.getRating()));
//                mappingRepo.save(populateEntityMapping(profile.getAssociateid(), skills, skillsListFromDB));
            }


            //STEP 4 - check if all records successfully inserted into DB, then send message to KAFKA
            log.info("LOG ENTRY - Sending Profile Object to kafka server");
            System.out.println("Sending Profile Object to kafka server");
            sendKafkaMessage("UPDATE", profile);
        response.setStatus(201);
        response.setMessage("Profile updated in the Database");
        return response;
    }



    /**
     * This method is used to validate the Technical and NonTechnical Skills of the User
     *
     * @param profile -  The Profile object sent from Angular UI
     */
    public void checkIfUpdationIsAllowed (final Profile profile){
        Associate associateFromDB = null;
        //check if the Profile is already present in the DB or not
        if(null == getAssociateByID(profile.getAssociateid())){
            throw new AssociateNotPresentInDBException();
        }

        //check if the profile was inserted 10 days ago
        String lastupdatedinDB = profile.getLastupdated();
        DateFormat formatter = new SimpleDateFormat("E MMM dd HH:mm:ss Z yyyy");
        Date dateFromDB = null;
        try {
            dateFromDB = (Date) formatter.parse(lastupdatedinDB);
        } catch (ParseException e) {
            e.printStackTrace();
            throw new RecordUpdationNotAllowedInDatabaseException();
        }
        Date currentDate = new java.util.Date();
        System.out.println("LAST UPDATED IN DB - DATE OBJECT = " + dateFromDB);
        System.out.println("CURRENT DATE - DATE OBJECT = " + currentDate);
        if(isProfileLessThanTenDays(currentDate, dateFromDB)){
            throw new RecordUpdationNotAllowedInDatabaseException();
        }

        //Check 3 - check if only the Skills ratings are u
    }


    private static boolean isProfileLessThanTenDays(Date current, Date obtained) {
        boolean isProfileLessThanTenDays = true;
        long differenceInTime = 0;
        if (current.after(obtained)) {
            differenceInTime = current.getTime() - obtained.getTime();
        } else {
            differenceInTime = obtained.getTime() - current.getTime();
        }

        long differenceInHours = (differenceInTime / (1000 * 60 * 60)) % 24;
        long differenceInMinutes = (differenceInTime / (1000 * 60)) % 60;
        long differenceInDays = (differenceInTime / (1000 * 60 * 60 * 24)) % 365;

        System.out.print("\n-----------------\n");
        System.out.println("The difference in of " + differenceInDays + " days " + differenceInHours + " Hours " + differenceInMinutes + " Minutes");
        //checkValidity(differenceInDays);
        if (differenceInDays > 10) {
            isProfileLessThanTenDays = false;
        }
        return isProfileLessThanTenDays;

    }



    /**
     * This method is used to validate the Technical and NonTechnical Skills of the User
     *
     * @param profile -  The Profile object sent from Angular UI
     */
    public void validateInputData (final Profile profile){

        for(SkillsFromUI skillRowFromUITechList : profile.getTechskills()) {
            checkSkillRatingsValue(skillRowFromUITechList.getRating());
        }
        for(SkillsFromUI skillRowFromUITechList : profile.getNontechskills()) {
            checkSkillRatingsValue(skillRowFromUITechList.getRating());
        }
    }


    /**
     * This method is used to validate the users given Skills Rating
     * throw an Exception if the Ratings of a Skills is Empty or Null
     * throw an Exception if the Ratings of a Skills is Non Numeric
     * throw an Exception if the value of Rating of a skill is not in range between 0-20
     *
     * @param ratingFromUI -  The rating value for any particular skills
     */
    private void checkSkillRatingsValue(final String ratingFromUI){
        if (StringUtils.isEmpty(ratingFromUI)) {
            throw new EmptySkillsRatingException();
        } else {
            if(!StringUtils.isNumeric(ratingFromUI)){
                throw new RatingNotNumericException();
            }
            int rating = Integer.parseInt(ratingFromUI);
            if (rating < 0 || rating > 20) {
                throw new RatingsValueOutsideRangeException();
            }
        }

    }

    private void sendKafkaMessage(String mongoOpsCode, Profile profile) {
        KafkaMessage kafkaMsgEent = new KafkaMessage();
        kafkaMsgEent.setMongoOpsCode(mongoOpsCode);
        kafkaMsgEent.setProfile(profile);

        kafkaMsgEvent.sendMessage(kafkaMsgEent);
    }

    public Associate populateEntityAssociate(final Profile profile){
        Associate associate = new Associate();
        UUID uuid=UUID.randomUUID();
        associate.setId(uuid.toString());
        associate.setUsername(profile.getAssociateid());
        associate.setName(profile.getName());
        associate.setMobile(profile.getMobile());
        associate.setEmail(profile.getEmail());
        associate.setLastupdated((new java.util.Date()).toString());
        associate.setPassword(profile.getPassword());
        associate.setRole("USER");
        System.out.println("\n\n\n\n\n ######################################");
        System.out.println("LastupdatedTimeinDB" + associate.getLastupdated());
        return associate;
    }

    /**
     * UUID For this was already present and was sent from the UI
     * and is now comming back from the ui with updated Rating values
     * @return
     */
    private Mapping populateExistingMappingObjectFromUI(final String id, final String associateid, final String skillId, final String rating){
        Mapping mapping = new Mapping();
        mapping.setId(id);
        mapping.setSkillid(skillId);
        mapping.setRating(rating);
        mapping.setAssociateid(associateid);
        System.out.println("INSERT INTO MAPPING TABLE = " + mapping.toString());
        return mapping;
    }


    public Mapping populateEntityMapping(final String associateid, final SkillsFromUI skillsFromUI, List<Skills> skillsListFromDB){
        Mapping mapping = new Mapping();
        UUID uuid=UUID.randomUUID();
        mapping.setId(uuid.toString());
        mapping.setAssociateid(associateid);
//        System.out.println("SKILL FROM UI TECH LIST OBJ = " + skillsFromUI.getTopic() + " - " + skillsFromUI.getRating());
        for(Skills skillsFromDB : skillsListFromDB){
//            System.out.println("SKILLS TABLE ROW = " + skillsFromDB.getSkillid() + " - " + skillsFromDB.getSkillname());
            if(skillsFromDB.getSkillname().equals(skillsFromUI.getTopic())){
//                System.out.println("SKILLS ID MATCH FOUND");
                mapping.setSkillid(skillsFromDB.getSkillid());
            }
        }
        mapping.setRating(skillsFromUI.getRating());
//        System.out.println("SAVING TO MAPPING TABLE = MAPPING OBJECT = " + mapping.toString());
        return mapping;
    }


}
