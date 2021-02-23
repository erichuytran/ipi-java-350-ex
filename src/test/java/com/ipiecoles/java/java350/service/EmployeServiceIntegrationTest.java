package com.ipiecoles.java.java350.service;


import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import java.time.LocalDate;
import java.util.List;

@SpringBootTest
class EmployeServiceIntegrationTest {

    @Autowired
    EmployeService employeService;

    @Autowired
    EmployeRepository employeRepository;

    @Test
    void test() throws EmployeException {
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;

        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        List<Employe> employes = employeRepository.findAll();
        Assertions.assertThat(employes).hasSize(1);
        Employe employe = employeRepository.findAll().get(0);
        Assertions.assertThat(employe.getNom()).isEqualTo("Doe");
        Assertions.assertThat(employe.getPrenom()).isEqualTo("John");
        Assertions.assertThat(employe.getSalaire()).isEqualTo(1825.46);
        Assertions.assertThat(employe.getTempsPartiel()).isEqualTo(1.0);
        Assertions.assertThat(employe.getDateEmbauche()).isEqualTo(LocalDate.now());
        Assertions.assertThat(employe.getMatricule()).isEqualTo("T00001");
    }

    @ParameterizedTest(name = "caTraite {0}, performanceCommercial {1}, performanceExpected {2}")
    @CsvSource({
            " 50, 2, 1", // Si le chiffre d'affaire est inférieur de plus de 20% à l'objectif fixé, le commercial retombe à la performance de base
            " 90, 2, 1", // Si le chiffre d'affaire est inférieur entre 20% et 5% par rapport à l'ojectif fixé, il perd 2 de performance (dans la limite de la performance de base)
            " 101, 4, 4", // Si le chiffre d'affaire est entre -5% et +5% de l'objectif fixé, la performance reste la même.
            " 110, 2, 4", // Si le chiffre d'affaire est supérieur entre 5 et 20%, il gagne 1 de performance (+ 1 de performance puisqu'au dessus de la moyenne [100])
            " 150, 5, 10" // Si le chiffre d'affaire est supérieur de plus de 20%, il gagne 4 de performance (+ 1 de performance puisqu'au dessus de la moyenne [100])
    })
    void testCalculPerformanceCommercialIntegration(Long caTraite, Integer performanceCommercial , Integer performanceExpected) throws EmployeException {
        Long caObjectif = 100L;

        //Je ne comprends pas pourquoi cette solution ne passe pas tous les tests
        /*
        employeService.embaucheEmploye("Doe", "John", Poste.COMMERCIAL, NiveauEtude.LICENCE, 1.0);
        Employe commercial = employeRepository.findAll().get(0);
        commercial.setPerformance(performanceCommercial);
        */

        String matricule = "C12345";
        employeRepository.save(new Employe("Doe", "John", matricule, LocalDate.now(), 1500d, performanceCommercial, 1.0));

        employeService.calculPerformanceCommercial(matricule, caTraite, caObjectif);

        Employe commercial = employeRepository.findByMatricule(matricule);
        Assertions.assertThat(commercial.getPerformance()).isEqualTo(performanceExpected);
    }

    @BeforeEach
    @AfterEach
    void purgeBDD() {
        employeRepository.deleteAll();
    }

}