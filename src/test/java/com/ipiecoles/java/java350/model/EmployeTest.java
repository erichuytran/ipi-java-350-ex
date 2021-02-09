package com.ipiecoles.java.java350.model;

import com.ipiecoles.java.java350.model.Employe;
import org.assertj.core.api.Assertions;
import org.junit.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.junit.runner.RunWith;
import org.junit.runners.Parameterized;

import java.time.LocalDate;

public class EmployeTest {

    @Test
    public void testGetNombreAncienneteDateEmbaucheSupNow() {
        // Given
        Employe employe = new Employe("Doe", "John", "T12345", LocalDate.now().plusYears(1), 1500d, 1, 1.0);

        // When
        Integer anneesAnciennete = employe.getNombreAnneeAnciennete();

        // Then
        Assertions.assertThat(anneesAnciennete).isNull();
    }

    @Test
    public void testGetNombreAncienneteDateEmbaucheInfNow() {
        // Given
        Employe employe = new Employe("Doe", "John", "T12345", LocalDate.now().minusYears(2), 1500d, 1, 1.0);

        // When
        Integer anneesAnciennete = employe.getNombreAnneeAnciennete();

        // Then
        Assertions.assertThat(anneesAnciennete).isEqualTo(2);
    }

    @Test
    public void testGetNombreAncienneteDateEmbaucheCurrYear() {
        // Given
        Employe employe = new Employe("Doe", "John", "T12345", LocalDate.now(), 1500d, 1, 1.0);

        // When
        Integer anneesAnciennete = employe.getNombreAnneeAnciennete();

        // Then
        Assertions.assertThat(anneesAnciennete).isEqualTo(0);
    }

    @Test
    public void testGetNombreAncienneteDateEmbaucheIsNull() {
        // Given
        Employe employe = new Employe("Doe", "John", "T12345", null, 1500d, 1, 1.0);

        // When
        Integer anneesAnciennete = employe.getNombreAnneeAnciennete();

        // Then
        Assertions.assertThat(anneesAnciennete).isNull();
    }

    @ParameterizedTest(name = "perf {0}, matricule {1}, txActivite {2}, anciennete {3} => prime {4}")
    @CsvSource({
            "1, 'T12345', 1.0, 0, 1000.0",
            "1, 'T12345', 0.5, 0, 500.0",
            "2, 'T12345', 1.0, 0, 2300.0",
            "1, 'T12345', 1.0, 2, 1200.0"
    })
    public void testGetPrimeAnnuelle(Integer performance, String matricule, Double tauxActivite,  Long nbAnneesAnciennetee, Double primeAttendue) {
        // Given

        Employe employe = new Employe("Doe", "John", matricule, LocalDate.now().minusYears(nbAnneesAnciennetee), 1500d, performance, tauxActivite);

        // When
        Double prime = employe.getPrimeAnnuelle();

        // Then
        Assertions.assertThat(prime).isEqualTo(primeAttendue);

    }

//    @Test
//    public void testGetPrimeAnnuelleMatriculeNull() {
//        // Given
//        Employe employe = new Employe("Doe", "John", null, null, 1500d, 1, 1.0);
//
//        // When
//        Double prime = employe.getPrimeAnnuelle();
//
//        // Then
//        Assertions.assertThat(prime).isNull();
//    }

}
