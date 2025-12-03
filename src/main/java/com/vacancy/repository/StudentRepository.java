package com.vacancy.repository;

import com.vacancy.model.Student;
import org.springframework.data.jpa.repository.JpaRepository;
import org.springframework.data.jpa.repository.Modifying;
import org.springframework.data.jpa.repository.Query;
import org.springframework.stereotype.Repository;
import org.springframework.transaction.annotation.Transactional;

/**
 * Репозиторий для работы с сущностью Student.
 */
@Repository
public interface StudentRepository extends JpaRepository<Student, Integer> {
    
    /**
     * Находит студентов по email
     * @param email email студента
     * @return список студентов с указанным email
     */
    Student findByEmail(String email);
    
    /**
     * Находит студентов по имени
     * @param name имя студента
     * @return список студентов с указанным именем
     */
    java.util.List<Student> findByName(String name);
}