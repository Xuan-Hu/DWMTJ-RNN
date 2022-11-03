import java.io.File;
import java.io.FileNotFoundException;
import java.util.Scanner;

public class read50File {
	public static void main(String args[]) throws FileNotFoundException {
		Scanner scanny = new Scanner(new File("ChargeUpShift.txt"));
		String estString = "";
		for (int i = 0; i < 12; i++) {
			scanny.nextDouble();
			double time = 0;
			for (int n = 0; n < 100; n++) {
				double d = scanny.nextDouble();
				System.out.println(d);
				time += d;
				
			}

			scanny.nextDouble();
			scanny.nextDouble();
			scanny.nextDouble();
			estString += time/100+" ";
		}
		System.out.println(estString);

	}
}
