package se.danielmartensson.tools;

import org.springframework.context.ApplicationContext;
import org.springframework.context.support.ClassPathXmlApplicationContext;

public class StaticDependencyInjection {

	public static ApplicationContext contextDL4J;
    
	static {
		contextDL4J = new ClassPathXmlApplicationContext("DL4JBeans.xml");
	}
     
}
