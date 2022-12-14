import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.util.Arrays;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;
import java.util.stream.Stream;

public class GetRanking3 {
    public static final int DEFAULT_PLAY_COUNT = 1;

    public static void main(String[] args) {
        Path rankingPath = Path.of("yumemi-problem/dummy-files/game_score_log.csv");

        try (BufferedReader reader = Files.newBufferedReader(rankingPath)) {
            // CSV ファイルから Stream を作成
            Stream<String> lines = reader.lines();

            // プレイヤーの平均スコアを格納する Map
            Map<String, Integer> averageScoreMap = new HashMap<>();

            // Stream を読み込んで、各行の値を処理
            lines.skip(1)  // 1 行目をスキップ
//                    .peek(v -> System.out.println(v.toString()))
                    .map(line -> line.split(","))  // カンマで分割して配列に変換
//                    .peek(v -> System.out.println(Arrays.toString(v)))
                    .collect(Collectors.groupingBy(data -> data[1]))  // playerId ごとにグループ化
                    .forEach((playerId, playerScores) -> {
                        // 各プレイヤーのスコアとプレイ回数の合計値を求める
                        int sumScore = playerScores.stream().mapToInt(data -> Integer.parseInt(data[2])).sum();
                        int sumPlayCount = playerScores.size();

                        // 平均スコアを求める
                        BigDecimal bigDecimalSum = BigDecimal.valueOf(sumScore);
                        BigDecimal bidDecimalCount = BigDecimal.valueOf(sumPlayCount);
                        int averageScore = bigDecimalSum.divide(bidDecimalCount, 1, RoundingMode.HALF_UP).intValue();
                        // Map に平均スコアを格納
                        averageScoreMap.put(playerId, averageScore);
                    });

            // 平均スコアの大きい順に並べ替えて、上位 10 件を取得
            List<Entry<String, Integer>> top10 = averageScoreMap.entrySet().stream()
                    .sorted(Map.Entry.<String, Integer>comparingByValue().reversed())
                    .limit(10)
                    .collect(Collectors.toList());

            // 結果を出力
            top10.forEach(entry -> {
                System.out.println(entry.getKey() + ": " + entry.getValue());
            });
        } catch (IOException e) {
            System.err.println("ファイルの読み込みに失敗しました: " + e.getMessage());
        }
    }
}