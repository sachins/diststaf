package name.sachin.diststaf;

//import java.io.BufferedReader;
//import java.io.FileNotFoundException;
//import java.io.FileReader;
import java.math.BigInteger;

public class PrimeNumberCalc {

	/**
	 * This method is for testing JAR file based algorithm with diststaf service
	 * 
	 * @param args
	 */
	public static void main(String[] args) throws Exception {
		// BufferedReader reader = new BufferedReader(new FileReader(args[0]));
		// String line;
		// if ((line = reader.readLine()) != null) {
		// String[] numbers = line.split("\\s");
		if (args.length < 2) {
			System.out.println("Atleast two numbers are required in argument.");
			System.exit(1);
		}
		long startTime = System.currentTimeMillis();
		BigInteger startNumber = new BigInteger(args[0].trim());
		BigInteger endNumber = new BigInteger(args[1].trim());
		System.out.println("Start number:" + startNumber + ", End number:"
				+ endNumber);
		if (startNumber.compareTo(endNumber) >= 0) {
			System.out.println("Start number:" + startNumber
					+ " should be less than end number:" + endNumber);
		}
		System.out.println("Result:");
		for (BigInteger i = startNumber; i.compareTo(endNumber) <= 0; i = i
				.add(BigInteger.ONE)) {
			if (i.isProbablePrime(1)) {
				if (isPrime(i)) {
					System.out.print(i + " ");
				}
			}
		}
		// } else {
		// System.out.println("File is empty: " + args[0]);
		// }

		long endTime = System.currentTimeMillis();

		System.out.println("\nTotal execution time (ms): " + (endTime - startTime));
	}

	private static boolean isPrime(BigInteger number) {
		for (BigInteger i = new BigInteger("2"); i.multiply(i)
				.compareTo(number) <= 0; i = i.add(BigInteger.ONE)) {
			if (number.remainder(i).compareTo(BigInteger.ZERO) == 0)
				return false;
		}
		return true;
	}

}
