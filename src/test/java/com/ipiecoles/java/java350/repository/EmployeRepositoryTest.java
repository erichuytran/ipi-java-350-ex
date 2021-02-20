package com.ipiecoles.java.java350.repository;

import com.ipiecoles.java.java350.model.Employe;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;

@SpringBootTest
class EmployeRepositoryTest {

    @Autowired
    EmployeRepository employeRepository;

    @Test
    void testFindLastMatricule0Employe(){
        // Given

        // When
        String lastMatricule = employeRepository.findLastMatricule();

        // Then
        Assertions.assertThat(lastMatricule).isNull();
    }

    @Test
    void testFindLastMatricule1Employe() {
        // Given
        // Inserer des données en base
        employeRepository.save(new Employe("Doe", "John", "T12345", LocalDate.now(), 1500d, 1, 1.0));

        // When
        // Excecuter les requetes en base
        String lastMatricule = employeRepository.findLastMatricule();

        // Then
        Assertions.assertThat(lastMatricule).isEqualTo("12345");
    }

    @Test
    void testFindLastMatriculeNEmploye() {
        // Given
        // Inserer des données en base
        employeRepository.save(new Employe("Doe", "John", "T12345", LocalDate.now(), 1500d, 1, 1.0));
        employeRepository.save(new Employe("Doe", "Jane", "M40325", LocalDate.now(), 1500d, 1, 1.0));
        employeRepository.save(new Employe("Doe", "Jim", "C05432", LocalDate.now(), 1500d, 1, 1.0));

        // When
        // Excecuter les requetes en base
        String lastMatricule = employeRepository.findLastMatricule();

        // Then
        Assertions.assertThat(lastMatricule).isEqualTo("40325");
    }

    @BeforeEach
    @AfterEach
    public void purgeBDD() {
        employeRepository.deleteAll();
    }

}
