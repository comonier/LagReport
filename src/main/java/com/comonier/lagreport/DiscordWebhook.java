package com.comonier.lagreport;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordWebhook {
    public static void enviar(String urlString, String conteudo) {
        if (urlString == null || urlString.isEmpty() || urlString.equals("SUA_URL_AQUI")) return;

        // Roda em uma thread separada para nao travar o servidor de minecraft
        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                con.setDoOutput(true);

                String json = "{\"content\": \"" + conteudo.replace("\n", "\\n") + "\"}";
                byte[] out = json.getBytes(StandardCharsets.UTF_8);
                
                OutputStream os = con.getOutputStream();
                os.write(out);
                os.close();
                
                con.getResponseCode();
                con.disconnect();
            } catch (Exception e) {
                // Silencioso
            }
        }).start();
    }
}
