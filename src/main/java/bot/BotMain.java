package bot;

import bot.account.AccountManagementDomain;
import bot.account.AccountManagementAPI;
import bot.account.AccountEntity;
import bot.exchange.kraken.account.ExchangeAccountKrakenAPI;
import bot.exchange.kraken.account.ExchangeAccountKrakenDomain;
import bot.exchange.kraken.account.KrakenExchangeAccountEntity;
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
                            AccountEntity.class,
                            AccountManagementAPI.getDescriptor().findServiceByName("AccountService"),
                            AccountManagementDomain.getDescriptor())
                    .registerEventSourcedEntity(
                            KrakenExchangeAccountEntity.class,
                            ExchangeAccountKrakenAPI.getDescriptor().findServiceByName("KrakenAccountService"),
                            ExchangeAccountKrakenDomain.getDescriptor()
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
