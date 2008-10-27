package name.sachin.diststaf;

import java.io.BufferedReader;
import java.io.FileNotFoundException;
import java.io.FileReader;

public class Main {

	/**
	 * This method is for testing JAR file based algorithm with diststaf service
	 * 
	 * @param args
	 * @throws FileNotFoundException
	 */
	public static void main(String[] args) throws Exception {
		BufferedReader reader = new BufferedReader(new FileReader(args[0]));
		String line;
		if ((line = reader.readLine()) != null) {
			long loopcount = Long.parseLong(line.trim());
			for (long i = 0; i <= loopcount; i++) {
				if (i % (loopcount / 10) == 0) {
					System.out
							.println("Completed " + i * 100 / loopcount + "%");
				}
			}
		} else {
			System.out.println("File is empty: " + args[0]);
		}
	}

}
