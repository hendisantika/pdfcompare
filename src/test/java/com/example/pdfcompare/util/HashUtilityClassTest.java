package com.example.pdfcompare.util;

import org.junit.jupiter.api.Test;

import static org.assertj.core.api.Assertions.assertThat;
import static org.junit.jupiter.api.Assertions.assertEquals;
import static org.mockito.Mockito.times;
import static org.mockito.Mockito.verify;

import java.nio.charset.StandardCharsets;
import java.security.MessageDigest;
import java.security.NoSuchAlgorithmException;
import java.util.Arrays;

import org.junit.jupiter.api.Test;
import org.mockito.MockedStatic;
import org.mockito.Mockito;

class HashUtilityClassTest {

    @Test
    void testHashBytes_normalCase() {
        // Given: a known input
        byte[] data = "Hello".getBytes(StandardCharsets.UTF_8);
        // Expected MD5 hash for "Hello" (computed externally)
        String expectedHash = "8b1a9953c4611296a827abf8c47804d7";

        // When: calling the utility method
        String result = HashUtilityClass.hashBytes(data);

        // Then: the result should match the expected hash
        assertThat(result).isEqualTo(expectedHash);
    }

    @Test
    void testHashBytes_exceptionCase() {
        // Given: a known input
        byte[] data = "Hello".getBytes(StandardCharsets.UTF_8);
        String expectedFallback = Arrays.toString(data);

        // And: mock the static method MessageDigest.getInstance to throw an exception
        try (MockedStatic<MessageDigest> mockedMessageDigest = Mockito.mockStatic(MessageDigest.class)) {
            mockedMessageDigest.when(() -> MessageDigest.getInstance("MD5"))
                    .thenThrow(new NoSuchAlgorithmException("Simulated exception"));

            // When: calling the utility method, which will catch the exception and return fallback
            String result = HashUtilityClass.hashBytes(data);

            // Then: the fallback value is returned
            assertThat(result).isEqualTo(expectedFallback);

            // And: verify that MessageDigest.getInstance("MD5") was called exactly once
            mockedMessageDigest.verify(() -> MessageDigest.getInstance("MD5"), times(1));
        }
    }

    @Test
    void testHashBytes_withLeadingZeros() {
        // Given: any input; we simulate that the MessageDigest returns 16 zero bytes.
        byte[] data = "anything".getBytes(StandardCharsets.UTF_8);
        // Create a fake MessageDigest that returns an array of 16 zero bytes.
        MessageDigest fakeDigest = Mockito.mock(MessageDigest.class);
        Mockito.when(fakeDigest.digest(data)).thenReturn(new byte[16]); // 16 bytes of 0

        try (MockedStatic<MessageDigest> mdMock = Mockito.mockStatic(MessageDigest.class)) {
            mdMock.when(() -> MessageDigest.getInstance("MD5")).thenReturn(fakeDigest);

            // When: compute the hash.
            String result = HashUtilityClass.hashBytes(data);

            // Then: since new BigInteger(1, new byte[16]).toString(16) returns "0",
            // the while-loop should pad it to 32 characters ("00000000000000000000000000000000").
            String expected = "00000000000000000000000000000000";
            assertEquals(expected, result);

            // Verify that MessageDigest.getInstance("MD5") and digest(data) were called.
            mdMock.verify(() -> MessageDigest.getInstance("MD5"), times(1));
            verify(fakeDigest, times(1)).digest(data);
        }
    }


}