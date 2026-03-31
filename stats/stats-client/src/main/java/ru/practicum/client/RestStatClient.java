package ru.practicum.client;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.cloud.client.ServiceInstance;
import org.springframework.cloud.client.discovery.DiscoveryClient;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpRequest;
import org.springframework.http.HttpStatus;
import org.springframework.http.HttpStatusCode;
import org.springframework.http.MediaType;
import org.springframework.http.client.ClientHttpResponse;
import org.springframework.stereotype.Service;
import org.springframework.web.client.RestClient;
import org.springframework.web.util.UriComponentsBuilder;
import ru.practicum.dto.HitDto;
import ru.practicum.dto.StatsDto;

import java.io.IOException;
import java.net.URI;
import java.util.List;

@Service
public class RestStatClient {
    private final DiscoveryClient discoveryClient;
    private final RestClient restClient;

    @Value("${stats.serviceName}")
    private String serviceName;

    public RestStatClient(DiscoveryClient discoveryClient) {
        this.discoveryClient = discoveryClient;

        restClient = RestClient.builder()
                .build();
    }

    private URI createUri(String path) {
        ServiceInstance instance = getInstance();
        return URI.create("http://" + instance.getHost() + ":" + instance.getPort() + path);
    }


    private boolean isNotCreated(HttpStatusCode status) {
        return status != HttpStatus.CREATED;
    }

      private void handleError(HttpRequest request, ClientHttpResponse response) throws IOException {
        throw new RuntimeException("Не удалось сохранить Hit: " + response.getStatusCode());
    }

    public void save(HitDto hitDto) {
        restClient.post()
                .uri(createUri("/hit"))
                .contentType(MediaType.APPLICATION_JSON)
                .body(hitDto)
                .retrieve()
                .onStatus(this::isNotCreated, this::handleError)
                .toBodilessEntity();

    }

    public List<StatsDto> getStats(String start, String end, List<String> uris, boolean unique) {
        UriComponentsBuilder builder = UriComponentsBuilder.fromUri(createUri("/stats"))
                .queryParam("start", start)
                .queryParam("end", end)
                .queryParam("unique", unique);
         if (uris != null && !uris.isEmpty()) {
            builder.queryParam("uris", String.join(",", uris));
          }
        String url = builder.build().toUriString();


        return restClient.get()
                .uri(url)
                .retrieve()
                .body(new ParameterizedTypeReference<List<StatsDto>>() {
                });
    }


    private ServiceInstance getInstance() {
        try {
            return discoveryClient
                    .getInstances(serviceName)
                    .getFirst();
        } catch (Exception exception) {
            throw new RuntimeException(exception);
        }
    }
}
