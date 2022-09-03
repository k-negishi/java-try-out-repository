import java.util.Arrays;

public class Eratosthenes {
    public static void main(String[] args) {
        int n = 10000;
        boolean[] prime = new boolean[n+1];
        Arrays.fill(prime, true);

        prime[0] = false;
        prime[1] = false;

        for (int i = 2; i < Math.sqrt(n); i++) {
            if (!prime[i]) {
                continue;
            }
            for (int j = i * 2; j <= n; j += i) {
                prime[j] = false;
            }
        }

        for (int i = 0; i < prime.length; i++) {
            if (prime[i]) {
                System.out.println(i);
            }
        }

    }
}
