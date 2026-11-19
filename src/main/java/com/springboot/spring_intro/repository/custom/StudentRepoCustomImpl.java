package com.springboot.spring_intro.repository.custom;

import com.springboot.spring_intro.entity.Student;
import jakarta.persistence.EntityManager;
import jakarta.persistence.PersistenceContext;
import jakarta.persistence.TypedQuery;

import java.util.List;

public class StudentRepoCustomImpl implements StudentRepoCustom {

    @PersistenceContext
    private EntityManager entityManager;

    @Override
    public List<Student> findStudentCustom() {
        TypedQuery<Student> query = entityManager.createQuery("SELECT s FROM Student s", Student.class);
        return query.getResultList();
    }

    @Override
    public List<Student> findStudentByDepartment(String departmentName) {
        TypedQuery<Student> query = entityManager.createQuery
                ("SELECT s FROM Student s WHERE s.department.departmentName=:departmentName", Student.class);
        query.setParameter("departmentName",departmentName);
        return query.getResultList();
    }
}
