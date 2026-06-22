package com.example.fashionshop.integration.ghn.client;

import com.example.fashionshop.integration.ghn.dto.GhnDistrict;
import com.example.fashionshop.integration.ghn.dto.GhnDistrictRequest;
import com.example.fashionshop.integration.ghn.dto.GhnProvince;
import com.example.fashionshop.integration.ghn.dto.GhnResponse;
import com.example.fashionshop.integration.ghn.dto.GhnWard;
import com.example.fashionshop.integration.ghn.dto.GhnWardRequest;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.core.ParameterizedTypeReference;
import org.springframework.http.HttpEntity;
import org.springframework.http.HttpHeaders;
import org.springframework.http.HttpMethod;
import org.springframework.http.MediaType;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Component;
import org.springframework.web.client.RestClientException;
import org.springframework.web.client.RestTemplate;

import java.util.List;

@Component
public class GhnClient {

    private final RestTemplate restTemplate;
    private final String baseUrl;
    private final String token;

    public GhnClient(RestTemplate restTemplate,
                     @Value("${ghn.base-url}") String baseUrl,
                     @Value("${ghn.token}") String token) {
        this.restTemplate = restTemplate;
        this.baseUrl = baseUrl;
        this.token = token;
    }

    public List<GhnProvince> getProvinces() {
        checkToken();

        String url = baseUrl + "/master-data/province";

        HttpEntity<Void> requestEntity = new HttpEntity<>(createHeaders());

        try {
            ResponseEntity<GhnResponse<List<GhnProvince>>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.GET,
                            requestEntity,
                            new ParameterizedTypeReference<>() {
                            }
                    );

            return extractData(response);
        } catch (RestClientException exception) {
            throw new RuntimeException(
                    "Không thể lấy danh sách tỉnh/thành từ GHN",
                    exception
            );
        }
    }

    public List<GhnDistrict> getDistricts(Integer provinceId) {
        checkToken();

        if (provinceId == null) {
            throw new IllegalArgumentException(
                    "Province ID không được để trống"
            );
        }

        String url = baseUrl + "/master-data/district";

        GhnDistrictRequest requestBody =
                new GhnDistrictRequest(provinceId);

        HttpEntity<GhnDistrictRequest> requestEntity =
                new HttpEntity<>(requestBody, createHeaders());

        try {
            ResponseEntity<GhnResponse<List<GhnDistrict>>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            requestEntity,
                            new ParameterizedTypeReference<>() {
                            }
                    );

            return extractData(response);
        } catch (RestClientException exception) {
            throw new RuntimeException(
                    "Không thể lấy danh sách quận/huyện từ GHN",
                    exception
            );
        }
    }

    public List<GhnWard> getWards(Integer districtId) {
        checkToken();

        if (districtId == null) {
            throw new IllegalArgumentException(
                    "District ID không được để trống"
            );
        }

        String url = baseUrl + "/master-data/ward";

        GhnWardRequest requestBody =
                new GhnWardRequest(districtId);

        HttpEntity<GhnWardRequest> requestEntity =
                new HttpEntity<>(requestBody, createHeaders());

        try {
            ResponseEntity<GhnResponse<List<GhnWard>>> response =
                    restTemplate.exchange(
                            url,
                            HttpMethod.POST,
                            requestEntity,
                            new ParameterizedTypeReference<>() {
                            }
                    );

            return extractData(response);
        } catch (RestClientException exception) {
            throw new RuntimeException(
                    "Không thể lấy danh sách phường/xã từ GHN",
                    exception
            );
        }
    }

    private HttpHeaders createHeaders() {
        HttpHeaders headers = new HttpHeaders();

        headers.set("Token", token);
        headers.setContentType(MediaType.APPLICATION_JSON);

        return headers;
    }

    private void checkToken() {
        if (token == null || token.isBlank()) {
            throw new IllegalStateException(
                    "Chưa cấu hình GHN_TOKEN"
            );
        }
    }

    private <T> T extractData(
            ResponseEntity<GhnResponse<T>> response
    ) {
        GhnResponse<T> responseBody = response.getBody();

        if (!response.getStatusCode().is2xxSuccessful()
                || responseBody == null) {
            throw new RuntimeException(
                    "GHN không trả về dữ liệu hợp lệ"
            );
        }

        if (responseBody.getCode() == null
                || responseBody.getCode() != 200) {
            throw new RuntimeException(
                    "GHN trả lỗi: " + responseBody.getMessage()
            );
        }

        if (responseBody.getData() == null) {
            throw new RuntimeException(
                    "GHN không trả về phần data"
            );
        }

        return responseBody.getData();
    }
}