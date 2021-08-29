package bot;

import bot.account.BotAccountDomain;
import bot.account.BotAccountAPI;
import bot.account.BotAccountEntity;
import bot.exchange.kraken.access.typed.HttpApiClientFactory;
import bot.exchange.kraken.account.KrakenAccountDomain;
import bot.exchange.kraken.account.KrakenAccountAPI;
import bot.exchange.kraken.account.KrakenAccountEntity;
import com.akkaserverless.javasdk.AkkaServerless;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;

/**
 * This is the entry point into this user function.
 */
public class BotMain {

    // Client library could be mocked on tests.
    public static HttpApiClientFactory httpApiClientFactory = new HttpApiClientFactory();

    private final static Logger LOG = LoggerFactory.getLogger(BotMain.class);

    public static void main(String... args) throws Exception {
        LOG.info("started");
        BotAkkaServerless.botAkkaServerless.start().toCompletableFuture().get();
    }

}
