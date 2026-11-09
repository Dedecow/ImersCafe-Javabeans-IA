
package com.animahub.javabeans.ia.bdd;

import com.animahub.javabeans.ia.JavabeansIa;
import io.cucumber.spring.CucumberContextConfiguration;
import org.springframework.boot.test.context.SpringBootTest;

@CucumberContextConfiguration
@SpringBootTest(classes = JavabeansIa.class)
public class CucumberSpringConfiguration {
}
