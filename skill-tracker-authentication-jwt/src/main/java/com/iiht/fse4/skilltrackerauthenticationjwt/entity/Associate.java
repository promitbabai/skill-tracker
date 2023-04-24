package com.iiht.fse4.skilltrackerauthenticationjwt.entity;


import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;
import lombok.Setter;

import javax.persistence.Column;
import javax.persistence.Entity;
import javax.persistence.Id;
import javax.persistence.Table;


/**
 * The Associate is a model class which holds the data sent from the Angular UI,
 * to be persisted onto the MySqlDB
 *
 * @author  Promit Majumder
 * @version 1.0
 * @since   2023-01-10
 */

@AllArgsConstructor
@NoArgsConstructor
@Getter
@Setter
@Entity
@Table(name = "associate")
public class Associate {

    @Id
    @Column
    private String id;

    @Column
    private String associateid;

    @Column
    private String name;

    @Column
    private String mobile;

    @Column
    private String email;

    @Column
    private String lastupdated;

    @Column
    private String password;

    @Column
    private String admin;


    public String toString()
    {
        return "\n\n ASSOCIATE DATA\n" + id + " | " + associateid + " | " + name + " | " + mobile + " | " + email + " | "
                + lastupdated + " | " + password + " | " + admin;
    }
}
