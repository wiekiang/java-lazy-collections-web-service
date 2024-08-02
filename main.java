/*
 * Click nbfs://nbhost/SystemFileSystem/Templates/Licenses/license-default.txt to change this license
 */

package au.edu.cqu.App;

import jakarta.persistence.Entity;
import jakarta.persistence.GeneratedValue; 
import jakarta.persistence.Id;
import jakarta.persistence.ManyToMany; 
import jakarta.transaction.Transactional; 
import java.util.ArrayList;
import java.util.List; 
import lombok.Data;
import lombok.NoArgsConstructor; 
import lombok.NonNull;
import lombok.RequiredArgsConstructor;
import org.springframework.boot.ApplicationArguments; 
import org.springframework.boot.ApplicationRunner; 
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication; 
import org.springframework.core.annotation.Order;
import org.springframework.data.jpa.repository.JpaRepository; 
import org.springframework.stereotype.Component;

/**
 *
 * @author wiekiang
 */

@Entity @Data @NoArgsConstructor @RequiredArgsConstructor 
class Interest {
    @Id @GeneratedValue 
    private Long id; 
    
    @NonNull 
    private String name; 
    
    @ManyToMany(mappedBy = "interests")

    private List<Person> people = new ArrayList(); public void addPerson(Person person) {
        this.people.add(person);

        if (!person.getInterests().contains(this)) { 
            person.addInterest(this);
        }
    }
}

@Entity @Data @NoArgsConstructor @RequiredArgsConstructor 
class Person {
    @Id @GeneratedValue 
    private Long id; 
    
    @NonNull 
    private String name;

    @ManyToMany 
    private List<Interest> interests = new ArrayList(); 
    public void addInterest(Interest interest) {
        this.interests.add(interest);
        if (!interest.getPeople().contains(this)) { 
            interest.addPerson(this);
        }
    }
}

interface InterestRepository extends JpaRepository<Interest, Long> {} 
interface PersonRepository extends JpaRepository<Person, Long> {}

@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}

@Order(1)
@Component @Data @Transactional
class AppInit implements ApplicationRunner { 
    private final PersonRepository personRepository; 
    private final InterestRepository interestRepository; 
    
    public void run(ApplicationArguments aa) {
        Interest v = new Interest("Volleyball");  
        Interest a = new Interest("Art galleries"); 
        
        Person s = new Person("Sabrina");
        Person j = new Person("Jim"); 
        
        s.addInterest(v); 
        j.addInterest(v); 
        j.addInterest(a); 
        
        personRepository.save(s); 
        personRepository.save(j); 
        
        interestRepository.save(v); 
        interestRepository.save(a);
    }
}

@Order(2) 
@Component @Data @Transactional 
class AppInit2 implements ApplicationRunner {
    private final PersonRepository personRepository; 
    
    public void run(ApplicationArguments aa) {
        List<Person> people = personRepository.findAll(); 
        
        for (Person p : people) {
            System.out.println(p.getName() + ":"); 
            List<Interest> interests = p.getInterests(); 
            
            for (Interest i : interests) {
                System.out.println(" shares " + i.getName() + " with:");

                for (Person match : i.getPeople()) { 
                    if (match != p) {
                        System.out.println(" " + match.getName());
                    }
                }
            }
        }
    }
}
