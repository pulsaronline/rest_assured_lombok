package tests;

import io.qameta.allure.restassured.AllureRestAssured;
import models.AuthorisationResponse;
import models.Book;
import models.Books;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;
import java.util.HashMap;
import java.util.Map;

import static filters.CustomLogFilter.customLogFilter;
import static io.restassured.RestAssured.given;
import static io.restassured.http.ContentType.JSON;
import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.CoreMatchers.is;
import static org.hamcrest.CoreMatchers.notNullValue;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BookStoreTests {
    @Test
    void noLogsTest() {
        given()
                .get("https://demoqa.com/BookStore/v1/Books")
                .then()
                .body("books", hasSize(greaterThan(0)));
    }

    @Test
    void withAllLogsTest() {
        given()
                .log().all()
                .get("https://demoqa.com/BookStore/v1/Books")
                .then()
                .log().all()
                .body("books", hasSize(greaterThan(0)));
    }

    @Test
    void withSomeLogsTest() {
        given()
                .log().uri()
                .log().body()
                .get("https://demoqa.com/BookStore/v1/Books")
                .then()
                .log().body()
                .body("books", hasSize(greaterThan(0)));
    }

    @Test
    void withSomePostTest() {
        given()
                .contentType(JSON)
                .body("{ \"userName\": \"alex\", \"password\": \"W1_#zqwerty\" }")
                .when()
                .log().uri()
                .log().body()
                .post("https://demoqa.com/Account/v1/GenerateToken")
                .then()
                .log().body()
                .body("status", is("Success"))
                .body("result", is("User authorized successfully."));
    }

    @Test
    void withAllureListenerTest() {
        Map<String, Object> data = new HashMap<>();
        data.put("userName", "alex");
        data.put("password", "W1_#zqwerty");

        Specs.request
                .filter(new AllureRestAssured())
                .body(data)
                .when()
                .log().uri()
                .log().body()
                .post("https://demoqa.com/Account/v1/GenerateToken")
                .then()
                .log().body()
                .spec(Specs.responseSpec)
                .body("result", is("User authorized successfully."));
    }

    @Test
    void withCustomFilterTest() {
        Map<String, Object> data = new HashMap<>();
        data.put("userName", "alex");
        data.put("password", "W1_#zqwerty");

        given()
                .contentType(JSON)
                .filter(customLogFilter().withCustomTemplates())
                .body(data)
                .when()
                .log().uri()
                .log().body()
                .post("https://demoqa.com/Account/v1/GenerateToken")
                .then()
                .log().body()
                .body("status", is("Success"))
                .body("result", is("User authorized successfully."));
    }

    @Test
    void withAssertJTest() {
        Map<String, Object> data = new HashMap<>();
        data.put("userName", "alex");
        data.put("password", "W1_#zqwerty");
        String response =
                given()
                        .contentType(JSON)
                        .filter(customLogFilter().withCustomTemplates())
                        .body(data)
                        .when()
                        .log().uri()
                        .log().body()
                        .post("https://demoqa.com/Account/v1/GenerateToken")
                        .then()
                        .log().body()
                        .extract().asString();
        assert (response).contains("\"status\":\"Success\"");
        assert (response).contains("\"result\":\"User authorized successfully.\"");
    }

    @Test
    void withModelTest() {
        Map<String, Object> data = new HashMap<>();
        data.put("userName", "alex");
        data.put("password", "W1_#zqwerty");
        AuthorisationResponse response =
                given()
                        .contentType(JSON)
                        .filter(customLogFilter().withCustomTemplates())
                        .body(data)
                        .when()
                        .log().uri()
                        .log().body()
                        .post("https://demoqa.com/Account/v1/GenerateToken")
                        .then()
                        .log().body()
                        .extract().as(AuthorisationResponse.class);
        assert (response.getStatus()).contains("Success");
        assert (response.getResult()).contains("User authorized successfully.");
    }

    @Test
    void booksModelTest() {
        Books books = Specs.request
                        .log().uri()
                        .log().body()
                        .get("https://demoqa.com/BookStore/v1/Books")
                        .then()
                        .spec(Specs.responseSpec)
                        .log().body()
                        .extract().as(Books.class);
        System.out.println(books);
        assertNotNull(books.getBooks());
    }
    @Test

    void FirstBookModelLombokTest() {
        String ISBN = "9781449325862";
        ArrayList<String> bookFields = new ArrayList<>();
        bookFields.add("9781449325862");
        bookFields.add("Git Pocket Guide");
        bookFields.add("A Working Introduction");
        bookFields.add("Richard E. Silverman");
        bookFields.add("2020-06-04T08:48:39.000Z");
        bookFields.add("O'Reilly Media");
        bookFields.add("234");
        bookFields.add("This pocket guide is the perfect on-the-job companion to Git, the distributed version control system. It provides a compact, readable introduction to Git for new users, as well as a reference to common commands and procedures for those of you with Git exp");
        bookFields.add("http://chimera.labs.oreilly.com/books/1230000000561/index.html");

        Book book = Specs.request
                        .log().uri()
                        .log().body()
                        .get("https://demoqa.com/BookStore/v1/Book?ISBN=" + ISBN)
                        .then()
                        .spec(Specs.responseSpec)
                        .log().body()
                        .extract().as(Book.class);
        System.out.println(book);

        assertEquals(bookFields.get(0), book.getIsbn());
        assertEquals(bookFields.get(1), book.getTitle());
        assertEquals(bookFields.get(2), book.getSubTitle());
        assertEquals(bookFields.get(3), book.getAuthor());
        assertEquals(bookFields.get(4), book.getPublishDate());
        assertEquals(bookFields.get(5), book.getPublisher());
        assertEquals(bookFields.get(6), book.getPages().toString());
        assertEquals(bookFields.get(7), book.getDescription());
        assertEquals(bookFields.get(8), book.getWebsite());
    }

    @Test
    void booksJsonSchemaTest() {
        given()
                .log().uri()
                .log().body()
                .get("https://demoqa.com/BookStore/v1/Books")
                .then()
                .log().body()
                .body(matchesJsonSchemaInClasspath("jsonSchemas/booklist_response.json"));
    }
}
