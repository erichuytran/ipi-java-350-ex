package com.ipiecoles.java.java350.model;

import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;

import java.time.LocalDate;

class EmployeTest {

    @Test
    void testGetNombreAncienneteDateEmbaucheSupNow() {
        // Given
        Employe employe = new Employe("Doe", "John", "T12345", LocalDate.now().plusYears(1), 1500d, 1, 1.0);

        // When
        Integer anneesAnciennete = employe.getNombreAnneeAnciennete();

        // Then
        Assertions.assertThat(anneesAnciennete).isNull();
    }

    @Test
    void testGetNombreAncienneteDateEmbaucheInfNow() {
        // Given
        Employe employe = new Employe("Doe", "John", "T12345", LocalDate.now().minusYears(2), 1500d, 1, 1.0);

        // When
        Integer anneesAnciennete = employe.getNombreAnneeAnciennete();

        // Then
        Assertions.assertThat(anneesAnciennete).isEqualTo(2);
    }

    @Test
    void testGetNombreAncienneteDateEmbaucheCurrYear() {
        // Given
        Employe employe = new Employe("Doe", "John", "T12345", LocalDate.now(), 1500d, 1, 1.0);

        // When
        Integer anneesAnciennete = employe.getNombreAnneeAnciennete();

        // Then
        Assertions.assertThat(anneesAnciennete).isZero();
    }

    @Test
    void testGetNombreAncienneteDateEmbaucheIsNull() {
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
    void testGetPrimeAnnuelle(Integer performance, String matricule, Double tauxActivite,  Long nbAnneesAnciennetee, Double primeAttendue) {
        // Given

        Employe employe = new Employe("Doe", "John", matricule, LocalDate.now().minusYears(nbAnneesAnciennetee), 1500d, performance, tauxActivite);

        // When
        Double prime = employe.getPrimeAnnuelle();

        // Then
        Assertions.assertThat(prime).isEqualTo(primeAttendue);

    }

    @Test
    void testAugmenterSalaireBy3Percent() {
        Employe employe = new Employe("Doe", "John", "T12345", LocalDate.now(), 1500d, 1, 1.0);

        Double salaireExpected = employe.getSalaire() * 3/100 + employe.getSalaire();
        employe.augmenterSalaire(3);

        Assertions.assertThat(employe.getSalaire()).isEqualTo(salaireExpected);
    }

    @Test
    void testAugmenterSalaireBy0Percent() {
        Employe employe = new Employe("Doe", "John", "T12345", LocalDate.now(), 1500d, 1, 1.0);

        Double salaireBefore = employe.getSalaire();
        employe.augmenterSalaire(0);

        Assertions.assertThat(employe.getSalaire()).isEqualTo(salaireBefore);
    }

    @Test
    void testAugmenterSalaireByDot3Percent() {
        Employe employe = new Employe("Doe", "John", "T12345", LocalDate.now(), 1500d, 1, 1.0);

        Double salaireExpected = employe.getSalaire() * 0.3/100 + employe.getSalaire();

        employe.augmenterSalaire(0.3);

        Assertions.assertThat(employe.getSalaire()).isEqualTo(salaireExpected);
    }

    @Test
    void testAugmenterSalaireByNegativePercent() {
        Employe employe = new Employe("Doe", "John", "T12345", LocalDate.now(), 1500d, 1, 1.0);

        Double salaireBefore = employe.getSalaire();
        try {
            employe.augmenterSalaire(-10);
            Assertions.fail("augmenterSalaire aurait du lancer une exception");
        } catch (IllegalArgumentException e){
            Assertions.assertThat(employe.getSalaire()).isEqualTo(salaireBefore);
        }
    }

    @ParameterizedTest(name = "year {0}, nbDaysRttExpected {1}, tempsPartiel {2}")
    @CsvSource({
            "2019, 8, 1",
            "2021, 10, 1",
            "2022, 10, 1",
            "2026, 9, 1",
            "2032, 11, 1",
            "2019, 4, 0.5",
            "2021, 5, 0.5",
            "2022, 5, 0.5",
            "2026, 5, 0.5",
            "2032, 6, 0.5"
    })
    void testGetNbRtt(Integer year, Integer nbDaysRttExpected, Double tempsPartiel) {
        Employe employe = new Employe("Doe", "John", "T12345", LocalDate.now(), 1500d, 1, tempsPartiel);

        Integer nbDaysRtt = employe.getNbRtt(LocalDate.of(year, 1, 01));

        Assertions.assertThat(nbDaysRtt).isEqualTo(nbDaysRttExpected);
    }

//    @Test
//    void testGetPrimeAnnuelleMatriculeNull() {
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
