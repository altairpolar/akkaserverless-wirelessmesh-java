package bot.account;

import bot.BotMain;
import bot.account.AccountServiceClient;
import com.akkaserverless.javasdk.testkit.junit.AkkaServerlessTestkitResource;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Test;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import bot.account.AccountManagementAPI.*;

public class AccountServiceIntegrationTest {

    private final AccountServiceClient accountServiceClient;

    @ClassRule
    public static final AkkaServerlessTestkitResource testkit = new AkkaServerlessTestkitResource(
            BotMain.botAkkaServerless);

    public AccountServiceIntegrationTest() {
        accountServiceClient = AccountServiceClient.create(testkit.getGrpcClientSettings(), testkit.getActorSystem());
    }

    @Test
    public void registerAccount() throws ExecutionException, InterruptedException {

        // given
        String accountId = UUID.randomUUID().toString();
        String email = "me@you.com";

        // when
        accountServiceClient.registerAccount(RegisterAccountCommand.newBuilder()
                .setAccountId(accountId)
                .setEmail(email)
                .build())
                .toCompletableFuture().get();

        // then

        // All Ok. No exception.
        Assert.assertTrue(true);

    }

}
