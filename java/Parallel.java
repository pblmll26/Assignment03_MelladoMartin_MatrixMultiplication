import java.util.Random;

public class Parallel {

    static class Worker extends Thread {
        double[][] A;
        double[][] B;
        double[][] C;
        int n;
        int first;
        int last;

        public void run() {
            for (int i = first; i < last; i++) {
                for (int k = 0; k < n; k++) {
                    double a = A[i][k];
                    for (int j = 0; j < n; j++) {
                        C[i][j] += a * B[k][j];
                    }
                }
            }
        }
    }

    static double[][] generate(int n, long seed) {
        Random random = new Random(seed);
        double[][] m = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                m[i][j] = random.nextDouble();
            }
        }
        return m;
    }

    static double[][] multiplySequential(double[][] A, double[][] B, int n) {
        double[][] C = new double[n][n];
        for (int i = 0; i < n; i++) {
            for (int k = 0; k < n; k++) {
                double a = A[i][k];
                for (int j = 0; j < n; j++) {
                    C[i][j] += a * B[k][j];
                }
            }
        }
        return C;
    }

    static double[][] multiplyParallel(double[][] A, double[][] B, int n, int threads) throws InterruptedException {
        double[][] C = new double[n][n];
        Worker[] workers = new Worker[threads];
        for (int t = 0; t < threads; t++) {
            workers[t] = new Worker();
            workers[t].A = A;
            workers[t].B = B;
            workers[t].C = C;
            workers[t].n = n;
            workers[t].first = t * n / threads;
            workers[t].last = (t + 1) * n / threads;
            workers[t].start();
        }
        for (int t = 0; t < threads; t++) {
            workers[t].join();
        }
        return C;
    }

    static double[][] multiply(double[][] A, double[][] B, int n, int threads) throws InterruptedException {
        if (threads == 0) {
            return multiplySequential(A, B, n);
        }
        return multiplyParallel(A, B, n, threads);
    }

    static boolean equal(double[][] X, double[][] Y, int n) {
        for (int i = 0; i < n; i++) {
            for (int j = 0; j < n; j++) {
                if (X[i][j] != Y[i][j]) {
                    return false;
                }
            }
        }
        return true;
    }

    public static void main(String[] args) throws InterruptedException {
        int[] threadCounts = {0, 1, 2, 3, 4, 6, 8, 12};
        int warmups = 3;
        int reps = 5;

        System.out.println("n,threads,repetition,time_s,correct");
        for (int a = 0; a < args.length; a++) {
            int n = Integer.parseInt(args[a]);
            double[][] A = generate(n, 42);
            double[][] B = generate(n, 43);
            double[][] reference = multiplySequential(A, B, n);

            for (int p = 0; p < threadCounts.length; p++) {
                int threads = threadCounts[p];
                for (int w = 0; w < warmups; w++) {
                    multiply(A, B, n, threads);
                }
                for (int r = 0; r < reps; r++) {
                    long start = System.nanoTime();
                    double[][] C = multiply(A, B, n, threads);
                    long end = System.nanoTime();
                    double seconds = (end - start) / 1e9;
                    System.out.println(n + "," + threads + "," + (r + 1) + "," + seconds + "," + equal(C, reference, n));
                }
            }
        }
    }
}
