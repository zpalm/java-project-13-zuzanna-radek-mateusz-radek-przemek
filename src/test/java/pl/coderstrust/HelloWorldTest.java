package pl.coderstrust;

import org.junit.jupiter.api.Test;

import static org.junit.jupiter.api.Assertions.assertTrue;

class HelloWorldTest {

    @Test
    public void shouldReturnTrueForPalindromeWord() {
        assertTrue(HelloWorld.isPalindrome("abba"));
    }
}
