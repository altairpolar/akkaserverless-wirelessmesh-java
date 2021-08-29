package com.github.sbouclier.result;

import bot.exchange.kraken.access.typed.result.RecentTradeResult;
import com.github.sbouclier.mock.MockInitHelper;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringStartsWith.startsWith;

/**
 * RecentTradeResultTest test
 *
 * @author Stéphane Bouclier
 */
public class RecentTradeResultTest {

    @Test
    public void should_return_to_string() {

        // Given
        RecentTradeResult mockResult = MockInitHelper.buildRecentTradeResult();

        // When
        final String toString = mockResult.toString();

        // Then
        assertThat(toString, startsWith("RecentTradeResult"));
    }
}
