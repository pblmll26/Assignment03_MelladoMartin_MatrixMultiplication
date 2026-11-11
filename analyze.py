import matplotlib.pyplot as plt
import pandas as pd

times = pd.read_csv("results/times.csv")
print("all results correct:", times["correct"].all())

rows = []
for n in [500, 1000, 2000]:
    sequential = times[(times["n"] == n) & (times["threads"] == 0)]["time_s"].median()
    for threads in [0, 1, 2, 3, 4, 6, 8, 12]:
        values = times[(times["n"] == n) & (times["threads"] == threads)]["time_s"]
        median = values.median()
        iqr = values.quantile(0.75) - values.quantile(0.25)
        speedup = sequential / median
        efficiency = 0.0
        if threads > 0:
            efficiency = speedup / threads
        rows.append([n, threads, median, iqr, speedup, efficiency])

summary = pd.DataFrame(rows, columns=["n", "threads", "median_s", "iqr_s", "speedup", "efficiency"])
summary.to_csv("results/summary.csv", index=False)
print(summary.round(4).to_string(index=False))

threads = [1, 2, 3, 4, 6, 8, 12]
colors = {500: "#2a78d6", 1000: "#eb6834", 2000: "#1baf7a"}
markers = {500: "o", 1000: "s", 2000: "^"}

plt.figure(figsize=(7, 5))
plt.plot(threads, threads, color="#999999", linestyle="--", linewidth=1, label="ideal (S = p)")
for n in [500, 1000, 2000]:
    part = summary[(summary["n"] == n) & (summary["threads"] > 0)]
    plt.plot(threads, part["speedup"], color=colors[n], marker=markers[n], linewidth=2, label="n = " + str(n))
plt.axvline(6, color="#cccccc", linewidth=1)
plt.xticks(threads)
plt.xlabel("Threads p")
plt.ylabel("Speedup S(p) = T_seq / T_p")
plt.title("Speedup vs threads (median of 5 repetitions)")
plt.grid(True, color="#eeeeee")
plt.legend()
plt.tight_layout()
plt.savefig("figures/speedup.png", dpi=150)

plt.figure(figsize=(7, 5))
plt.plot(threads, [1] * len(threads), color="#999999", linestyle="--", linewidth=1, label="ideal (E = 1)")
for n in [500, 1000, 2000]:
    part = summary[(summary["n"] == n) & (summary["threads"] > 0)]
    plt.plot(threads, part["efficiency"], color=colors[n], marker=markers[n], linewidth=2, label="n = " + str(n))
plt.axvline(6, color="#cccccc", linewidth=1)
plt.xticks(threads)
plt.ylim(0, 1.2)
plt.xlabel("Threads p")
plt.ylabel("Efficiency E(p) = S(p) / p")
plt.title("Efficiency vs threads (median of 5 repetitions)")
plt.grid(True, color="#eeeeee")
plt.legend()
plt.tight_layout()
plt.savefig("figures/efficiency.png", dpi=150)
