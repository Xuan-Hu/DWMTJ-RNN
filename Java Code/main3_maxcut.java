import java.io.File;
import java.io.FileNotFoundException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.Scanner;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

public class main3_maxcut {
	final static int max = 1;
	final static int min = 0;
	final static double weight = 0.1;
	final public static double leakNodee = 0.2;
	static int nMin = 60;
	static int nMax = 60;

	static int randNumPatsMin = 1;
	static int randNumPatsMax = 1;

	final static int randNumTests = 1;
	static BigInteger[][] neededPats = { { new BigInteger("1") } };
	static String[] predeterminedStart = {
			"00000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000000" };
	final static boolean print = true;
	static boolean[][] graph;

	enum mode {
		RAND, PREDETERMINED, FULL
	};

	final static mode thisMode = mode.PREDETERMINED;

	// 505
	// 493
	// 497
	public static void main(String[] args) throws FileNotFoundException {
		Scanner scanny = new Scanner(new File("g05_60.9"));
		scanny.nextLine();
		graph = new boolean[60][60];
		while (scanny.hasNextInt()) {
			int r = scanny.nextInt();
			int c = scanny.nextInt();
			scanny.nextInt();

			graph[r - 1][c - 1] = true;
			graph[c - 1][r - 1] = true;
		}

		boolean keepLooping = true;
		int cycleCount = 0;

		for (int n = nMin; n <= nMax; n++) {
			for (int randNumPats = randNumPatsMin; randNumPats <= randNumPatsMax; randNumPats++) {
				
				
				int[][][] pats = new int[0][0][0];
				double[][] net;
				double[][][] weights;
				String[] start = new String[0];

				// START
				BigInteger TWO = BigInteger.valueOf(2L);
				start = new String[neededPats.length];
				pats = new int[neededPats.length][][];

				for (int t = 0; t < neededPats.length; t++) {
					pats[t] = new int[neededPats[t].length][n];
					for (int p = 0; p < neededPats[t].length; p++) {
						for (int i = n - 1; i >= 0; i--) {
							pats[t][p][i] = neededPats[t][p].mod(TWO).intValue() == 1 ? max : min;
							neededPats[t][p] = neededPats[t][p].divide(TWO);
						}
					}
					start[t] = predeterminedStart[t];
				}
				// END

				net = new double[pats.length][0];
				for (int t = 0; t < pats.length; t++) {
					net[t] = new double[start[t].length()];
					for (int i = 0; i < n; i++) {
						net[t][i] = start[t].charAt(i) == '1' ? max : min;
					}
				}

				int count = 0;
				weights = new double[pats.length][0][0];
				ExecutorService executorService = Executors.newFixedThreadPool(1);

				for (int t = 0; t < pats.length; t++) {
					int numPats = pats[t].length;
					// int n = pats[t][0].length;
					weights[t] = new double[n][n];
					for (int r = 0; r < n; r++) {
						for (int c = 0; c < n; c++) {
							weights[t][r][c] = (graph[r][c] ? weight * -1.05 : weight * 1); // -1.05 505
							// weights[t][r][c] = (graph[r][c] ? weight*-1.05 : weight*1); // -1.05 505
							// -1.05 +- 0.15
							// -1.05 +-0.05 509 509 504 525
						}
					}
				
//				System.out.println(Arrays.deepToString(pats[t]));
//				System.out.println(Arrays.deepToString(weights[t]));
//				System.out.println(Arrays.toString(net[t]));
//				System.out.println(start[t]);
					sim3_max s = new sim3_max(pats[t], weights[t], net[t], start[t], count++);
					executorService.execute(s);
					// s.start();
				}
				while (sim3_max.countRef != weights.length) {
					try {
						Thread.sleep(200);
					} catch (InterruptedException e) {
						e.printStackTrace();
					}
				}
				System.out.println(n + " " + randNumPats + " " + sim3_max.failCount);
				sim3_max.failCount = 0;
				sim3_max.countRef = 0;
				executorService.shutdown();
			}
		}
	}
}

class sim3_max extends Thread {
	final static double nano = 0.000000001f;
	final static double timeStep = nano / 1000;
	final static double timeStop = nano * 3000;
	final static double error = 0.05f;
	final static double learningRate = 1f;
	final static int max = 1;
	final static int min = 0;
	final static double Rdw = 2000;
	final static double leakConn = 5;
	final static double leakNode = main3_maxcut.leakNodee; // 92 //92 //90 //94 ==== 1000 /w0.15=91
	final static double k = 4 * 265933;
	final static double Rp = 500; // w/ 0.01 100=38,39 4000=46 /w0.00005 >2.5 hr
	final static double Rap = 4 * Rp;
	final static double length = 100 * 0.000000001;
	public static int countRef = 0;
	public static int failCount = 0;
	public static double maxTime = 0;
	public double chargeUpV = 0.1;

	int n;
	int printCount;
	double[] net;
	int[][] pats;
	double[][] weights;
	double DC;
	int numPats;
	String start = "";

	public sim3_max(int[][] pats, double[][] weights, double[] net, String start, int printCount) {
		this.pats = pats;
		this.weights = weights;
		this.net = net;
		this.start = start;
		this.printCount = printCount;
		numPats = pats.length;
		n = pats[0].length;
		DC = (double) (leakConn / k) * ((n - 1) * (Rap + Rp) / 2.0 + Rdw);
	}

