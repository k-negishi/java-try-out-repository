package RestApiTutorial.src.main.java;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpRequest.BodyPublishers;
import java.net.http.HttpResponse;
import java.util.ResourceBundle;

import static java.net.http.HttpRequest.newBuilder;

/*
    Java で REST API を呼び出す方法 - 簡単なチュートリアル
    https://www.youtube.com/watch?v=9oq7Y8n1t00

    assemblyaiのAPIを使って試す。
    https://www.assemblyai.com/docs/reference#transcript
 */
public class RestApiTutorial {
    public static void main(String[] args) throws Exception {

        Transcript transcript = new Transcript();
        transcript.setAudio_url("https://bit.ly/3yxKEIY");

        Gson gson = new Gson();
        // gsonでオブジェクトをjson文字列化
        String jsonRequest = gson.toJson(transcript);

        System.out.println("jsonRequest : " + jsonRequest);

        // propertiesからAPIキーの読み込み
        ResourceBundle bundle = ResourceBundle.getBundle("sample");
        String apiKey = bundle.getString("assemblyai.key");

        // postRequestに必要な情報を作成
        HttpRequest postRequest = newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript"))
                .header("Authorization", apiKey)
                .POST(BodyPublishers.ofString(jsonRequest))
                .build();

        HttpClient httpClient = HttpClient.newHttpClient();

        // post request実行
        HttpResponse<String> postResponse = httpClient.send(postRequest, HttpResponse.BodyHandlers.ofString());

        System.out.println("postResponse : " + postResponse.body());

        // postのレスポンスのjsonをオブジェクトに変換
        transcript = gson.fromJson(postResponse.body(), Transcript.class);

        System.out.println(transcript.getId());

        // get requestに必要な情報生成
        HttpRequest getRequest = newBuilder()
                .uri(new URI("https://api.assemblyai.com/v2/transcript/" + transcript.getId()))
                .header("Authorization", apiKey)
                // .GET() getはデフォルトのため、省略可能
                .build();

        // assemblyai側のデータ生成がcompleteするまでループ
        while (true) {
            HttpResponse<String> getResponse = httpClient.send(getRequest, HttpResponse.BodyHandlers.ofString());
            transcript = gson.fromJson(getResponse.body(), Transcript.class);

            System.out.println(transcript.getStatus());

            if (transcript.getStatus().equals("completed") || transcript.getStatus().equals("error")) {
                break;
            }
            // 1秒待って再リクエスト
            Thread.sleep(1000);
        }

        System.out.println("Transcription completed!");
        System.out.println(transcript.getText());
    }
}
