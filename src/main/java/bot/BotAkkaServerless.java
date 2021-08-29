package bot;

import bot.account.BotAccountAPI;
import bot.account.BotAccountDomain;
import bot.account.BotAccountEntity;
import bot.exchange.kraken.account.KrakenAccountAPI;
import bot.exchange.kraken.account.KrakenAccountDomain;
import bot.exchange.kraken.account.KrakenAccountEntity;
import com.akkaserverless.javasdk.AkkaServerless;

public class BotAkkaServerless {

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
}