	@Override
	public void run() {
		double[][] conn = new double[n][n];
		double time = 0.0f;
		boolean done = true;
		int timeCounter = 0;
		while (time < 28 * nano || (!done && time < timeStop)) {
			done = true;
			double[][] updateConn = new double[n][n];
			for (int r = 0; r < n; r++) {
				for (int c = 0; c < n; c++) {
					if (r != c) {
						double Req = Req(net[r]);
						double Rt = Req + Rdw / (n - 1.0);
						updateConn[r][c] += (k * (DC / Rt / (n - 1.0)) - leakConn) / length;
					}
				}
			}

			double[] update = new double[n];
			double chargeCutoff = 15; // How long before disconnecting Vc and connecting W

			// Inexact bounds
			if (chargeUpV >= 0.02 && chargeUpV < 0.03) {
				chargeCutoff = 20;
			} else if (chargeUpV >= 0.03 && chargeUpV < 0.05) {
				chargeCutoff = 18;
			} else if (chargeUpV >= 0.05 && chargeUpV < 0.07) {
				chargeCutoff = 17;
			} else if (chargeUpV >= 0.07 && chargeUpV < 0.15) {
				chargeCutoff = 16;
			} else if (chargeUpV >= 0.15) {
				chargeCutoff = 15;
			}else {
				System.out.println("TOO SMALL");
				System.exit(-1);
			}

			if (time <= nano * chargeCutoff) {
				for (int c = 0; c < n; c++) {
					// update[c] += (start.charAt(c) == '1' ? 0.25 : -0.25) / (timeStep *
					// learningRate); // 0.25
					update[c] += (start.charAt(c) == '1' ? k * chargeUpV / Rdw : k * -chargeUpV / Rdw)
							/ (timeStep * learningRate); // 0.25
				}
			} else {
				for (int c = 0; c < n; c++) {
					for (int r = 0; r < n; r++) {
						if (r != c) {
							double invR = 0;
							for (int rr = 0; rr < n; rr++) {
								if (rr != c && rr != r) {
									invR += 1 / Req(conn[rr][c]);
								}
							}
							double Rtot = 1 / (invR + (1 / Rdw));
							update[c] += (k * ((weights[r][c] / (Req(conn[r][c]) + Rtot)) * (Rtot / Rdw)));
						}
					}
				}
			}

			for (int i = 0; i < n; i++) {
				double old = net[i];
				net[i] = net[i] + timeStep * learningRate * ((update[i] + leakNode) / length);
				net[i] = Math.max(Math.min(1, net[i]), 0);
				if (old != net[i]) {
					done = false;
				}
			}

			if (timeCounter++ % 1000 == 1) {
				// System.out.println(Arrays.toString(net)+(time/nano));
			}

			for (int r = 0; r < n; r++) {
				for (int c = 0; c < n; c++) {
					double old = conn[r][c];
					conn[r][c] = Math.max(0, Math.min(1, conn[r][c] + timeStep * learningRate * updateConn[r][c]));
					if (old != conn[r][c]) {
						done = false;
					}
				}
			}

			time += timeStep;
		}
		if (time > maxTime) {
			System.out.println(time / nano);
			maxTime = time;
		}

		int[] res = new int[n];
		for (int i = 0; i < n; i++) {
			if (net[i] > max - error) {
				res[i] = max;
			} else if (net[i] < min + error) {
				res[i] = min;
			} else {
				res[i] = Integer.MIN_VALUE;
			}
		}

		boolean pass = false;
		for (int p = 0; p < numPats; p++) {
			boolean passPat = true;
			boolean passTap = true;

			for (int i = 0; i < n; i++) {
				if (pats[p][i] != res[i]) {
					passPat = false;
				}
				if (!(pats[p][i] == min && res[i] == max || pats[p][i] == max && res[i] == min)) {
					passTap = false;
				}
			}
			if (passPat || passTap) {
				pass = true;
			}
		}
		while (countRef != printCount) {
			try {
				Thread.sleep(100);
			} catch (Exception e) {

			}

		}
		synchronized (this) {
			if (!pass) {
				failCount++;
			}
			if (main3_maxcut.print) {
				double score = 0;
				for (int r = 0; r < n; r++) {
					for (int c = r + 1; c < n; c++) {
						if (r != c) {
							if (res[r] != res[c] && main3_maxcut.graph[r][c]) {
								score++;
							}
						}
					}
				}
				System.out.println("score " + score);
			}
			countRef++;
		}
	}

	void printNet(double net[]) {
		int[] res = new int[n];
		for (int i = 0; i < n; i++) {
			if (net[i] > max - error) {
				res[i] = max;
			} else if (net[i] < min + error) {
				res[i] = min;
			} else {
				res[i] = Integer.MIN_VALUE;
			}
		}
		if (main3_maxcut.print) {
			for (int i = 0; i < n; i++) {
				if (res[i] == max) {
					System.out.print(1);
				} else {
					System.out.print((res[i] == min ? '0' : 'X'));
				}
			}
		}
	}

	double Req(double f) {
		if (f < 0.45) {
			return Rap;
		} else if (f > .55) {
			return Rp;
		} else {
			return ((.55f - .45f) * Rp * Rap) / ((Rap * (f - .44f)) + (Rp * (.55f - f)));
		}
	}
}
