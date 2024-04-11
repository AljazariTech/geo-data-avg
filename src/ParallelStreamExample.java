import java.util.stream.IntStream;

public class ParallelStreamExample {

    public static void main(String[] args) {
        long startTime = System.currentTimeMillis();
        int sumOfSquaresEvenSequential = IntStream.rangeClosed(1, 1_000_000)
                                                   .filter(n -> n % 2 == 0)
                                                   .map(n -> n * n)
                                                   .sum();
        long endTime = System.currentTimeMillis();
        System.out.println("Sequential sum of squares of even numbers: " + sumOfSquaresEvenSequential);
        System.out.println("Sequential time: " + (endTime - startTime) + "ms");

        startTime = System.currentTimeMillis();
        int sumOfSquaresEvenParallel = IntStream.rangeClosed(1, 1_000_000)
                                                .parallel() // This converts the stream to parallel
                                                .filter(n -> n % 2 == 0)
                                                .map(n -> n * n)
                                                .sum();
        endTime = System.currentTimeMillis();
        System.out.println("Parallel sum of squares of even numbers: " + sumOfSquaresEvenParallel);
        System.out.println("Parallel time: " + (endTime - startTime) + "ms");
    }
}
