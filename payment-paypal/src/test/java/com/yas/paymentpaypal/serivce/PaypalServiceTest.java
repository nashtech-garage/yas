package com.yas.paymentpaypal.serivce;

import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertEquals;

class PaypalServiceTest {
	@BeforeEach
	void setUp() {

	}

	@Test
	void test() {
		// Test logic here

		// Add an assertion
		int expected = 10;
		int actual = yourMethodUnderTest(); // Call the method you want to test
		assertEquals(expected, actual);
	}

	// Define yourMethodUnderTest() that returns an int
	int yourMethodUnderTest() {
		// Your implementation here
		return 10; // Return a value to be tested
	}

}
