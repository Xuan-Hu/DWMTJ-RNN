package quar;
import java.awt.Color;
import java.awt.image.BufferedImage;
import java.io.File;
import java.io.IOException;
import java.math.BigInteger;
import java.util.Arrays;
import java.util.concurrent.ExecutorService;
import java.util.concurrent.Executors;

import javax.imageio.ImageIO;

public class main3pic {
	final static int max = 1;
	final static int min = 0;
	final static double weight = 0.1; // out of 1300 0.01=60 0.02=55 0.03=56 0.1=53 0.125=66 0.135=63 0.14=59 0.145=65
										// 0.475=56 0.159=53 0.15=5X 0.175=66 0.185=57 0.195=57 0.2=54 0.3=65 1=65 10=69
										// 20=62 100=63 1000=74 2000=108 5000=158 1000=147
	final public static double leakNodee = 0.2; // 0 36 0.01 37 0.1 23 0.2 4 0.3 40 -0.1 67
	static int nMin = 100;
	static int nMax = 100;

	static int randNumPatsMin = 3;
	static int randNumPatsMax = 3;

	final static int randNumTests = 100;
	static BigInteger[][] neededPats = { { new BigInteger("421"), new BigInteger("145") } };
	static String[] predeterminedStart = { "000100010" };
	final static boolean print = true;

	static Color redColor = new Color(255, 0, 0);
	public static int red = redColor.getRGB();
	static Color whiteColor = new Color(255, 255, 255);
	public static int white = whiteColor.getRGB();
	static Color blackColor = new Color(0, 0, 0);
	public static int black = blackColor.getRGB();
	public static int ref = 0;
	public static int noiseNum = 0;
	public static String[] outS = { "U", "T", "D" };

	enum mode {
		RAND, PREDETERMINED, FULL
	};

	final static mode thisMode = mode.RAND;

	public static void main(String[] args) throws IOException {
		File AFile = new File("pics/U.png");
		File BFile = new File("pics/T.png");
		File CFile = new File("pics/D.png");

		BufferedImage U = ImageIO.read(AFile);
		BufferedImage T = ImageIO.read(BFile);
		BufferedImage D = ImageIO.read(CFile);
		BufferedImage[] imgs = { U, T, D };

		for (int n = nMin; n <= nMax; n++) {
			for (noiseNum = 0; noiseNum <= 50; noiseNum += 5) {
				ref=0;
				for (int randNumPats = randNumPatsMin; randNumPats <= randNumPatsMax; randNumPats++) {
					int[][][] pats = new int[0][0][0];
					double[][] net;
					double[][][] weights;
					String[] start = new String[0];
					start = new String[randNumTests];
					pats = new int[randNumTests][randNumPats][n];
					for (int t = 0; t < randNumTests; t++) {
						start[t] = "";
						boolean[] flip = new boolean[n];
						int localNoiseNum = (Math.random()<0.5?noiseNum:100-noiseNum);
						for (int i = 0; i < localNoiseNum; i++) {
							int r = (int) (Math.random() * 100);
							if (flip[r]) {
								i--;
							} else {
								flip[r] = true;
							}
						}
						for (int d = 0; d < n; d++) {
							for (int p = 0; p < randNumPats; p++) {
								pats[t][p][d] = (imgs[p].getRGB(d / 10, d % 10) == white ? max : min);
							}
						}
						BufferedImage outImg = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
						for (int d = 0; d < n; d++) {
							if (flip[d]) {
								start[t] += (imgs[t % 3].getRGB(d / 10, d % 10) == white ? min : max);
								outImg.setRGB(d / 10, d % 10,
										(imgs[t % 3].getRGB(d / 10, d % 10) == white ? black : white));
							} else {
								outImg.setRGB(d / 10, d % 10,
										(imgs[t % 3].getRGB(d / 10, d % 10) == white ? white : black));
								start[t] += (imgs[t % 3].getRGB(d / 10, d % 10) == white ? max : min);

							}

						}
						ImageIO.write(outImg, "png", new File(outS[t % 3] + "_" + noiseNum + "-" + ref++/3 + ".png"));
					}

					net = new double[pats.length][0];
					for (int t = 0; t < pats.length; t++) {
						net[t] = new double[start[t].length()];
						for (int i = 0; i < n; i++) {
							net[t][i] = start[t].charAt(i) == '1' ? max : min;
						}
					}

					int count = 0;
					weights = new double[pats.length][0][0];
					ExecutorService executorService = Executors.newFixedThreadPool(20);

					for (int t = 0; t < pats.length; t++) {
						int numPats = pats[t].length;
						// int n = pats[t][0].length;
						weights[t] = new double[n][n];
						for (int r = 0; r < n; r++) {
							for (int c = 0; c < n; c++) {
								double w = 0;
								for (int p = 0; p < numPats; p++) {
									if (r != c) {
										w += (pats[t][p][r] == pats[t][p][c] ? weight : -weight);
									}
								}
								// weights[t][r][c] = w / numPats; removed for tes
								weights[t][r][c] = w / numPats * numPats;
							}
						}
						simPic2 s = new simPic2(pats[t], weights[t], net[t], start[t], count++, t % 3);
						executorService.execute(s);
						// s.start();
					}
					while (simPic2.countRef != weights.length) {
						try {
							Thread.sleep(200);
						} catch (InterruptedException e) {
							e.printStackTrace();
						}
					}
					System.out.println(n + " " + randNumPats + " " + simPic2.failCount);
					simPic2.failCount = 0;
					simPic2.countRef = 0;
					executorService.shutdown();
				}
			}
		}
	}
}

