package bot;

import bot.account.BotAccountDomain;
import bot.account.BotAccountAPI;
import bot.account.BotAccountEntity;
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



    private final static Logger LOG = LoggerFactory.getLogger(BotMain.class);

    public static AkkaServerless botAkkaServerless =
            new AkkaServerless()
                    .registerEventSourcedEntity(
                            BotAccountEntity.class,
                            BotAccountAPI.getDescriptor().findServiceByName("BotAccountService"),
                            BotAccountDomain.getDescriptor())
                    .registerEventSourcedEntity(
                            KrakenAccountEntity.class,
                            KrakenAccountAPI.getDescriptor().findServiceByName("KrakenAccountService"),
                            KrakenAccountDomain.getDescriptor()
                    );

/*                    .registerView(
                            CustomerLocationView.class,
                            Customerlocationview.getDescriptor()
                                    .findServiceByName("CustomerLocationByEmailService"),
                            "customer_locations",
                            Wirelessmeshdomain.getDescriptor(),
                            Customerlocationview.getDescriptor())
                    .registerAction(
                            ToggleNightlightAction.class,
                            Devicecontrol.getDescriptor().findServiceByName("DeviceControlService"),
                            Wirelessmeshdomain.getDescriptor())
                    .registerAction(
                            PublishingAction.class,
                            Publishing.getDescriptor().findServiceByName("PublishingService"),
                            Wirelessmeshdomain.getDescriptor()
                    );*/

    public static void main(String... args) throws Exception {
        LOG.info("started");
        botAkkaServerless.start().toCompletableFuture().get();
    }
}
