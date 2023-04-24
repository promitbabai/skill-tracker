package com.iiht.fse4.skilltrackerauth.repo;

import com.iiht.fse4.skilltrackerauth.entity.Associate;
import com.iiht.fse4.skilltrackerauth.entity.Mapping;
import com.iiht.fse4.skilltrackerauth.entity.Skills;
import org.springframework.data.jpa.repository.JpaRepository;

import java.util.List;


public interface SkillsRepository extends JpaRepository<Skills, String> {


}




