import java.util.Arrays;

/**
 * エラトステネスの篩
 * 1から10000までの素数を標準出力するプログラム
 */
public class Eratosthenes {
    public static void main(String[] args) {
        int n = 10000;
        boolean[] primeArray = new boolean[n+1];
        Arrays.fill(primeArray, true);

        primeArray[0] = false;
        primeArray[1] = false;

        for (int i = 2; i < Math.sqrt(n); i++) {
            // すでに合成数判定されているものは処理をskip
            if (!primeArray[i]) {
                continue;
            }

            // iの倍数をふるい落とす
            for (int j = i * 2; j <= n; j += i) {
                primeArray[j] = false;
            }
        }

        for (int i = 0; i < primeArray.length; i++) {
            if (primeArray[i]) {
                System.out.println(i);
            }
        }
    }
}
