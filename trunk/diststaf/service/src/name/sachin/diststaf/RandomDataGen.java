package name.sachin.diststaf;

import java.io.IOException;
import java.io.RandomAccessFile;
import java.util.Random;

public class RandomDataGen {

	/**
	 * @param args
	 * @throws IOException
	 */
	public static void main(String[] args) throws IOException {
		if (args.length < 2) {
			System.out
					.println("Usage: RandomDataGen <File name to write random data> <Number of bytes per loop> <Number of loops to run>");
			System.exit(1);
		}
		System.out.println("Generating random data file:" + args[0]);
		long startTime = System.currentTimeMillis();
		int sizeInBytes = Integer.parseInt(args[1]);
		int numOfLoops = Integer.parseInt(args[2]);
		byte[] bytes = new byte[sizeInBytes];
		RandomAccessFile rndFile = new RandomAccessFile(args[0], "rw");
		rndFile.setLength(0); //Truncate file if it already has some data
		Random rndGen = new Random();
		for (int i = 1; i <= numOfLoops; i++) {
			rndGen.nextBytes(bytes);
			rndFile.write(bytes);
		}
		System.out.println("Successfully generated random data file:" + args[0]
				+ " with data length:" + rndFile.length());
		rndFile.close();

		long endTime = System.currentTimeMillis();

		System.out.println("\nTotal execution time (ms): "
				+ (endTime - startTime));
	}

}
