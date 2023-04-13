package com.iiht.fse4.skilltrackersearch.kafkaconfig;


import com.iiht.fse4.skilltrackersearch.entity.Associate;
import com.iiht.fse4.skilltrackersearch.model.Profile;
import com.iiht.fse4.skilltrackersearch.repo.AssociateRepository;
import com.iiht.fse4.skilltrackersearch.service.AssociateService;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.kafka.annotation.KafkaListener;
import org.springframework.stereotype.Service;



/**
 * This is the Kafka Consumer Class that that will listen to the Kafka Queue for incoming messages.
 * The Incoming messages will be the JSON entity Object
 *
 * Please see the {@link com.iiht.fse4.skilltrackersearch.entity.Associate} class for true identity
 * @author Promit Majumder
 *
 */

@Service
public class KafkaConsumer {

    private static final Logger LOGGER = LoggerFactory.getLogger(KafkaConsumer.class);

    @Autowired
    private AssociateRepository repo;

    @Autowired
    private AssociateService service;

    @KafkaListener(
            topics = "${spring.kafka.topic.name}"
            ,groupId = "${spring.kafka.consumer.group-id}"
    )


    public void consume(KafkaMessage kafkaMessage){
        LOGGER.info(String.format("Course event received in course-query service => %s", kafkaMessage.toString()));
        System.out.println("event++++++++++++++++++");
        System.out.println(kafkaMessage.getProfile().getName());
        service.saveProfileFromCQRS(kafkaMessage);
    }



//    private void deleteCourseEntity(CourseEvent event) {
//        Course courseFromEvent = event.getCourse();
//        Course courseToBeDeleted = courseRepository.findByName(courseFromEvent.getName());
//        if (courseToBeDeleted == null) {
//            LOGGER.info("No course available with coursename to be deleted.");
//        }
//        courseRepository.delete(courseToBeDeleted);
//    }
//
//    private void saveCourseEntity(CourseEvent event) {
//        LOGGER.info(String.format("Inside saveCourseEntity"));
//        Course courseFromEvent = event.getCourse();
//
//        Course courseByName = courseRepository.findByName(courseFromEvent.getName());
//        Course savedOrUpdated = null;
//        if (courseByName != null) {
//            courseFromEvent.setId(courseByName.getId());
//            savedOrUpdated = courseRepository.save(courseFromEvent);
//            LOGGER.info(String.format("Course updated in query database. Course name => %s", courseFromEvent.getName()));
//        }else{
//            savedOrUpdated = courseRepository.save(courseFromEvent);
//        }
//    }
}