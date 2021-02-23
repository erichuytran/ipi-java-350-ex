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

    @Test
    void testAvgPerformanceWhereMatriculeStartsWithTechnicien() {
        Integer perfTech1 = 2;
        Integer perfTech2 = 5;
        Integer perfTech3 = 8;
        Double avgPerfExpected = (perfTech1 + perfTech2+ perfTech3) / 3d;
        employeRepository.save(new Employe("Doe1", "John1", "T00001", LocalDate.now(), 1500d, perfTech1, 1.0));
        employeRepository.save(new Employe("Doe2", "John2", "T00002", LocalDate.now(), 1500d, perfTech2, 1.0));
        employeRepository.save(new Employe("Doe3", "John3", "T00003", LocalDate.now(), 1500d, perfTech3, 1.0));

        Double avgPerf = employeRepository.avgPerformanceWhereMatriculeStartsWith("T");

        Assertions.assertThat(avgPerf).isEqualTo(avgPerfExpected);
    }

    @Test
    void testAvgPerformanceWhereMatriculeStartsWithCommercial() {
        Integer perfComm1 = 4;
        Integer perfComm2 = 2;
        Integer perfComm3 = 1;
        Double avgPerfExpected = (perfComm1 + perfComm2+ perfComm3) / 3d;
        employeRepository.save(new Employe("Doe1", "John1", "C00001", LocalDate.now(), 1500d, perfComm1, 1.0));
        employeRepository.save(new Employe("Doe2", "John2", "C00002", LocalDate.now(), 1500d, perfComm2, 1.0));
        employeRepository.save(new Employe("Doe3", "John3", "C00003", LocalDate.now(), 1500d, perfComm3, 1.0));

        Double avgPerf = employeRepository.avgPerformanceWhereMatriculeStartsWith("C");

        Assertions.assertThat(avgPerf).isEqualTo(avgPerfExpected);
    }

    @Test
    void testAvgPerformanceWhereMatriculeStartsWithManager() {
        Integer perfMana1 = 8;
        Integer perfMana2 = 7;
        Integer perfMana3 = 7;
        Double avgPerfExpected = (perfMana1 + perfMana2+ perfMana3) / 3d;
        employeRepository.save(new Employe("Doe1", "John1", "M00001", LocalDate.now(), 1500d, perfMana1, 1.0));
        employeRepository.save(new Employe("Doe2", "John2", "M00002", LocalDate.now(), 1500d, perfMana2, 1.0));
        employeRepository.save(new Employe("Doe3", "John3", "M00003", LocalDate.now(), 1500d, perfMana3, 1.0));

        Double avgPerf = employeRepository.avgPerformanceWhereMatriculeStartsWith("M");

        Assertions.assertThat(avgPerf).isEqualTo(avgPerfExpected);
    }

    @BeforeEach
    @AfterEach
    public void purgeBDD() {
        employeRepository.deleteAll();
    }

}
