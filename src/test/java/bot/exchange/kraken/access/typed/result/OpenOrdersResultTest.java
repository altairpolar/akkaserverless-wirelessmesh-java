package bot.exchange.kraken.access.typed.result;

import bot.exchange.kraken.access.typed.utils.StreamUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringStartsWith.startsWith;

/**
 * OpenOrdersResult test
 *
 * @author Stéphane Bouclier
 */
public class OpenOrdersResultTest {

    @Test
    public void should_return_to_string() throws IOException {

        // Given
        final String jsonResult = StreamUtils.getResourceAsString(this.getClass(), "json/open_orders.mock.json");
        OpenOrdersResult mockResult = new ObjectMapper().readValue(jsonResult, OpenOrdersResult.class);

        // When
        final String toString = mockResult.toString();
        final String openOrdersToString = mockResult.getResult().toString();
        final String descriptionToString = mockResult.getResult().open.get("OC6Z5B-NLAHB-6MQNLA").description.toString();

        // Then
        assertThat(toString, startsWith("OpenOrdersResult"));
        assertThat(openOrdersToString, startsWith("OpenOrders"));
        assertThat(descriptionToString, startsWith("OpenOrdersResult.OpenOrder.Description"));
    }
}
