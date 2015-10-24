package filterresearch;

/**
 * Modified from:
 * Mathias Eitz
 * http://cybertron.cg.tu-berlin.de/eitz/bilateral_filtering/
 */
public class BilateralFilter {

    double sigmaD;
    double sigmaR;

    double[][] kernelD;
    double[][] kernelR;
    int[][] pixels;

    double gaussSimilarity[];

    double twoSigmaRSquared;

    int kernelRadius;

    byte[][] in;

    public BilateralFilter(double sigmaD, double sigmaR, byte[][] input) {

	// compute the necessary kernel radius from the maximum
        // of both sigma values
        double sigmaMax = Math.max(sigmaD, sigmaR);
        this.kernelRadius = (int) Math.ceil(2 * sigmaMax);

        this.sigmaD = sigmaD;
        this.sigmaR = sigmaR;
        this.in = input;

        this.twoSigmaRSquared = 2 * this.sigmaR * this.sigmaR;

        // this will always be an odd number, i.e. {1,3,5,7,9,...}
        int kernelSize = kernelRadius * 2 + 1;
        int center = (kernelSize - 1) / 2;

        System.out.println("Applying Bilateral Filter with sigmaD = " + sigmaD + ", sigmaR = " + sigmaR + " and kernelRadius " + kernelRadius);

        this.kernelD = new double[kernelSize][kernelSize];

        for (int x = -center; x < -center + kernelSize; x++) {
            for (int y = -center; y < -center + kernelSize; y++) {
                kernelD[x + center][y + center] = this.gauss(sigmaD, x, y);
            }
        }

	// precompute all possible similarity values for
        // performance reasons
        this.gaussSimilarity = new double[256];
        for (int i = 0; i < 256; i++) {
            this.gaussSimilarity[i] = Math.exp(-((i) / this.twoSigmaRSquared));
        }
    }

    public int apply(int i, int j) {

        double sum = 0;
        double totalWeight = 0;
        int intensityCenter = (this.in[i][j] & 0xff);

        int mMax = i + kernelRadius;
        int nMax = j + kernelRadius;
        double weight;

        for (int m = i - kernelRadius; m < mMax; m++) {
            for (int n = j - kernelRadius; n < nMax; n++) {

                if (this.isInsideBoundaries(m, n)) {
                    //System.out.println("m: " + m + " n: " + n);
                    int intensityKernelPos = (this.in[m][n] & 0xff);
                    weight = kernelD[i - m + kernelRadius][j - n + kernelRadius] * this.similarity(intensityKernelPos, intensityCenter);
                    totalWeight += weight;
                    sum += (weight * intensityKernelPos);
                }
            }
        }
        return (int) Math.floor(sum / totalWeight);
    }

    private double similarity(int p, int s) {
	// this equals: Math.exp(-(( Math.abs(p-s)) /  2 * this.sigmaR * this.sigmaR));
        // but is precomputed to improve performance
        return this.gaussSimilarity[Math.abs(p - s)];
    }

    private double gauss(double sigma, int x, int y) {
        return Math.exp(-((x * x + y * y) / (2 * sigma * sigma)));
    }

    private boolean isInsideBoundaries(int m, int n) {
        return 0 <= m - kernelRadius && m + kernelRadius <= in[0].length && 0 <= n - kernelRadius && n + kernelRadius <= in[0].length; 
    }
}
