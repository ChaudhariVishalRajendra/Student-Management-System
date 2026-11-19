package com.springboot.spring_intro.service;

import com.springboot.spring_intro.dto.StudentResDto;
import com.springboot.spring_intro.entity.Address;
import com.springboot.spring_intro.entity.Course;
import com.springboot.spring_intro.entity.Department;
import com.springboot.spring_intro.entity.Student;
import com.springboot.spring_intro.exception.StudentNotFoundException;
import com.springboot.spring_intro.repository.CourseRepo;
import com.springboot.spring_intro.repository.DepartmentRepo;
import com.springboot.spring_intro.repository.StudentRepo;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

import java.util.List;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;

@SpringBootTest
@ActiveProfiles("test")
@Transactional
public class StudentServiceImplIntegrationTest {

    @Autowired
    private StudentRepo studentRepo;

    @Autowired
    private DepartmentRepo departmentRepo;

    @Autowired
    private CourseRepo courseRepo;

    @Autowired
    private StudentService service;

    @Test
    void shouldReturnStudentById(){
        // Arrange
        Department department = new Department();
        department.setDepartmentName("Computer Science");
        department = departmentRepo.save(department);

        Course course = new Course();
        course.setCourseName("Spring-Boot");
        course.setDuration("6 Months");
        course.setFees(25000.0);
        course.setInstructorName("Vishal");
        course = courseRepo.save(course);

        Address address = new Address();
        address.setCity("Surat");
        address.setState("Gujarati");
        address.setCountry("India");

        Student student = new Student();
        student.setFirstName("Vishal");
        student.setLastName("Chaudhari");
        student.setEmail("chaudharivishal123@gmail.com");
        student.setPassword("123456");
        student.setAddress(address);
        student.setDepartment(department);
        student.setCourses(List.of(course));
        Student savedStudent = studentRepo.save(student);

        // Act
        StudentResDto res = service.getStudentById(savedStudent.getId());

        // Assert
        assertEquals(savedStudent.getId(), res.getId());
        assertEquals("Vishal", res.getFirstName());
        assertEquals("Chaudhari", res.getLastName());
        assertEquals("chaudharivishal123@gmail.com", res.getEmail());
        assertEquals("Surat", res.getAddress().getCity());
        assertEquals("Gujarati", res.getAddress().getState());
        assertEquals("India", res.getAddress().getCountry());
        assertEquals("Computer Science", res.getDepartmentName());
        assertEquals(1,res.getCourses().size());
        assertEquals("Spring-Boot", res.getCourses().get(0).getCourseName());

    }

    void shouldThrowException(){
        assertThrows(
                StudentNotFoundException.class,() ->
                        service.getStudentById(999999L)
        );
    }

}
