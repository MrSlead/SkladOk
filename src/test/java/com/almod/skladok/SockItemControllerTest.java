package com.almod.skladok;

import com.almod.skladok.store.model.SockItem;
import com.almod.skladok.store.repository.SockItemRepository;
import io.restassured.RestAssured;
import io.restassured.http.ContentType;
import java.util.List;

import io.restassured.response.Response;
import org.junit.jupiter.api.AfterAll;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.BeforeAll;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.boot.test.web.server.LocalServerPort;
import org.springframework.test.context.DynamicPropertyRegistry;
import org.springframework.test.context.DynamicPropertySource;
import org.testcontainers.containers.PostgreSQLContainer;

@SpringBootTest(webEnvironment = SpringBootTest.WebEnvironment.RANDOM_PORT)
public class SockItemControllerTest {
    @LocalServerPort
    private Integer port;

    static PostgreSQLContainer<?> postgres = new PostgreSQLContainer<>(
            "postgres:latest"
    );

    @BeforeAll
    static void beforeAll() {
        postgres.start();
    }

    @AfterAll
    static void afterAll() {
        postgres.stop();
    }

    @DynamicPropertySource
    static void configureProperties(DynamicPropertyRegistry registry) {
        registry.add("spring.datasource.url", postgres::getJdbcUrl);
        registry.add("spring.datasource.username", postgres::getUsername);
        registry.add("spring.datasource.password", postgres::getPassword);
    }

    @Autowired
    SockItemRepository sockItemRepository;

    @BeforeEach
    void setUp() {
        RestAssured.baseURI = "http://localhost:" + port;
        sockItemRepository.deleteAll();
    }

    @Test
    void shouldGetSockItems() {
        List<SockItem> sockItems = List.of(
                new SockItem("green", 95, 10),
                new SockItem("green", 90, 5)
        );
        sockItemRepository.saveAll(sockItems);

        Response response = RestAssured.given()
                .param("itemColor", "green")
                .param("compareType", "gt")
                .param("materialPercentage", 85)
                .contentType(ContentType.JSON)
                .when()
                .get("/storage/items")
                .then()
                .statusCode(200)
                .extract().response();

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals("{\"totalUnits\":15}", response.getBody().print());
    }

    @Test
    void shouldGet_200CodeStatus_OnPostRequestIncoming() {
        String json = """
                {
                  "itemColor": "black",
                  "materialPercentage": 90,
                  "units": 5
                }
               """;
        Response response = RestAssured.given()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .post("/storage/items/incoming")
                .then()
                .statusCode(200)
                .extract().response();

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals("Операция выполнена успешна", response.getBody().print());

        List<SockItem> items = sockItemRepository.findByItemColorAndMaterialPercentageEquals("black", 90);
        int units = items.stream()
                .mapToInt(SockItem::getUnits)
                .sum();
        Assertions.assertEquals(5, units);
    }

    @Test
    void shouldGet_200CodeStatus_OnSameTwoPostRequestsIncoming() {
        String json = """
                {
                  "itemColor": "black",
                  "materialPercentage": 90,
                  "units": 5
                }
               """;
        Response response = RestAssured.given()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .post("/storage/items/incoming")
                .then()
                .statusCode(200)
                .extract().response();

        Response response2 = RestAssured.given()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .post("/storage/items/incoming")
                .then()
                .statusCode(200)
                .extract().response();

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals("Операция выполнена успешна", response.getBody().print());

        List<SockItem> items = sockItemRepository.findByItemColorAndMaterialPercentageEquals("black", 90);
        int units = items.stream()
                .mapToInt(SockItem::getUnits)
                .sum();
        Assertions.assertEquals(10, units);
    }

    @Test
    void shouldGet_400CodeStatus_OnPostRequestIncoming_When_MaterialPercentage_IsMissing() {
        List<SockItem> sockItems = List.of(
                new SockItem("green", 95, 10),
                new SockItem("green", 90, 5)
        );
        sockItemRepository.saveAll(sockItems);

        String json = """
                {
                  "itemColor": "green",
                  "units": 121
                }
               """;
        Response response = RestAssured.given()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .post("/storage/items/incoming")
                .then()
                .statusCode(400)
                .extract().response();

        Assertions.assertEquals(400, response.getStatusCode());
    }

    @Test
    void shouldGet_400CodeStatus_OnPostRequestIncoming_When_ItemColor_IsMissing() {
        List<SockItem> sockItems = List.of(
                new SockItem("green", 95, 10),
                new SockItem("green", 90, 5)
        );
        sockItemRepository.saveAll(sockItems);

        String json = """
                {
                  "materialPercentage": 1,
                  "units": 121
                }
               """;
        Response response = RestAssured.given()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .post("/storage/items/incoming")
                .then()
                .statusCode(400)
                .extract().response();

        Assertions.assertEquals(400, response.getStatusCode());
    }

