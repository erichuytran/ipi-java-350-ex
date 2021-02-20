package com.ipiecoles.java.java350.service;
import com.ipiecoles.java.java350.exception.EmployeException;
import com.ipiecoles.java.java350.model.Employe;
import com.ipiecoles.java.java350.model.NiveauEtude;
import com.ipiecoles.java.java350.model.Poste;
import com.ipiecoles.java.java350.repository.EmployeRepository;
import org.assertj.core.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.*;
import org.mockito.junit.jupiter.MockitoExtension;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;

import javax.persistence.EntityExistsException;
import java.time.LocalDate;
import java.util.List;


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
}
