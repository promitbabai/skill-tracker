package com.iiht.fse4.skilltrackerbatch.entity;



/**
 * The Associate is a model class which holds the data sent pulled from the CSV file,
 * to be persisted onto the MySqlDB
 *
 * @author  Promit Majumder
 * @version 1.0
 * @since   2023-03-10
 */

public class Associate {

    private String id;
    private String associateid;
    private String name;
    private String mobile;
    private String email;
    private String lastupdated;


    public Associate() {
    }


    public Associate(String associateid, String name, String mobile, String email, String lastupdated) {
        this.associateid = associateid;
        this.name = name;
        this.mobile = mobile;
        this.email = email;
        this.lastupdated = lastupdated;
    }

    public String getName() {
        return name;
    }

    public void setName(String name) {
        this.name = name;
    }

    public String getAssociateid() {
        return associateid;
    }

    public void setAssociateid(String associateid) {
        this.associateid = associateid;
    }

    public String getMobile() {
        return mobile;
    }

    public void setMobile(String mobile) {
        this.mobile = mobile;
    }

    public String getEmail() {
        return email;
    }

    public void setEmail(String email) {
        this.email = email;
    }

    public String getLastupdated() {
        return lastupdated;
    }

    public void setLastupdated(String lastupdated) {
        this.lastupdated = lastupdated;
    }

    public String toString()
    {
        return "ASSOCIATE DATA = | " + associateid + " | " + name + " | " + mobile + " | " + email + " | "
                + lastupdated ;
    }
}