    @Test
    void shouldGet_400CodeStatus_OnPostRequestIncoming_When_Units_IsMissing() {
        List<SockItem> sockItems = List.of(
                new SockItem("green", 95, 10),
                new SockItem("green", 90, 5)
        );
        sockItemRepository.saveAll(sockItems);

        String json = """
                {
                  "itemColor": "green",
                  "materialPercentage": 1
                }
                """;
        Response response = RestAssured.given()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .post("/storage/items/incoming")
                .then()
                .statusCode(400)
                .extract().response();

        Assertions.assertEquals(400, response.getStatusCode());
    }

    @Test
    void shouldGet_200CodeStatus_OnPostRequestOutgoing() {
        List<SockItem> sockItems = List.of(
                new SockItem("green", 95, 10)
        );
        sockItemRepository.saveAll(sockItems);

        String json = """
                {
                  "itemColor": "green",
                  "materialPercentage": 95,
                  "units": 5
                }
               """;
        Response response = RestAssured.given()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .post("/storage/items/outgoing")
                .then()
                .statusCode(200)
                .extract().response();

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals("Операция выполнена успешна", response.getBody().print());

        List<SockItem> items = sockItemRepository.findByItemColorAndMaterialPercentageEquals("green", 95);
        int units = items.stream()
                .mapToInt(SockItem::getUnits)
                .sum();
        Assertions.assertEquals(5, units);
    }

    @Test
    void shouldGet_200CodeStatus_OnSameTwoPostRequestOutgoing() {
        List<SockItem> sockItems = List.of(
                new SockItem("green", 95, 10)
        );
        sockItemRepository.saveAll(sockItems);

        String json = """
                {
                  "itemColor": "green",
                  "materialPercentage": 95,
                  "units": 5
                }
               """;
        Response response = RestAssured.given()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .post("/storage/items/outgoing")
                .then()
                .statusCode(200)
                .extract().response();

        Response response2 = RestAssured.given()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .post("/storage/items/outgoing")
                .then()
                .statusCode(200)
                .extract().response();

        Assertions.assertEquals(200, response.getStatusCode());
        Assertions.assertEquals("Операция выполнена успешна", response.getBody().print());

        List<SockItem> items = sockItemRepository.findByItemColorAndMaterialPercentageEquals("green", 95);
        int units = items.stream()
                .mapToInt(SockItem::getUnits)
                .sum();
        Assertions.assertEquals(0, units);
    }

    @Test
    void shouldGet_400CodeStatus_OnPostRequestOutgoing_When_NotSoManyUnits() {
        List<SockItem> sockItems = List.of(
                new SockItem("green", 95, 10)
        );
        sockItemRepository.saveAll(sockItems);

        String json = """
                {
                  "itemColor": "green",
                  "materialPercentage": 95,
                  "units": 15
                }
               """;
        Response response = RestAssured.given()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .post("/storage/items/outgoing")
                .then()
                .statusCode(400)
                .extract().response();

        Assertions.assertEquals(400, response.getStatusCode());
    }


    @Test
    void shouldGet_400CodeStatus_OnPostRequestOutgoing_When_MaterialPercentage_IsMissing() {
        List<SockItem> sockItems = List.of(
                new SockItem("green", 95, 10),
                new SockItem("green", 90, 5)
        );
        sockItemRepository.saveAll(sockItems);

        String json = """
                {
                  "itemColor": "green",
                  "units": 121
                }
               """;
        Response response = RestAssured.given()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .post("/storage/items/outgoing")
                .then()
                .statusCode(400)
                .extract().response();

        Assertions.assertEquals(400, response.getStatusCode());
    }

    @Test
    void shouldGet_400CodeStatus_OnPostRequestOutgoing_When_ItemColor_IsMissing() {
        List<SockItem> sockItems = List.of(
                new SockItem("green", 95, 10),
                new SockItem("green", 90, 5)
        );
        sockItemRepository.saveAll(sockItems);

        String json = """
                {
                  "materialPercentage": 1,
                  "units": 121
                }
               """;
        Response response = RestAssured.given()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .post("/storage/items/outgoing")
                .then()
                .statusCode(400)
                .extract().response();

        Assertions.assertEquals(400, response.getStatusCode());
    }

    @Test
    void shouldGet_400CodeStatus_OnPostRequestOutgoing_When_Units_IsMissing() {
        List<SockItem> sockItems = List.of(
                new SockItem("green", 95, 10),
                new SockItem("green", 90, 5)
        );
        sockItemRepository.saveAll(sockItems);

        String json = """
                {
                  "itemColor": "green",
                  "materialPercentage": 1
                }
                """;
        Response response = RestAssured.given()
                .body(json)
                .contentType(ContentType.JSON)
                .when()
                .post("/storage/items/outgoing")
                .then()
                .statusCode(400)
                .extract().response();

        Assertions.assertEquals(400, response.getStatusCode());
    }
}
