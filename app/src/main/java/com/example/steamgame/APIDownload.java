package com.example.steamgame;

import android.os.AsyncTask;
import android.util.Log;

import androidx.fragment.app.Fragment;

import com.google.gson.JsonArray;
import com.google.gson.JsonElement;
import com.google.gson.JsonObject;
import com.google.gson.JsonParser;

import java.io.IOException;
import java.lang.ref.WeakReference;
import java.util.ArrayList;
import java.util.List;

import okhttp3.HttpUrl;
import okhttp3.OkHttpClient;
import okhttp3.Request;
import okhttp3.Response;

public class APIDownload extends AsyncTask<Void, List<GameEntity>, Void> {

    private static final String USER_API_KEY = "4237F1EF052B65DDB502B7B0A020AA9A";
    private static final String USER_STEAM_ID = "76561199222400750";
    private static final String USER_GAME_LIST_API_URL = "https://api.steampowered.com/IPlayerService/GetOwnedGames/v0001/";
    private static final String APP_DETAILS_API_URL = "https://store.steampowered.com/api/appdetails/";

    private static final int SIZE = 10;

    private WeakReference<Fragment> fragmentReference;
    private GameViewModel gameViewModel;

    public APIDownload(Fragment fragment, GameViewModel viewModel) {
        this.fragmentReference = new WeakReference<>(fragment);
        this.gameViewModel = viewModel;
    }

    @Override
    protected Void doInBackground(Void... voids) {
        fetchOwnedGames();
        return null;
    }

    private void fetchOwnedGames() {
        OkHttpClient client = new OkHttpClient();
        try {
            String userGameListUrl = buildSteamApiUrl(USER_GAME_LIST_API_URL);
            Response userGameListResponse = client.newCall(new Request.Builder().url(userGameListUrl).build()).execute();
            String userGameListResponseBody = userGameListResponse.body().string();

            processOwnedGamesJson(userGameListResponseBody);
        } catch (IOException e) {
            e.printStackTrace();
        }
    }

    private String buildSteamApiUrl(String apiUrl) {
        HttpUrl.Builder urlBuilder = HttpUrl.parse(apiUrl).newBuilder();
        urlBuilder.addQueryParameter("key", USER_API_KEY);
        urlBuilder.addQueryParameter("steamid", USER_STEAM_ID);
        urlBuilder.addQueryParameter("format", "json");
        return urlBuilder.build().toString();
    }

    private void processOwnedGamesJson(String responseBody) {
        JsonParser parser = new JsonParser();
        JsonObject json = parser.parse(responseBody).getAsJsonObject();

        if (json.has("response") && json.getAsJsonObject("response").has("games")) {
            JsonArray gamesArray = json.getAsJsonObject("response").getAsJsonArray("games");

            List<GameEntity> gamesBatchEntities = new ArrayList<>();

            for (JsonElement gameElement : gamesArray) {
                JsonObject gameObject = gameElement.getAsJsonObject();
                int appId = gameObject.get("appid").getAsInt();
                String gameName = getAppName(appId);
                Log.d("APIDownload", "Owned Game: " + gameName);

                GameEntity gameEntity = new GameEntity();
                gameEntity.appId = appId;
                gameEntity.gameName = gameName;

                gamesBatchEntities.add(gameEntity);

                if (gamesBatchEntities.size() == SIZE) {
                    publishProgress(gamesBatchEntities);
                    gamesBatchEntities = new ArrayList<>();
                }
            }
            if (!gamesBatchEntities.isEmpty()) {
                publishProgress(gamesBatchEntities);
            }
        } else {
            Log.d("APIDownload", "No owned games found for the user.");
        }
    }

    private String getAppName(int appId) {
        OkHttpClient client = new OkHttpClient();

        HttpUrl.Builder urlBuilder = HttpUrl.parse(APP_DETAILS_API_URL).newBuilder();
        urlBuilder.addQueryParameter("appids", String.valueOf(appId));

        String url = urlBuilder.build().toString();

        try {
            Response response = client.newCall(new Request.Builder().url(url).build()).execute();
            String responseBody = response.body().string();

            if (responseBody != null && !responseBody.trim().isEmpty()) {
                JsonObject json = JsonParser.parseString(responseBody).getAsJsonObject();

                if (json.has(String.valueOf(appId))) {
                    JsonObject appData = json.getAsJsonObject(String.valueOf(appId));

                    if (appData != null && appData.has("data")) {
                        JsonObject data = appData.getAsJsonObject("data");

                        if (data.has("name")) {
                            return data.get("name").getAsString();
                        } else {
                            Log.d("APIDownload", "No name found for appId: " + appId);
                            // Jeśli nazwa nie jest dostępna, zwróć ID gry jako string
                            return String.valueOf(appId);
                        }
                    } else {
                        Log.d("APIDownload", "No data found for appId: " + appId);
                        // Jeśli dane nie są dostępne, zwróć ID gry jako string
                        return String.valueOf(appId);
                    }
                } else {
                    Log.d("APIDownload", "No entry found for appId: " + appId);
                    // Jeśli nie ma wpisu, zwróć ID gry jako string
                    return String.valueOf(appId);
                }
            } else {
                Log.d("APIDownload", "Response body is null or empty.");
                // Jeśli odpowiedź jest pusta, zwróć ID gry jako string
                return String.valueOf(appId);
            }
        } catch (IOException e) {
            e.printStackTrace();
            // W przypadku błędu, zwróć ID gry jako string
            return String.valueOf(appId);
        }
    }
}