class simPic2 extends Thread {
	final static double nano = 0.000000001f;
	final static double timeStep = nano / 1000;
	final static double timeStop = nano * 3000;
	final static double error = 0.05f;
	final static double learningRate = 1f;
	final static int max = 1;
	final static int min = 0;
	final static double Rdw = 2000;
	final static double leakConn = 5;
	final static double leakNode = main3pic.leakNodee; // 92 //92 //90 //94 ==== 1000 /w0.15=91
	final static double k = 4 * 265933;
	final static double Rp = 500; // w/ 0.01 100=38,39 4000=46 /w0.00005 >2.5 hr
	final static double Rap = 4 * Rp;
	final static double length = 100 * 0.000000001;
	public static int countRef = 0;
	public static int failCount = 0;
	public static double chargeUpV = 0.25f;

	int letter;
	int n;
	int printCount;
	double[] net;
	int[][] pats;
	double[][] weights;
	double DC;
	int numPats;
	String start = "";

	public simPic2(int[][] pats, double[][] weights, double[] net, String start, int printCount, int letter) {
		this.pats = pats;
		this.weights = weights;
		this.net = net;
		this.start = start;
		this.printCount = printCount;
		numPats = pats.length;
		n = pats[0].length;
		DC = (double) (leakConn / k) * ((n - 1) * (Rap + Rp) / 2.0 + Rdw);
		this.letter = letter;
	}

	@Override
	public void run() {
		double[][] conn = new double[n][n];
		double time = 0.0f;
		boolean done = true;
		while (time < 56 * nano || (!done && time < timeStop)) {
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
			if (time <= nano * 15) {
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
		System.out.println(time/nano);

		// last modified 7/7
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
			if (main3pic.print) {
				for (int p = 0; p < numPats; p++) {
					for (int i = 0; i < n; i++) {
						System.out.print((pats[p][i] == max ? 1 : 0));
					}
					System.out.println();
				}
				System.out.print(start + "->\n");
				printNet(net);
				System.out.println(pass ? " pass\n\n" : " FAIL\n\n");
				saveImg(net, letter);
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
		if (main3pic.print) {
			for (int i = 0; i < n; i++) {
				if (res[i] == max) {
					System.out.print(1);
				} else {
					System.out.print((res[i] == min ? '0' : 'X'));
				}
			}
		}
	}

	void saveImg(double net[], int letter) {
		System.out.println(letter);
		BufferedImage image = new BufferedImage(10, 10, BufferedImage.TYPE_INT_ARGB);
		for (int r = 0; r < 10; r++) {
			for (int c = 0; c < 10; c++) {
				if (net[10 * r + c] > max - error) {
					image.setRGB(r, c, main3pic.white);
				} else if (net[10 * r + c] < min + error) {
					image.setRGB(r, c, main3pic.black);
				} else {
					image.setRGB(r, c, main3pic.red);
				}
			}
		}
		try {
			ImageIO.write(image, "png",
					new File(main3pic.outS[letter] + "_Out_" + main3pic.noiseNum + "-" + countRef + ".png"));
		} catch (IOException e) {
			// TODO Auto-generated catch block
			e.printStackTrace();
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
