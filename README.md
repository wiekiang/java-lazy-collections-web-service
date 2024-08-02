## Lazy Collections

This code is a Spring Boot application that demonstrates the use of JPA (Java Persistence API) with lazy collections and bidirectional relationships. Here’s a breakdown of its components:

### Entities
**Interest** and **Person** are JPA entities that represent the database tables.

#### Interest Entity
```
@Entity @Data @NoArgsConstructor @RequiredArgsConstructor 
class Interest {
    @Id @GeneratedValue 
    private Long id; 
    
    @NonNull 
    private String name; 
    
    @ManyToMany(mappedBy = "interests")
    private List<Person> people = new ArrayList(); 
    
    public void addPerson(Person person) {
        this.people.add(person);

        if (!person.getInterests().contains(this)) { 
            person.addInterest(this);
        }
    }
}
```

* **@Entity**: Marks this class as a JPA entity.
* **@Id** and **@GeneratedValue**: Specifies the primary key and its generation strategy.
* **@NonNull**: Ensures that the name field is not null.
* **@ManyToMany(mappedBy = "interests")**: Defines a many-to-many relationship with the **Person** entity. The **mappedBy** attribute indicates that **Interest** is the inverse side of the relationship.
* **addPerson(Person person)**: Adds a **Person** to the **Interest** and ensures the **Person** also adds this **Interest** to their own list.

#### Person Entity

```
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
```

* **@Entity**: Marks this class as a JPA entity.
* **@Id** and **@GeneratedValue**: Specifies the primary key and its generation strategy.
* **@NonNull**: Ensures that the **name** field is not null.
* **@ManyToMany**: Defines a many-to-many relationship with the **Interest** entity.
* **addInterest(Interest interest)**: Adds an **Interest** to the **Person** and ensures the **Interest** also adds this **Person** to its own list.

### Repositories

```
interface InterestRepository extends JpaRepository<Interest, Long> {} 
interface PersonRepository extends JpaRepository<Person, Long> {}
```

**InterestRepository** and **PersonRepository** are JPA repositories that provide CRUD operations for the Interest and Person entities, respectively.

### Spring Boot Application

```
@SpringBootApplication
public class App {
    public static void main(String[] args) {
        SpringApplication.run(App.class, args);
    }
}
```

* **@SpringBootApplication**: Marks this class as the entry point of the Spring Boot application.
* **main(String[] args)**: Starts the Spring Boot application.

### Application Initialization

```
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
```

* **@Order(1)**: Specifies the order in which this component is executed.
* **@Component**: Marks this class as a Spring component.
* **@Data**: Generates getters, setters, and other common methods.
* **@Transactional**: Ensures that the operations within run() are executed within a transaction.
* **run(ApplicationArguments aa)**: Creates and saves **Interest** and **Person** instances. It also establishes the relationships between **Person** and **Interest**.

```
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
```

* **@Order(2)**: Specifies the order in which this component is executed.
* **@Component**: Marks this class as a Spring component.
* **@Data**: Generates getters, setters, and other common methods.
* **@Transactional**: Ensures that the operations within **run()** are executed within a transaction.
* **run(ApplicationArguments aa)**: Fetches all **Person** entities and prints each **Person** and their interests, along with other people who share those interests.

### Key Points

* The **Interest** and **Person** entities are interconnected via a many-to-many relationship.
* **AppInit** initializes and saves the sample data.
* **AppInit2** retrieves and displays the data to show how interests are shared among people.
* **@Transactional** ensures that the database operations are executed within a transaction, which is crucial for consistency.

### Dependencies

```
<!-- Spring Boot Starter Data JPA -->
<dependency>
    <groupId>org.springframework.boot</groupId>
    <artifactId>spring-boot-starter-data-jpa</artifactId>
</dependency>

<!-- H2 Database (In-memory) -->
<dependency>
    <groupId>com.h2database</groupId>
    <artifactId>h2</artifactId>
    <scope>runtime</scope>
</dependency>

<!-- Lombok -->
<dependency>
    <groupId>org.projectlombok</groupId>
    <artifactId>lombok</artifactId>
    <scope>provided</scope>
</dependency>
```

### Things to Know

In the provided code:

* **@NoArgsConstructor**: Generates a no-argument constructor for both the **Interest** and **Person** classes. This is needed for JPA entities because JPA requires a default constructor to create instances of entities via reflection.
* **@RequiredArgsConstructor**: Generates a constructor that takes parameters for all fields marked as **@NonNull**. In this case, it creates constructors for **Interest** and **Person** classes that require the name field, which is marked as **@NonNull**.
