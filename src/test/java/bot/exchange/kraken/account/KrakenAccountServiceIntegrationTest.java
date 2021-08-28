package bot.exchange.kraken.account;

import bot.BotMain;
import bot.account.BotAccountAPI;
import bot.account.BotAccountServiceClient;
import bot.exchange.kraken.account.KrakenAccountAPI.*;
import com.akkaserverless.javasdk.testkit.junit.AkkaServerlessTestkitResource;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class KrakenAccountServiceIntegrationTest {

    private final BotAccountServiceClient botAccountServiceClient;
    private final KrakenAccountServiceClient krakenAccountServiceClient;

    @ClassRule
    public static final AkkaServerlessTestkitResource testkit = new AkkaServerlessTestkitResource(
            BotMain.botAkkaServerless);

    public KrakenAccountServiceIntegrationTest() {
        botAccountServiceClient = BotAccountServiceClient.create(testkit.getGrpcClientSettings(), testkit.getActorSystem());
        krakenAccountServiceClient = KrakenAccountServiceClient.create(testkit.getGrpcClientSettings(), testkit.getActorSystem());
    }

    @Test
    public void registerAccount_happyCase() throws ExecutionException, InterruptedException {

        // given
        String accountId = UUID.randomUUID().toString();
        String email = "me@you.com";
        botAccountServiceClient.registerAccount(BotAccountAPI.RegisterAccountCommand.newBuilder()
                .setId(accountId)
                .setEmail(email)
                .build())
                .toCompletableFuture().get();

        // when
        krakenAccountServiceClient.associateAccount(AssociateAccountCommand.newBuilder()
                .setBotAccountId(accountId)
                .build())
                .toCompletableFuture().get();

        // then

        // All Ok. No exception.
        Assert.assertTrue(true);

    }

    @Test
    public void registerAccount_okAlsoForInvalidAccount() throws ExecutionException, InterruptedException {

        // given
        String accountId = UUID.randomUUID().toString();

        // when
        krakenAccountServiceClient.associateAccount(AssociateAccountCommand.newBuilder()
                .setBotAccountId(accountId)
                .build())
                .toCompletableFuture().get();

        // then

        // All Ok. No exception.
        Assert.assertTrue(true);

    }

}
