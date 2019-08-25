package se.danielmartensson.tools;


import lombok.Getter;
import se.danielmartensson.deeplearning.DL4JModel;

/**
 * The reason why I'm not using Gluon-Ignite is due to memory use.
 * I need simple access to the dL4JModel everywhere.
 * @author Daniel Mårtensson
 *
 */
public class SimpleDependencyInjection {

	private static @Getter DL4JModel dL4JModel;
    
	static {
		dL4JModel = new DL4JModel();
	}
     
}
