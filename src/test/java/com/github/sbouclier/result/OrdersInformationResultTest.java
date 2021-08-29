package com.github.sbouclier.result;

import bot.exchange.kraken.access.typed.result.OrdersInformationResult;
import bot.exchange.kraken.access.typed.utils.StreamUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringStartsWith.startsWith;

/**
 * ClosedOrdersResult test
 *
 * @author Stéphane Bouclier
 */
public class OrdersInformationResultTest {

    @Test
    public void should_return_to_string() throws IOException {

        // Given
        final String jsonResult = StreamUtils.getResourceAsString(this.getClass(), "json/orders_information.mock.json");
        OrdersInformationResult mockResult = new ObjectMapper().readValue(jsonResult, OrdersInformationResult.class);

        // When
        final String toString = mockResult.toString();
        final String descriptionToString = mockResult.getResult().get("OGRQC4-Q5C5N-2EYZDZ").description.toString();

        // Then
        assertThat(toString, startsWith("OrdersInformationResult"));
        assertThat(descriptionToString, startsWith("OrdersInformationResult.OrderInfo.Description"));
    }
}
