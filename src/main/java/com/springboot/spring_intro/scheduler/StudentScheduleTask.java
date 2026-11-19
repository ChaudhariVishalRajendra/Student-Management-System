package com.springboot.spring_intro.scheduler;

import com.springboot.spring_intro.repository.StudentRepo;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.springframework.scheduling.annotation.Scheduled;
import org.springframework.stereotype.Component;

@Slf4j
@Component
@RequiredArgsConstructor
public class StudentScheduleTask {

    private final StudentRepo repo;

//    @Scheduled(fixedDelay = 10000)
//    @Scheduled(fixedRate = 10000) // 10Sec
//    @Scheduled(cron = "*/10 * * * * *")
    public void printStudentCount(){
        long studentCount = repo.count();
        log.info("Scheduled Task: Total Students = {} ", studentCount);

//        try {
//            Thread.sleep(3000);
//        } catch (InterruptedException e) {
//            Thread.currentThread().interrupt();
//        }
    }

}
