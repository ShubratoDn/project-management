package com.innoweb.project.management.config;

import com.azure.identity.ClientSecretCredential;
import com.azure.identity.ClientSecretCredentialBuilder;
import com.microsoft.graph.authentication.TokenCredentialAuthProvider;
import com.microsoft.graph.requests.GraphServiceClient;
import okhttp3.Request;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;

import java.util.List;

@Configuration
public class GraphClientConfig {

    @Value("${graph.client-id}")
    private String clientId;
    @Value("${graph.client-secret}")
    private String clientSecret;
    @Value("${graph.tenant-id}")
    private String tenantId;

    @Bean
    public GraphServiceClient<?> graphClient() {
        ClientSecretCredential credential = new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .tenantId(tenantId)
                .build();

        TokenCredentialAuthProvider authProvider = new TokenCredentialAuthProvider(
                List.of("https://graph.microsoft.com/.default"), credential);

        GraphServiceClient<Request> requestGraphServiceClient = GraphServiceClient.builder()
                .authenticationProvider(authProvider)
                .buildClient();

        System.out.println(
                requestGraphServiceClient.authenticationMethodConfigurations().getRequestUrl()
        );

        return requestGraphServiceClient;
    }
}
