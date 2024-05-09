package net.momirealms.sparrow.common.feature.skull.mineskin;

import com.google.gson.JsonObject;
import com.google.gson.JsonParseException;
import net.momirealms.sparrow.common.feature.skull.mineskin.data.MineskinException;
import net.momirealms.sparrow.common.feature.skull.mineskin.data.Skin;
import net.momirealms.sparrow.common.helper.GsonHelper;
import org.jetbrains.annotations.NotNull;

import java.io.BufferedReader;
import java.io.IOException;
import java.io.InputStream;
import java.io.InputStreamReader;
import java.net.HttpURLConnection;
import java.net.URL;
import java.util.concurrent.CompletableFuture;

import static java.util.Objects.requireNonNull;

public class MineskinClient {

    private static final String API_BASE = "https://api.mineskin.org";
    private static final String GENERATE_BASE = API_BASE + "/generate";

    private final String userAgent;
    private final String apiKey;

    private long nextRequest = 0;

    public MineskinClient(String userAgent) {
        this.userAgent = requireNonNull(userAgent);
        this.apiKey = null;
    }

    public MineskinClient(String userAgent, String apiKey) {
        this.userAgent = requireNonNull(userAgent);
        this.apiKey = apiKey;
    }

    public long getNextRequest() {
        return nextRequest;
    }

    public CompletableFuture<Skin> generateUrl(String url, SkinOptions options) {
        requireNonNull(url);
        requireNonNull(options);
        return CompletableFuture.supplyAsync(() -> {
            try {
                if (System.currentTimeMillis() < nextRequest) {
                    long delay = (nextRequest - System.currentTimeMillis());
                    Thread.sleep(delay + 10);
                }
                JsonObject body = options.toJson();
                body.addProperty("url", url);
                HttpURLConnection connection = generateRequest("/url", body.toString().getBytes());
                return handleResponse(connection.getInputStream());
            } catch (Exception e) {
                e.printStackTrace();
                throw new RuntimeException(e);
            }
        });
    }

    private Skin handleResponse(InputStream responseStream) throws IOException, JsonParseException {
        StringBuilder responseBuilder = new StringBuilder();
        try (BufferedReader reader = new BufferedReader(new InputStreamReader(responseStream))) {
            String line;
            while ((line = reader.readLine()) != null) {
                responseBuilder.append(line);
            }
        }

        String responseBody = responseBuilder.toString();
        JsonObject jsonObject = GsonHelper.get().fromJson(responseBody, JsonObject.class);
        if (jsonObject.has("error")) {
            throw new MineskinException(jsonObject.get("error").getAsString());
        }

        Skin skin = GsonHelper.get().fromJson(jsonObject, Skin.class);
        this.nextRequest = System.currentTimeMillis() + ((long) (skin.delayInfo.millis + (this.apiKey == null ? 10_000 : 100)));
        return skin;
    }

    private HttpURLConnection generateRequest(String endpoint, byte[] data) throws IOException {
        URL url = new URL(GENERATE_BASE + endpoint);
        HttpURLConnection connection = (HttpURLConnection) url.openConnection();
        connection.setRequestMethod("POST");
        connection.setRequestProperty("User-Agent", userAgent);
        connection.setDoOutput(true);
        connection.setConnectTimeout(10000);
        connection.setReadTimeout(10000);
        connection.setInstanceFollowRedirects(false);
        connection.setRequestProperty("Content-Type", "application/json");

        if (apiKey != null) {
            connection.setRequestProperty("Authorization", "Bearer " + apiKey);
        }

        connection.getOutputStream().write(data);

        return connection;
    }
}
