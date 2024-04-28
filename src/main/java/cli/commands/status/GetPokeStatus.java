package cli.commands.status;

import cli.utils.HttpRequest;
import cli.utils.Logger;
import org.json.JSONObject;
import org.json.JSONArray;
import java.nio.file.Files;
import java.nio.file.Paths;

public class GetPokeStatus implements Runnable {
  private String name;

  public GetPokeStatus(String name) {
    this.name = name;
  }

  @Override
  public void run() {
    String ename = "";

    if(!isEnglish(name)){
      try {
        // locales.jsonファイルの内容を読み込む
        String content = new String(Files.readAllBytes(Paths.get("src/main/resources/locales.json")));
        JSONArray jsonArray = new JSONArray(content);
        
        // 配列をループして、指定された日本語の名前に対応する英語の名前を探す
        for (int i = 0; i < jsonArray.length(); i++) {
          JSONObject jsonObject = jsonArray.getJSONObject(i);
          
          if(jsonObject.getString("ja").equals(name)) {
            // 名前が一致した場合、英語の名前を取得
            ename = jsonObject.getString("en").toLowerCase();;
            break;
          }
        }
        
        if(ename.isEmpty()) {
          System.out.println("指定された名前に対応するポケモンが見つかりません: " + name);
          return;
        }
        
      } catch(Exception e) {
        System.out.println("エラーが発生しました: " + e.getMessage());
        return;
      }
    }else{
      ename = name;
    }

    System.out.println("英語名:" + ename);

    // ポケモンのデータを取得する
    // 参照: https://pokeapi.co/docs/v2#pokemon
    HttpRequest fetcher = new HttpRequest("https://pokeapi.co/api/v2/pokemon/" + ename);
    String res = fetcher.getResponse();

    // resからstatsを抜き出す
    String[] stats = res.split("\"stats\":\\[")[1].split("\\]")[0].split("\\},\\{");

    // 種族値を表示させる
    Logger.attention(name + "の種族値" + ":");
    System.out.println();
    for (String stat: stats) {
      String statName = stat.split("\"name\":\"")[1].split("\"")[0];
      int baseStat = Integer.parseInt(stat.split("\"base_stat\":")[1].split(",")[0]);
      Logger.log(statName + ": ");
      Logger.success(Integer.toString(baseStat));
      System.out.println();
    }
  }

  // 英語かどうかを判断するメソッド
  private boolean isEnglish(String str) {
    return str.matches("[a-zA-Z]+");
  }
}
