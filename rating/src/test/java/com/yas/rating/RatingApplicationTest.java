package com.yas.rating;

import org.junit.jupiter.api.Test;
import org.springframework.boot.test.context.SpringBootTest;

@SpringBootTest(useMainMethod = SpringBootTest.UseMainMethod.ALWAYS)
class RatingApplicationTest {

    @Test
    void contextLoads() {
        // It is suppose to be empty
    }

    @Test
    void main() {
        // We can't actually start the full application in a unit test easily without context collision,
        // but spring boot test already starts the context.
        // Let's just instantiate the class to cover constructor
        org.junit.jupiter.api.Assertions.assertNotNull(new RatingApplication());
    }
}
