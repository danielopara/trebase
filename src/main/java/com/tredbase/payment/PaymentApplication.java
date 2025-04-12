package com.tredbase.payment;

import com.tredbase.payment.entity.Parent;
import com.tredbase.payment.entity.Role;
import com.tredbase.payment.entity.Student;
import com.tredbase.payment.entity.UserModel;
import com.tredbase.payment.jwt.JwtAuthService;
import com.tredbase.payment.jwt.JwtService;
import com.tredbase.payment.repository.ParentRepository;
import com.tredbase.payment.repository.StudentRepository;
import com.tredbase.payment.repository.UserRepository;
import org.springframework.boot.CommandLineRunner;
import org.springframework.boot.SpringApplication;
import org.springframework.boot.autoconfigure.SpringBootApplication;
import org.springframework.context.annotation.Bean;
import org.springframework.security.crypto.password.PasswordEncoder;

import java.math.BigDecimal;
import java.util.HashSet;
import java.util.Set;
import java.util.UUID;


@SpringBootApplication
public class PaymentApplication {
	private final JwtService jwtService;

    public PaymentApplication(JwtService jwtService) {
        this.jwtService = jwtService;
    }

    public static void main(String[] args) {
		SpringApplication.run(PaymentApplication.class, args);
	}

	@Bean
	public CommandLineRunner dataLoader(UserRepository userRepository, ParentRepository parentRepository, StudentRepository studentRepository, PasswordEncoder passwordEncoder) {
		return args -> {
			if (userRepository.findByUsername("admin").isEmpty()) {
				UserModel admin = new UserModel();

				admin.setUsername("admin");
				admin.setPassword(passwordEncoder.encode("admin"));
				admin.setRole(String.valueOf(Role.ADMIN));

				userRepository.save(admin);
				String token =jwtService.generateAccessTokenByUsername(admin.getUsername());
				Student student1 = new Student();
				student1.setFirst_name("Daniel");
				student1.setLast_name("Doe");
				student1.setBalance(new BigDecimal("5000.00"));

				Student student2 = new Student();
				student2.setFirst_name("Peter");
				student2.setLast_name("Doe");
				student2.setBalance(new BigDecimal("5000.00"));

				Student student3 = new Student();
				student3.setFirst_name("Jane");
				student3.setLast_name("Doe");
				student3.setBalance(new BigDecimal("5000.00"));

				Parent a = new Parent();

				a.setFirstName("John");
				a.setLastName("Doe");
				a.setBalance(new BigDecimal("20000.00"));


				Parent b = new Parent();
				b.setFirstName("Mary");
				b.setLastName("Doe");
				b.setBalance(new BigDecimal("10000.00"));


				student1.getParents().add(a);
				student1.getParents().add(b);

				student2.getParents().add(a);

				student3.getParents().add(b);

				Set<Student> parentAStudents = new HashSet<>();
				parentAStudents.add(student1);
				parentAStudents.add(student2);
				a.setStudents(parentAStudents);

				Set<Student> parentBStudents = new HashSet<>();
				parentBStudents.add(student1);
				parentBStudents.add(student3);
				b.setStudents(parentBStudents);

				parentRepository.save(a);
				parentRepository.save(b);
				studentRepository.save(student1);
				studentRepository.save(student2);
				studentRepository.save(student3);

				System.out.println(token);
			}
		};
	}

}
