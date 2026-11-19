package com.springboot.spring_intro.repository;

import com.springboot.spring_intro.dto.StudentProjectionDto;
import com.springboot.spring_intro.entity.Student;
import com.springboot.spring_intro.projection.StudentProjection;
import com.springboot.spring_intro.repository.custom.StudentRepoCustom;
import org.springframework.data.jpa.repository.EntityGraph;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.JpaSpecificationExecutor;
import org.springframework.data.jpa.repository.Query;
import org.springframework.data.repository.query.Param;

import java.util.List;
import java.util.Optional;

public interface StudentRepo extends JpaRepository<Student, Long>,
        JpaSpecificationExecutor<Student>, StudentRepoCustom {

    // ---------derived Query----------
    boolean existsByEmail(String email);
    Optional<Student> findByEmail(String email);

    // -------------JPQL-------------
    @Query("""
            SELECT COUNT(s)
            FROM Student s
            JOIN s.courses c
            WHERE LOWER(c.courseName) = LOWER(:courseName) 
            """)
    Long countStudentByCourse(@Param("courseName") String courseName);
    void deleteByEmail(String email);

    @Query("""
           SELECT s FROM Student s
           JOIN s.courses c
           WHERE s.firstName = :firstName
           AND LOWER(c.courseName) = LOWER(:course) 
           """)
    List<Student> findByFirstNameAndCourse(String firstName, String course);

    List<Student> findByFirstNameContaining(String firstName);

    // ---------- Native Query -----------
    @Query(value = "SELECT * FROM students WHERE email=:email",
            nativeQuery = true)
    Optional<Student> findStudentByEmailNative(@Param("email") String email);

    List<StudentProjection> findBy();

    @Query("""
            SELECT new com.springboot.spring_intro.dto
            .StudentProjectionDto(
                        s.firstName,
                        s.lastName,
                        s.email
                        )
            FROM Student s
            """)
    List<StudentProjectionDto> getStudentProjection();

    // for N+1 Problem
//    @Query("""
//           SELECT distinct s
//           FROM Student s
//           LEFT join fetch s.courses
//           LEFT JOIN FETCH s.department
//           left join fetch s.address
//           """)
    @EntityGraph(attributePaths = {"address", "department", "courses"})
    @Query("SELECT s from Student s")
    List<Student> findAllWithDetails();


}
