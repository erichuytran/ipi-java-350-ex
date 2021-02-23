package com.ipiecoles.java.java350.service;
import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.junit.jupiter.params.ParameterizedTest;
import org.junit.jupiter.params.provider.CsvSource;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;


@ExtendWith(MockitoExtension.class)
class EmployeServiceTest {

    @InjectMocks
    private EmployeService employeService;

    @Mock
    private EmployeRepository employeRepository;

    @Test
    void testEmbauchePremierEmploye() throws EmployeException {
        // Given Pas d'employés en base
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;

        Mockito.when(employeRepository.save(Mockito.any(Employe.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

        // Simuler qu'aucun employé n'est présent (ou du moins aucun matricule)
        Mockito.when(employeRepository.findLastMatricule()).thenReturn(null);
        // Simuler que la recherche par matricule ne renvoie pas de résultats
        Mockito.when(employeRepository.findByMatricule("T00001")).thenReturn(null);
        // When
        employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);

        // Then
        ArgumentCaptor<Employe> employeArgumentCaptor = ArgumentCaptor.forClass(Employe.class);
//        Mockito.verify(employeRepository, Mockito.times(1)).save(employeArgumentCaptor.capture());
        Mockito.verify(employeRepository).save(employeArgumentCaptor.capture());
        Employe employe = employeArgumentCaptor.getValue();
        Assertions.assertThat(employe).isNotNull();
        Assertions.assertThat(employe.getNom()).isEqualTo(nom);
        Assertions.assertThat(employe.getPrenom()).isEqualTo(prenom);
        Assertions.assertThat(employe.getSalaire()).isEqualTo(1825.46);
        Assertions.assertThat(employe.getTempsPartiel()).isEqualTo(1.0);
        Assertions.assertThat(employe.getDateEmbauche()).isEqualTo(LocalDate.now());
        Assertions.assertThat(employe.getMatricule()).isEqualTo("T00001");
    }

    @Test
    void testEmbaucheLimiteMatricule() throws EmployeException {
        // Given Pas d'employés en base
        String nom = "Doe";
        String prenom = "John";
        Poste poste = Poste.TECHNICIEN;
        NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
        Double tempsPartiel = 1.0;
        // Simuler qu'il y a 99999 employés en base (ou du moins que e matricule le plus hait est X99999
        Mockito.when(employeRepository.findLastMatricule()).thenReturn("99999");

        // When
        try {
            employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
            Assertions.fail("embaucheEmploye aurait du lancer une exception");
        } catch (EmployeException e) {
            // Then
            Assertions.assertThat(e.getMessage()).isEqualTo("Limite des 100000 matricules atteinte !");
            Mockito.verify(employeRepository, Mockito.never()).save(Mockito.any(Employe.class));
        }
    }

    @Test
    void testEmbaucheExisteDeja() throws EmployeException {
         // Given Pas d'employés en base
         String nom = "Doe";
         String prenom = "John";
         Poste poste = Poste.TECHNICIEN;
         NiveauEtude niveauEtude = NiveauEtude.BTS_IUT;
         Double tempsPartiel = 1.0;
         Employe employeExistant = new Employe("Doe", "Jane", "T00001", LocalDate.now(), 1500d, 1, 1.0);
         // Simuler qu'aucun employé n'est présent (ou du moins aucun matricule)
         Mockito.when(employeRepository.findLastMatricule()).thenReturn(null);
         // Simuler que la recherche par matricule renvoie un employé (un employé a été embauché entre temps)
         Mockito.when(employeRepository.findByMatricule("T00001")).thenReturn(employeExistant);
         // When
         try {
             employeService.embaucheEmploye(nom, prenom, poste, niveauEtude, tempsPartiel);
             Assertions.fail("embaucheEmploye aurait dû lancer une exception");
         } catch (Exception e) {
             // Then
             Assertions.assertThat(e).isInstanceOf(EntityExistsException.class);
             Assertions.assertThat(e.getMessage()).isEqualTo("L'employé de matricule T00001 existe déjà en BDD");
             Mockito.verify(employeRepository, Mockito.never()).save(Mockito.any(Employe.class));
         }
    }

    @ParameterizedTest(name = "caTraite {0}, performanceCommercial {1}, performanceExpected {2}")
    @CsvSource({
            " 50, 2, 1", // Si le chiffre d'affaire est inférieur de plus de 20% à l'objectif fixé, le commercial retombe à la performance de base
            " 90, 2, 1", // Si le chiffre d'affaire est inférieur entre 20% et 5% par rapport à l'ojectif fixé, il perd 2 de performance (dans la limite de la performance de base)
            " 90, 5, 3", // Si le chiffre d'affaire est inférieur entre 20% et 5% par rapport à l'ojectif fixé, il perd 2 de performance (dans la limite de la performance de base)
            " 101, 4, 4", // Si le chiffre d'affaire est entre -5% et +5% de l'objectif fixé, la performance reste la même.
            " 110, 2, 3", // Si le chiffre d'affaire est supérieur entre 5 et 20%, il gagne 1 de performance
            " 150, 5, 9" // Si le chiffre d'affaire est supérieur de plus de 20%, il gagne 4 de performance
    })
    void testCalculPerformanceCommercialPerfMoyenneIsHigher(Long caTraite, Integer performanceCommercial , Integer performanceExpected) throws EmployeException {
        String matricule = "C12345";
        Employe commercial = new Employe("Doe", "John", matricule, LocalDate.now(), 2000d, performanceCommercial, 1.0);
        Long caObjectif = 100L;
        Mockito.when(employeRepository.findByMatricule(matricule)).thenReturn(commercial);
        Mockito.when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(999999d);
        Mockito.when(employeRepository.save(Mockito.any(Employe.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

        employeService.calculPerformanceCommercial(matricule, caTraite, caObjectif);

        ArgumentCaptor<Employe> commercialCaptor = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository).save(commercialCaptor.capture());
        Employe commercialPerformanceUpdated = commercialCaptor.getValue();
        Assertions.assertThat(commercialPerformanceUpdated.getPerformance()).isEqualTo(performanceExpected);
    }

    @ParameterizedTest(name = "caTraite {0}, performanceCommercial {1}, performanceExpected {2}")
    @CsvSource({
            " 50, 2, 1", // Si le chiffre d'affaire est inférieur de plus de 20% à l'objectif fixé, le commercial retombe à la performance de base
            " 90, 2, 1", // Si le chiffre d'affaire est inférieur entre 20% et 5% par rapport à l'ojectif fixé, il perd 2 de performance (dans la limite de la performance de base)
            " 90, 5, 4", // Si le chiffre d'affaire est inférieur entre 20% et 5% par rapport à l'ojectif fixé, il perd 2 de performance (dans la limite de la performance de base)
            " 101, 4, 5", // Si le chiffre d'affaire est entre -5% et +5% de l'objectif fixé, la performance reste la même.
            " 110, 2, 4", // Si le chiffre d'affaire est supérieur entre 5 et 20%, il gagne 1 de performance
            " 150, 5, 10" // Si le chiffre d'affaire est supérieur de plus de 20%, il gagne 4 de performance
    })
    void testCalculPerformanceCommercialPerfMoyenneIsLower(Long caTraite, Integer performanceCommercial , Integer performanceExpected) throws EmployeException {
        String matricule = "C12345";
        Employe commercial = new Employe("Doe", "John", matricule, LocalDate.now(), 2000d, performanceCommercial, 1.0);
        Long caObjectif = 100L;
        Mockito.when(employeRepository.findByMatricule(matricule)).thenReturn(commercial);
        Mockito.when(employeRepository.avgPerformanceWhereMatriculeStartsWith("C")).thenReturn(1d);
        Mockito.when(employeRepository.save(Mockito.any(Employe.class))).thenAnswer(AdditionalAnswers.returnsFirstArg());

        employeService.calculPerformanceCommercial(matricule, caTraite, caObjectif);

        ArgumentCaptor<Employe> commercialCaptor = ArgumentCaptor.forClass(Employe.class);
        Mockito.verify(employeRepository).save(commercialCaptor.capture());
        Employe commercialPerformanceUpdated = commercialCaptor.getValue();
        Assertions.assertThat(commercialPerformanceUpdated.getPerformance()).isEqualTo(performanceExpected);
    }

    @Test
    void testCheckEnteringParamsMatriculeIsNullOrDoesNotStartWithC() {
        String matriculeNull = null;
        String matriculeNotC = "T12345";

        try {
            employeService.checkEnteringParams(matriculeNull, 1L, 1L);
            employeService.checkEnteringParams(matriculeNotC, 1L, 1L);
            Assertions.fail("checkEnteringParams aurait du lancer une exception");
        } catch (EmployeException e) {
            Assertions.assertThat(e.getMessage()).isEqualTo("Le matricule ne peut être null et doit commencer par un C !");
        }
    }

    @Test
    void testCheckEnteringParamsCaTraiteIsNullOrNegative() {
        Long caTraiteNull = null;
        Long caTraiteNegative = -5L;

        try {
            employeService.checkEnteringParams("C12345", caTraiteNull, 1L);
            employeService.checkEnteringParams("C12345", caTraiteNegative, 1L);
            Assertions.fail("checkEnteringParams aurait du lancer une exception");
        } catch (EmployeException e) {
            Assertions.assertThat(e.getMessage()).isEqualTo("Le chiffre d'affaire traité ne peut être négatif ou null !");
        }
    }

    @Test
    void testCheckEnteringParamsObjectfCaIsNullOrNegative() {
        Long objectifCaNull = null;
        Long objectifCaNegative = -5L;

        try {
            employeService.checkEnteringParams("C12345", 1L, objectifCaNull);
            employeService.checkEnteringParams("C12345", 1L, objectifCaNegative);
            Assertions.fail("checkEnteringParams aurait du lancer une exception");
        } catch (EmployeException e) {
            Assertions.assertThat(e.getMessage()).isEqualTo("L'objectif de chiffre d'affaire ne peut être négatif ou null !");
        }
    }

}
