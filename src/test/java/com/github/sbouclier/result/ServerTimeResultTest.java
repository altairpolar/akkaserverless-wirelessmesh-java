package com.github.sbouclier.result;

import bot.exchange.kraken.access.typed.result.ServerTimeResult;
import bot.exchange.kraken.access.typed.utils.StreamUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringStartsWith.startsWith;

/**
 * ServerTimeResult test
 *
 * @author Stéphane Bouclier
 */
public class ServerTimeResultTest {

    @Test
    public void should_return_to_string() throws IOException {

        // Given
        final String jsonResult = StreamUtils.getResourceAsString(this.getClass(), "json/server_time.mock.json");
        ServerTimeResult mockResult = new ObjectMapper().readValue(jsonResult, ServerTimeResult.class);

        // When
        final String toString = mockResult.toString();

        // Then
        assertThat(toString, startsWith("ServerTimeResult"));
    }
}
