package com.example.timeoff;

import com.jayway.jsonpath.JsonPath;
import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;

class ApiClient {

    private final HttpClient http = HttpClient.newHttpClient();
    private final int port;

    ApiClient(int port) {
        this.port = port;
    }

    String tokenOf(String username) throws Exception {
        String json = "{\"username\":\"" + username + "\",\"password\":\"password\"}";
        HttpRequest.BodyPublisher body = HttpRequest.BodyPublishers.ofString(json);
        HttpRequest request = HttpRequest.newBuilder(uri("/api/auth/login"))
            .header("Content-Type", "application/json")
            .POST(body)
            .build();
        HttpResponse<String> response = http.send(request, HttpResponse.BodyHandlers.ofString());
        return JsonPath.read(response.body(), "$.token");
    }

    HttpResponse<String> get(String path, String token) throws Exception {
        HttpRequest.Builder builder = HttpRequest.newBuilder(uri(path))
            .GET();
        if (token != null) {
            builder.header("Authorization", "Bearer " + token);
        }
        return http.send(builder.build(), HttpResponse.BodyHandlers.ofString());
    }

    private URI uri(String path) {
        return URI.create("http://localhost:" + port + path);
    }
}
