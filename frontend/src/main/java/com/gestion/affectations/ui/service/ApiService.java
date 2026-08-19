package com.gestion.affectations.ui.service;

import com.gestion.affectations.ui.util.LocalDateAdapter;
import com.google.gson.Gson;
import com.google.gson.GsonBuilder;

import java.net.URI;
import java.net.http.HttpClient;
import java.net.http.HttpRequest;
import java.net.http.HttpResponse;
import java.time.LocalDate;

public class ApiService {

    private static final String BASE_URL = "http://localhost:8088/api";
    private final HttpClient client;
    private final Gson gson;

    private static ApiService instance;

    private ApiService() {
        this.client = HttpClient.newHttpClient();
        this.gson = new GsonBuilder()
                .registerTypeAdapter(LocalDate.class, new LocalDateAdapter())
                .create();
    }

    public static ApiService getInstance() {
        if (instance == null) {
            instance = new ApiService();
        }
        return instance;
    }

    public String post(String path, Object body) throws Exception {
        String jsonBody = gson.toJson(body);
        
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .POST(HttpRequest.BodyPublishers.ofString(jsonBody));
                
        return execute(requestBuilder);
    }

    public String get(String path) throws Exception {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .GET();
                
        return execute(requestBuilder);
    }

    public String put(String path, Object body) throws Exception {
        String jsonBody = gson.toJson(body);
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .header("Content-Type", "application/json")
                .PUT(HttpRequest.BodyPublishers.ofString(jsonBody));
                
        return execute(requestBuilder);
    }

    public String delete(String path) throws Exception {
        HttpRequest.Builder requestBuilder = HttpRequest.newBuilder()
                .uri(URI.create(BASE_URL + path))
                .DELETE();
                
        return execute(requestBuilder);
    }

    private String execute(HttpRequest.Builder requestBuilder) throws Exception {
        if (AuthContext.getInstance().isAuthenticated()) {
            requestBuilder.header("Authorization", "Bearer " + AuthContext.getInstance().getToken());
        }
        
        HttpRequest request = requestBuilder.build();
        HttpResponse<String> response = client.send(request, HttpResponse.BodyHandlers.ofString());
        
        if (response.statusCode() >= 200 && response.statusCode() < 300) {
            return response.body();
        } else {
            throw new Exception("Erreur API (" + response.statusCode() + ") : " + response.body());
        }
    }
    
    public Gson getGson() {
        return gson;
    }
}
