package com.comonier.lagreport;

import java.io.OutputStream;
import java.net.HttpURLConnection;
import java.net.URL;
import java.nio.charset.StandardCharsets;

public class DiscordWebhook {
    public static void enviar(String urlString, String conteudo) {
        if (urlString == null || urlString.isEmpty() || urlString.contains("SUA_URL_AQUI")) return;

        new Thread(() -> {
            try {
                URL url = new URL(urlString);
                HttpURLConnection con = (HttpURLConnection) url.openConnection();
                con.setRequestMethod("POST");
                con.setRequestProperty("Content-Type", "application/json");
                
                // Identifica o plugin para o Discord (Obrigatório)
                con.setRequestProperty("User-Agent", "LagReport-Plugin");
                con.setDoOutput(true);

                // Prepara o JSON protegendo aspas e quebras de linha
                String jsonLimpo = conteudo.replace("\"", "'").replace("\n", "\\n");
                String json = "{\"content\": \"" + jsonLimpo + "\"}";
                
                try (OutputStream os = con.getOutputStream()) {
                    byte[] input = json.getBytes(StandardCharsets.UTF_8);
                    os.write(input, 0, input.length);
                }

                // Lê a resposta para confirmar o envio no console se der erro
                int code = con.getResponseCode();
                if (code >= 400) {
                    System.out.println("[LagReport] Webhook Error Code: " + code);
                }
                con.disconnect();
            } catch (Exception e) {
                e.printStackTrace();
            }
        }).start();
    }
}
