package performancetesting;

import conceptualapi.ComputationAPI;
import conceptualapi.ComputeRequest;
import emptyimplementations.ConceptualAPIImpl;
import emptyimplementations.OptimizedConceptualAPIImpl;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

public class ConceptualApiBenchmarkTest {

    private static final int ITERATIONS = 1_000_000;
    private static final double REQUIRED_IMPROVEMENT = 1.10; // 10% improvement needed

    @Test
    void optimizedUnitTest() {
        
        ComputationAPI original = new ConceptualAPIImpl();
        ComputationAPI optimized = new OptimizedConceptualAPIImpl();

        // Run benchmark on original (Slow)
        long originalTime = runBenchmark(original);
        
        // Run benchmark on optimized (Fast)
        long optimizedTime = runBenchmark(optimized);

        double ratio = (double) originalTime / optimizedTime;
        double improvementPercent = (ratio - 1.0) * 100.0;
        System.out.println("Original time:" + originalTime + " ms");
        System.out.println("Optimized time:" + optimizedTime + " ms");
        System.out.println("Improvement:" + String.format("%.2f", improvementPercent) + "%");

        assertTrue(ratio >= REQUIRED_IMPROVEMENT, 
            "Expected at least 10% faster; got " + String.format("%.2f", improvementPercent) + "%");
    }

    private long runBenchmark(ComputationAPI api) {
        
        // This runs the code so the JVM optimizes it before we start the clock
        for (int i = 1; i <= ITERATIONS; i++) {
            int n = (i % 20_000) + 1;
            api.compute(new ComputeRequest(n));
        }
        long start = System.currentTimeMillis();

        // Actual mesurement loop
        for (int i = 1; i <= ITERATIONS; i++) {
            int n = (i % 20_000) + 1;
            api.compute(new ComputeRequest(n));
        }
        long end = System.currentTimeMillis();
        return end - start;
    }
}