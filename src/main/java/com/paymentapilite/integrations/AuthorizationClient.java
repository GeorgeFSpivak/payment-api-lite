package com.paymentapilite.integrations;


import com.paymentapilite.exceptions.AuthorizationServiceUnavailableException;
import org.springframework.beans.factory.annotation.Value;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClient;
import org.springframework.web.client.RestClientException;


@Component
public class AuthorizationClient {
    private final RestClient restClient;
    private final String url;

    public AuthorizationClient (
            RestClient.Builder builder,
            @Value("${integrations.authorization.url}") String url
    ) {

        this.restClient = builder.build();
        this.url = url;
    }

    public boolean authorize() {
        try {
            ResponseEntity<AuthorizationResponse> response = restClient.get()
                    .uri(url)
                    .accept(MediaType.APPLICATION_JSON)
                    .retrieve()
                    .onStatus(
                            status -> status.value() == 403,
                            (request, result)  -> {}
                    ).toEntity(AuthorizationResponse.class);

            AuthorizationResponse body = response.getBody();

            if (body == null || body.data() == null || body.data().authorization() == null) {
                throw new AuthorizationServiceUnavailableException(
                        "Incomplete response from the authorization service."
                );
            }

            if (response.getStatusCode().value() == 200 && "success".equals(body.status())
                    && Boolean.TRUE.equals(body.data().authorization())) {
                return true;
            }
            if (response.getStatusCode().value() == 403 && "fail".equals(body.status())
                    && Boolean.FALSE.equals(body.data().authorization())) {
                return false;
            }
            throw new AuthorizationServiceUnavailableException(
                    "Unexpected response from the authorization service."
            );
        } catch (RestClientException ex) {
            throw new AuthorizationServiceUnavailableException(
                    "Failed to communicate with the authorization service.", ex
            );
        }
    }
}
