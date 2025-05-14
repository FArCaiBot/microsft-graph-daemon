package com.farcai.daemon.configuration;


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
public class GraphConfig {

    @Value("${azure.client-id}")
    private String clientId;

    @Value("${azure.client-secret}")
    private String clientSecret;

    @Value("${azure.tenant-id}")
    private String tenantId;

    @Value("${azure.graph.scopes}")
    private String[] scopes;

    @Bean
    public ClientSecretCredential clientSecretCredential() {
        return new ClientSecretCredentialBuilder()
                .clientId(clientId)
                .clientSecret(clientSecret)
                .tenantId(tenantId)
                .build();
    }

    @Bean
    public TokenCredentialAuthProvider tokenCredentialAuthProvider(ClientSecretCredential credential) {
        return new TokenCredentialAuthProvider(List.of(scopes), credential);
    }

    @Bean
    public GraphServiceClient<Request> graphServiceClient(TokenCredentialAuthProvider authProvider) {

        return GraphServiceClient.builder().authenticationProvider(authProvider).buildClient();
    }
}
