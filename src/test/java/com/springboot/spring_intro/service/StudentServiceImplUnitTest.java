package com.springboot.spring_intro.service;

import com.springboot.spring_intro.dto.StudentResDto;
import com.springboot.spring_intro.entity.Address;
import com.springboot.spring_intro.entity.Department;
import com.springboot.spring_intro.entity.Student;
import com.springboot.spring_intro.exception.StudentNotFoundException;
import com.springboot.spring_intro.repository.StudentRepo;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertThrows;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class StudentServiceImplUnitTest {

    @Mock
    private StudentRepo studentRepo;

    @InjectMocks
    private StudentServiceImpl service;

    @Test
    void shouldReturnStudentById(){
        // Arrange
        Student student = new Student();
        student.setId(1L);
        student.setFirstName("Vishal");
        student.setLastName("Chaudhari");
        student.setEmail("chaudharivishal405@gmail.com");

        Address address = new Address();
        address.setCity("Surat");
        address.setState("Gujarati");
        address.setCountry("India");
        student.setAddress(address);

        Department department = new Department();
        department.setDepartmentName("CS");
        student.setDepartment(department);
        student.setCourses(List.of());

        when(studentRepo.findById(anyLong()))
                .thenReturn(Optional.of(student));

        // Act
        StudentResDto res = service.getStudentById(1L);

        // Assert
        assertEquals(1L, res.getId());
        assertEquals("Vishal", res.getFirstName());
        assertEquals("Chaudhari", res.getLastName());
        assertEquals("chaudharivishal405@gmail.com", res.getEmail());
        verify(studentRepo, times(1)).findById(1L);
//        studentRepo.deleteById(1L);
        verify(studentRepo, never()).deleteById(1L);

    }

    @Test
    void shouldThrowExceptionWhenRepositoryFails(){
        // Arrange
        when(studentRepo.findById(1L))
                .thenThrow(new RuntimeException());

        // Act
        RuntimeException exception = assertThrows(
                RuntimeException.class, () ->
                        service.getStudentById(1L)
        );
//        service.getStudentById(1L);

        // Assert
    }

    void shouldThrowExceptionWhenStudentNotFound(){
        // Arrange
        when(studentRepo.findById(99L))
                .thenReturn(Optional.empty());

        // Act & Assert
        assertThrows(
                StudentNotFoundException.class, () ->
                    service.getStudentById(99L)
        );

    }

}
