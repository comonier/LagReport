package com.comonier.lagreport;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordWebhook {
    public static void enviar(String urlString, String conteudo) {
        if (urlString == null || urlString.isEmpty() || urlString.contains("URL_")) return;

        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setRequestProperty("User-Agent", "LagReport-Plugin");
                con.setDoOutput(true);

                // Escapamento rigoroso para JSON
                String jsonLimpo = conteudo.replace("\\", "\\\\")
                                          .replace("\"", "\\\"")
                                          .replace("\b", "")
                                          .replace("\f", "")
                                          .replace("\n", "\\n")
                                          .replace("\r", "\\r")
                                          .replace("\t", "\\t");
                
                String json = "{\"content\": \"" + jsonLimpo + "\"}";
                
                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = json.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                int code = con.getResponseCode();
                if (code >= 400) {
                    org.bukkit.Bukkit.getLogger().warning("[LagReport] Webhook Error: " + code);
                }
                con.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
