# Assignment 3 - Parallel Matrix Multiplication

Big Data, Grado en Ciencia e Ingeniería de Datos, ULPGC (2026-2027).

Reused from Assignment 2: the validated sequential i-k-j method (`multiplyIkj` in `java/Dense.java` of
https://github.com/pblmll26/Assignment02_MelladoMartin_MatrixMultiplication). Here it is `multiplySequential`, and
`multiplyParallel` is the same loop with the rows of C split into contiguous blocks, one per Java thread.

## Files

| Path | What it is |
|---|---|
| `java/Parallel.java` | Sequential i-k-j and parallel i-k-j with Java threads; runs all configurations |
| `analyze.py` | Medians, IQR, speedup, efficiency and figures |
| `results/times.csv` | Raw times, one row per repetition |
| `results/summary.csv` | Median, IQR, speedup and efficiency per configuration |
| `figures/` | Speedup and efficiency plots |

## Environment

- CPU: AMD Ryzen 5 5625U, 6 physical cores, 12 logical processors (SMT), 16 MB L3. No affinity settings.
- RAM: 16 GB DDR4-3200. OS: Windows 11 Home 10.0.26200.
- Java: OpenJDK 21.0.12.1 (Temurin), HotSpot Server VM, `-Xmx4g`. Python 3.11.9, pandas 3.0.1, matplotlib 3.10.6.

## How to reproduce

```
javac -d java java/Parallel.java
java -Xmx4g -cp java Parallel 500 1000 2000 > results/times.csv
pip install pandas matplotlib
python analyze.py
```

## Protocol

- Inputs: A and B generated in Java with `java.util.Random` (seeds 42 and 43), 64-bit doubles uniform in [0, 1).
- Sizes: n = 500, 1000, 2000. Threads: 0 (sequential method), 1, 2, 3, 4, 6, 8, 12. No test exceeds the 12 hardware threads.
- Timed region: allocation of C, creating and starting the threads, the multiplication and waiting for all threads (`join`).
- 3 warm-up runs and 5 measured repetitions per configuration, `System.nanoTime()`, median and IQR.
- Speedup S(p) = T_seq / T_p and efficiency E(p) = S(p) / p, using medians.
- Correctness: every repetition is compared element by element with the sequential result (`correct` column). Each element is computed by one thread in the same order, so the results must be identical.
- Memory: estimated array storage, 3 × 8n² bytes, the same for every thread count (threads share A, B and C).
