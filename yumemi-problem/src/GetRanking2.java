import java.io.BufferedReader;
import java.io.FileReader;
import java.util.Comparator;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.stream.Collectors;

public class GetRanking2 {
    public static void main(String[] args) {
        // コマンドライン引数からファイル名を取得する
        String fileName = args[0];

        // ファイルからデータを読み込む
        Map<String, Integer> scoreMap = new HashMap<>();
        try (BufferedReader br = new BufferedReader(new FileReader(fileName))) {
            br.readLine();
            String line; // ヘッダー行を読み飛ばす
            while ((line = br.readLine()) != null) {
                // CSVからプレイヤーIDとスコアを取得する
                String[] data = line.split(",");
                String playerId = data[1];
                int score = Integer.parseInt(data[2]);

                // プレイヤーIDをキーとして、プレイヤーのスコアを保持する
                scoreMap.put(playerId, scoreMap.getOrDefault(playerId, 0) + score);
            }
        } catch (Exception e) {
            e.printStackTrace();
        }

        // プレイヤーIDをキーとしたスコアのマップから、平均点が高い順のランキングを作成する
        List<Map.Entry<String, Integer>> rankingList = scoreMap.entrySet().stream()
                .sorted(Map.Entry.comparingByValue(Comparator.reverseOrder()))
                .collect(Collectors.toList());

        // ランキング上位10人を標準出力する
        System.out.println("rank,player_id,mean_score");
        for (int i = 0; i < 10; i++) {
            Map.Entry<String, Integer> entry = rankingList.get(i);
            String playerId = entry.getKey();
            int meanScore = entry.getValue() / scoreMap.get(playerId);
            System.out.printf("%d,%s,%d%n", i + 1, playerId, meanScore);
        }
    }
}
