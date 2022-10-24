import java.io.BufferedReader;
import java.io.IOException;
import java.math.BigDecimal;
import java.math.RoundingMode;
import java.nio.file.Files;
import java.nio.file.Path;
import java.time.LocalDateTime;
import java.util.ArrayList;
import java.util.HashMap;
import java.util.LinkedHashMap;
import java.util.List;
import java.util.Map;
import java.util.Map.Entry;
import java.util.stream.Collectors;

/**
 * https://www.yumemi.co.jp/serverside_recruit
 * 【新卒・中途採用】サーバーサイドエンジニア応募者向けの模試を解いてみる
 * <p>
 * game_score_log.csvから上位10位を出力するためのクラス
 * <p>
 * 実装速度優先のため、例外処理は未実装
 */
public class GetRanking {
    public static final int DEFAULT_PLAY_COUNT = 1;

    public static void main(String[] args) throws IOException {
        Path rankingPath = Path.of("yumemi-problem/game_score_log.csv");

        List<PlayerScore> playerScoreList = new ArrayList<>();
        // key:player id, value:listのindexを管理すためのMap
        Map<String, Integer> playerIdMap = new HashMap<>();

        BufferedReader reader = Files.newBufferedReader(rankingPath);

        // csvファイルを1行ずつ読み込む
        // 1行ずつ読み込むことで、大量データにも対応した
        int rowIndex = 0;
        String line;
        while ((line = reader.readLine()) != null) {
            // csvの1行目はスキップ
            if (rowIndex == 0) {
                rowIndex++;
                continue;
            }

            // 行の読み込み
            String[] data = line.split(",");
            String playerId = data[1];
            int score = Integer.parseInt(data[2]);

            // すでに集計されたplayerIdの存在有無で処理を分岐
            // HashMapなのでO(1)で判定できるはず
            if (!playerIdMap.containsKey(playerId)) {
                int mapIndex = playerIdMap.size();
                playerIdMap.put(playerId, mapIndex);
                playerScoreList.add(new PlayerScore(playerId, score, DEFAULT_PLAY_COUNT));

            } else {
                int index = playerIdMap.get(playerId);
                PlayerScore playerScore = playerScoreList.get(index);
                int sumScore = playerScore.getScore() + score;
                PlayerScore newRecord = new PlayerScore(playerScore.getPlayerId(), sumScore, playerScore.getPlayCount() + 1);

                playerScoreList.set(index, newRecord);
            }
        }

        // 平均を計算する
        Map<String, Integer> averageScoreMap = new HashMap<>();
        for (PlayerScore playerScore : playerScoreList) {
            String playerId = playerScore.getPlayerId();
            int averageScore = BigDecimal.valueOf(playerScore.getScore()).divide(BigDecimal.valueOf(playerScore.getPlayCount()), 1, RoundingMode.HALF_UP).intValue();

            averageScoreMap.put(playerId, averageScore);
        }

        // スコアが高い順にソート
        // TODO:もっといい方法があるはず
        Map<String, Integer> sortedMap = averageScoreMap.entrySet().stream()
                .sorted(Entry.<String, Integer>comparingByValue().reversed())
                .collect(Collectors.toMap(Entry::getKey, Entry::getValue, (e1, e2) -> e1, LinkedHashMap::new));


        // forループに入れるためにList化。
        // TODO:streamで上手くやる方法があるはず...
        List<Entry<String, Integer>> sortedScoreList = new ArrayList<>(sortedMap.entrySet());

        // ランキング初期値
        int ranking = 1;

        // SCV形式のヘッダー出力
        System.out.println("rank,player_id,mean_score");
        // 上位10位までを出力するための処理
        for (int i = 0; i < sortedScoreList.size(); i++) {
            Entry<String, Integer> entry = sortedScoreList.get(i);
            String playerId = entry.getKey();
            int averageScore = entry.getValue();

            // 前のレコードとスコアが異なる場合、ランキング順位が1上がる
            if (i != 0 && averageScore != sortedScoreList.get(i - 1).getValue()) {
                ranking++;
            }
            // ランキング10位まででbreak
            if (ranking > 10) break;

            System.out.println(ranking + "," + playerId + "," + averageScore);
        }
    }
}

class PlayerScore {
    private String playerId;
    private int score;
    private int playCount;

    public PlayerScore(String playerId, int score, int playCount) {
        this.playerId = playerId;
        this.score = score;
        this.playCount = playCount;
    }

    public String getPlayerId() {
        return playerId;
    }

    public void setPlayerId(String playerId) {
        this.playerId = playerId;
    }

    public int getScore() {
        return score;
    }

    public void setScore(int score) {
        this.score = score;
    }

    public int getPlayCount() {
        return playCount;
    }

    public void setPlayCount(int playCount) {
        this.playCount = playCount;
    }
}

