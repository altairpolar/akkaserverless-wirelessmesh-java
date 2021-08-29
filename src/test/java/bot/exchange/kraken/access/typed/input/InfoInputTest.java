package bot.exchange.kraken.access.typed.input;

import org.junit.Test;

import static org.hamcrest.MatcherAssert.assertThat;
import static org.hamcrest.core.IsEqual.equalTo;

/**
 * InfoInput test
 *
 * @author Stéphane Bouclier
 */
public class InfoInputTest {

    @Test
    public void should_return_value_of()  {
        assertThat(InfoInput.ALL, equalTo(InfoInput.valueOf("ALL")));
        assertThat(InfoInput.MARGIN, equalTo(InfoInput.valueOf("MARGIN")));
    }
}
