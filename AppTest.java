-- Refactor App for Testability

package com.napier.sem;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;

public class App {

    public Document insertStudent(MongoClient mongoClient) {
        MongoDatabase database = mongoClient.getDatabase("mydb");
        MongoCollection<Document> collection = database.getCollection("test");

        Document doc = new Document("name", "Matthew Walker")
                .append("class", "DevOps")
                .append("year", "2025")
                .append("result", new Document("CW", 95).append("EX", 85));

        collection.insertOne(doc);

        return collection.find().first();
    }

    public static void main(String[] args) {
        MongoClient client = new com.mongodb.MongoClient("mongo-dbserver");
        App app = new App();
        Document result = app.insertStudent(client);
        System.out.println(result.toJson());
    }
}

--Add Dependencies (JUnit + Mockito)


<dependencies>
    <dependency>
        <groupId>org.junit.jupiter</groupId>
        <artifactId>junit-jupiter</artifactId>
        <version>5.10.0</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-core</artifactId>
        <version>5.12.0</version>
        <scope>test</scope>
    </dependency>

    <dependency>
        <groupId>org.mockito</groupId>
        <artifactId>mockito-junit-jupiter</artifactId>
        <version>5.12.0</version>
        <scope>test</scope>
    </dependency>
</dependencies>

--JUnit 5 + Mockito Unit Test

package com.napier.sem;

import com.mongodb.client.MongoClient;
import com.mongodb.client.MongoCollection;
import com.mongodb.client.MongoDatabase;
import org.bson.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.Mockito;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.Mockito.*;

@ExtendWith(MockitoExtension.class)
public class AppTest {

    @Mock
    MongoClient mockClient;

    @Mock
    MongoDatabase mockDb;

    @Mock
    MongoCollection<Document> mockCollection;

    @InjectMocks
    App app;

    @Test
    void testInsertStudent() {
        // Arrange
        when(mockClient.getDatabase("mydb")).thenReturn(mockDb);
        when(mockDb.getCollection("test")).thenReturn(mockCollection);

        Document expected = new Document("name", "Matthew Walker");
        when(mockCollection.find()).thenReturn(
                Mockito.mock(com.mongodb.client.FindIterable.class)
        );
        when(mockCollection.find().first()).thenReturn(expected);

        // Act
        Document result = app.insertStudent(mockClient);

        // Assert
        verify(mockCollection).insertOne(any(Document.class)); // ensure insert was called
        assertEquals("Matthew Walker", result.getString("name"));
    }
}



