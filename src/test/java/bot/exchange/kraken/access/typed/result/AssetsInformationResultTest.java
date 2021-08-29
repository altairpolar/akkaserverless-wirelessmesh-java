package bot.exchange.kraken.access.typed.result;

import bot.exchange.kraken.access.typed.utils.StreamUtils;
import com.fasterxml.jackson.databind.ObjectMapper;
import org.junit.Test;

import java.io.IOException;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.StringStartsWith.startsWith;

/**
 * AssetInformationResult test
 *
 * @author Stéphane Bouclier
 */
public class AssetsInformationResultTest {

    @Test
    public void should_return_to_string() throws IOException {

        // Given
        final String jsonResult = StreamUtils.getResourceAsString(this.getClass(), "json/assets_information.mock.json");
        AssetsInformationResult mockResult = new ObjectMapper().readValue(jsonResult, AssetsInformationResult.class);

        // When
        final String toString = mockResult.toString();

        // Then
        assertThat(toString, startsWith("AssetsInformationResult"));
    }
}
