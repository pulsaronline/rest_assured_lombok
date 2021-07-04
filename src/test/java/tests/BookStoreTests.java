package tests;

import models.Book;
import models.Books;
import org.junit.jupiter.api.Test;

import java.util.ArrayList;

import static io.restassured.module.jsv.JsonSchemaValidator.matchesJsonSchemaInClasspath;
import static org.hamcrest.Matchers.*;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.junit.jupiter.api.Assertions.assertNotNull;

public class BookStoreTests {

    @Test
    void withGroovyTest() {
                Specs.request
                .get("/users?page=2")
                .then()
                .spec(Specs.responseSpec)
                .log().body()
                .body("data.findAll{it.last_name='Funke'}.email.flatten()",
                        hasItem("tobias.funke@reqres.in"))
                        .extract().asPrettyString();
    }

    @Test
    void booksModelLombokTest() {
        Books books = Specs.request
                        .log().uri()
                        .log().body()
                        .get("/BookStore/v1/Books")
                        .then()
                        .spec(Specs.responseSpec)
                        .log().body()
                        .extract().as(Books.class);
        System.out.println(books);
        assertNotNull(books.getBooks());
    }

    @Test
    void firstBookModelLombokTest() {
        String ISBN = "9781449325862";
        ArrayList<String> bookFields = new ArrayList<>();
        bookFields.add("9781449325862");
        bookFields.add("Git Pocket Guide");
        bookFields.add("A Working Introduction");
        bookFields.add("Richard E. Silverman");
        bookFields.add("2020-06-04T08:48:39.000Z");
        bookFields.add("O'Reilly Media");
        bookFields.add("234");
        bookFields.add("This pocket guide is the perfect on-the-job companion to Git, the distributed version control system. " +
                "It provides a compact, readable introduction to Git for new users, as well as a reference to common commands and " +
                "procedures for those of you with Git exp");
        bookFields.add("http://chimera.labs.oreilly.com/books/1230000000561/index.html");

        Book book = Specs.request
                        .log().uri()
                        .log().body()
                        .get("/BookStore/v1/Book?ISBN=" + ISBN)
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
        Specs.request
                .log().body()
                .get("/BookStore/v1/Books")
                .then()
                .spec(Specs.responseSpec)
                .log().body()
                .body(matchesJsonSchemaInClasspath("jsonSchemas/booklist_response.json"));
    }
}
