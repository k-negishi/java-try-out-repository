import java.io.BufferedWriter;
import java.io.IOException;
import java.nio.charset.StandardCharsets;
import java.nio.file.Files;
import java.nio.file.Path;
import java.nio.file.StandardOpenOption;
import java.time.LocalDateTime;
import java.util.Random;

/*
    おまけ
    ランダムなCSVを作ってみる
 */
public class RandomCSVGeneration {
    public static void main(String[] args) throws IOException {
        LocalDateTime time = LocalDateTime.now();
        Path path = Path.of("yumemi-problem/dummy-files/game_score_log" + String.valueOf(time) + ".csv");

        String header = "create_timestamp,player_id,score";
        Files.createFile(path);

        BufferedWriter bw = Files.newBufferedWriter(path, StandardOpenOption.APPEND);
        bw.write(header);
        bw.newLine();

        int row = 99_999_999;


        for (int i = 0; i < row; i++) {
            String date = "2021/01/01 12:00";

            Random random1 = new Random();
            int playerNumber = random1.nextInt(9999);
            String playerName = "player" + String.format("%04d", playerNumber);

            Random random2 = new Random();
            int score = random2.nextInt(5000);

            String out = date + "," + playerName + "," + score;
            bw.write(out);
            bw.newLine();
            if (i % 10000 == 0) {
                System.out.println("現在" + i + " 番目");
            }
        }
        bw.close();
    }
}
