package bot.exchange.kraken.account;

import bot.BotMain;
import bot.account.BotAccountAPI;
import bot.account.BotAccountServiceClient;
import bot.exchange.kraken.account.KrakenAccountAPI.*;
import com.akkaserverless.javasdk.testkit.junit.AkkaServerlessTestkitResource;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class KrakenAccountServiceIntegrationTest {

    private final BotAccountServiceClient botAccountServiceClient;
    private final KrakenAccountServiceClient krakenAccountServiceClient;

    @ClassRule
    public static final AkkaServerlessTestkitResource testkit = new AkkaServerlessTestkitResource(
            BotMain.botAkkaServerless);

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    public KrakenAccountServiceIntegrationTest() {
        botAccountServiceClient = BotAccountServiceClient.create(testkit.getGrpcClientSettings(), testkit.getActorSystem());
        krakenAccountServiceClient = KrakenAccountServiceClient.create(testkit.getGrpcClientSettings(), testkit.getActorSystem());
    }

    @Test
    public void associateAccount() throws ExecutionException, InterruptedException {

        // given
        String botAccountId = UUID.randomUUID().toString();
        String krakenAccountId = UUID.randomUUID().toString();
        String email = "me@you.com";

        // when
        createAssociatedKrakenAccount(botAccountId, krakenAccountId, email);

        // then

        // All Ok. No exception.
        Assert.assertTrue(true);

    }

    @Test
    public void associateAccount_okAlsoForInvalidAccount() throws ExecutionException, InterruptedException {

        // given
        String unregisteredBotAccountId = UUID.randomUUID().toString();
        String krakenAccountId = UUID.randomUUID().toString();

        // when
        krakenAccountServiceClient.associateAccount(AssociateAccountCommand.newBuilder()
                .setKrakenAccountId(krakenAccountId)
                .setBotAccountId(unregisteredBotAccountId)
                .build())
                .toCompletableFuture().get();

        // then

        // All Ok. No exception.
        Assert.assertTrue(true);

    }

    @Test
    public void dissociateAccount() throws ExecutionException, InterruptedException {

        // given
        String unregisteredBotAccountId = UUID.randomUUID().toString();
        String krakenAccountId = UUID.randomUUID().toString();
        krakenAccountServiceClient.associateAccount(AssociateAccountCommand.newBuilder()
                .setKrakenAccountId(krakenAccountId)
                .setBotAccountId(unregisteredBotAccountId)
                .build())
                .toCompletableFuture().get();

        // when
        krakenAccountServiceClient.dissociateAccount(KrakenAccountAPI.DissociateAccountCommand.newBuilder()
                .setKrakenAccountId(krakenAccountId)
                .build())
                .toCompletableFuture().get();

        // then
        Assert.assertTrue(true);

    }

    @Test
    public void dissociateAccount_failsIfKrakenAccountNotAssociated() throws ExecutionException, InterruptedException {

        // given
        String unregisteredBotAccountId = UUID.randomUUID().toString();
        String krakenAccountId = UUID.randomUUID().toString();
        krakenAccountServiceClient.associateAccount(AssociateAccountCommand.newBuilder()
                .setBotAccountId(unregisteredBotAccountId)
                .setKrakenAccountId(krakenAccountId)
                .build())
                .toCompletableFuture().get();

        krakenAccountServiceClient.dissociateAccount(KrakenAccountAPI.DissociateAccountCommand.newBuilder()
                .setKrakenAccountId(krakenAccountId)
                .build())
                .toCompletableFuture().get();

        // when
        exceptionRule.expect(ExecutionException.class);
        exceptionRule.expectMessage(KrakenAccountEntity.KRAKEN_ACCOUNT_IS_ALREADY_DISSOCIATED);
        krakenAccountServiceClient.dissociateAccount(KrakenAccountAPI.DissociateAccountCommand.newBuilder()
                .setKrakenAccountId(krakenAccountId)
                .build())
                .toCompletableFuture().get();

        // then
        Assert.fail(); // Should not arrive here.

    }

    @Test
    public void assignAPIKeys() throws ExecutionException, InterruptedException {

        // given
        String botAccountId = UUID.randomUUID().toString();
        String krakenAccountId = UUID.randomUUID().toString();
        String email = "me@you.com";
        createAssociatedKrakenAccount(botAccountId, krakenAccountId, email);

        String apiKey = UUID.randomUUID().toString();
        String apiSecret = UUID.randomUUID().toString();

        // when
        krakenAccountServiceClient.assignAPIKeys(AssignAPIKeysCommand.newBuilder()
                .setKrakenAccountId(krakenAccountId)
                .setApiKey(apiKey)
                .setApiSecret(apiSecret)
                .build())
                .toCompletableFuture().get();

        // then

        // All Ok. No exception.
        Assert.assertTrue(true);

    }

    @Test
    public void assignAPIKeys_failsIfAPIKeyIsShorterThan10() throws ExecutionException, InterruptedException {

        // given
        String botAccountId = UUID.randomUUID().toString();
        String krakenAccountId = UUID.randomUUID().toString();
        String email = "me@you.com";
        createAssociatedKrakenAccount(botAccountId, krakenAccountId, email);

        String apiKey = "123456789";
        String apiSecret = UUID.randomUUID().toString();

        // when
        exceptionRule.expect(ExecutionException.class);
        exceptionRule.expectMessage(KrakenAccountEntity.KRAKEN_API_KEY_SHORTEN_THAN_10);
        krakenAccountServiceClient.assignAPIKeys(AssignAPIKeysCommand.newBuilder()
                .setKrakenAccountId(krakenAccountId)
                .setApiKey(apiKey)
                .setApiSecret(apiSecret)
                .build())
                .toCompletableFuture().get();

        // then

        // All Ok. No exception.
        Assert.assertTrue(true);

    }

    @Test
    public void assignAPIKeys_failsIfAPISecretIsShorterThan10() throws ExecutionException, InterruptedException {

        // given
        String botAccountId = UUID.randomUUID().toString();
        String krakenAccountId = UUID.randomUUID().toString();
        String email = "me@you.com";
        createAssociatedKrakenAccount(botAccountId, krakenAccountId, email);

        String apiKey = UUID.randomUUID().toString();
        String apiSecret = "123456789";

        // when
        //        https://developer.lightbend.com/docs/akka-serverless/java/kickstart.html
        //assertThrows(MockedContextFailure.class, () -> {
        //  entity.increaseWithReply(CounterApi.IncreaseValue.newBuilder().build(), context);
        //});/
        exceptionRule.expect(ExecutionException.class);
        exceptionRule.expectMessage(KrakenAccountEntity.KRAKEN_API_SECRET_SHORTEN_THAN_10);
        krakenAccountServiceClient.assignAPIKeys(AssignAPIKeysCommand.newBuilder()
                .setKrakenAccountId(krakenAccountId)
                .setApiKey(apiKey)
                .setApiSecret(apiSecret)
                .build())
                .toCompletableFuture().get();

        // then

        // All Ok. No exception.
        Assert.assertTrue(true);

    }

    @Test
    @Ignore("Cannot invoke each test execution at risk of being banned. Would required a mocked version")
    public void testAPIKeys() throws ExecutionException, InterruptedException {

        String botAccountId = UUID.randomUUID().toString();
        String krakenAccountId = UUID.randomUUID().toString();
        String email = "me@you.com";
        createAssociatedKrakenAccount(botAccountId, krakenAccountId, email);

        String apiPublicKey = ""; // FIX-ME
        String apiSecretKey = ""; // FIX-ME
        krakenAccountServiceClient.assignAPIKeys(AssignAPIKeysCommand.newBuilder()
                .setKrakenAccountId(krakenAccountId)
                .setApiKey(apiPublicKey)
                .setApiSecret(apiSecretKey)
                .build())
                .toCompletableFuture().get();

        // when
        TestAPIKeysResponse testAPIKeysResponse = krakenAccountServiceClient.testAPIKeys(TestAPIKeysCommand.newBuilder()
                .setKrakenAccountId(krakenAccountId)
                .build())
                .toCompletableFuture().get();

        // then

        // All Ok. No exception.
        Assert.assertTrue(testAPIKeysResponse.getSuccess());
        Assert.assertEquals("", testAPIKeysResponse.getErrorMessage());

    }

    @Test
    public void testUpdateBalance() throws ExecutionException, InterruptedException {

        String botAccountId = UUID.randomUUID().toString();
        String krakenAccountId = UUID.randomUUID().toString();
        String email = "me@you.com";

        String apiPublicKey ="";
        String apiSecretKey = "";
        createAssociatedKrakenAccountWithAPIKeys(botAccountId, krakenAccountId, email, apiPublicKey, apiSecretKey);

        krakenAccountServiceClient.assignAPIKeys(AssignAPIKeysCommand.newBuilder()
                .setKrakenAccountId(krakenAccountId)
                .setApiKey(apiPublicKey)
                .setApiSecret(apiSecretKey)
                .build())
                .toCompletableFuture().get();

        // when
        UpdateBalanceResponse updateBalanceResponse = krakenAccountServiceClient.updateBalance(UpdateBalanceCommand.newBuilder()
                .setKrakenAccountId(krakenAccountId)
                .build())
                .toCompletableFuture().get();

        // then
        Assert.assertNotSame("", updateBalanceResponse.getCostBasis());

    }

    private void createAssociatedKrakenAccountWithAPIKeys(String botAccountId, String krakenAccountId, String email, String apiKey, String apiSecret) throws InterruptedException, ExecutionException {
        botAccountServiceClient.registerAccount(BotAccountAPI.RegisterAccountCommand.newBuilder()
                .setId(botAccountId)
                .setEmail(email)
                .build())
                .toCompletableFuture().get();

        krakenAccountServiceClient.associateAccount(KrakenAccountAPI.AssociateAccountCommand.newBuilder()
                .setBotAccountId(botAccountId)
                .setKrakenAccountId(krakenAccountId)
                .build())
                .toCompletableFuture().get();

        krakenAccountServiceClient.assignAPIKeys(KrakenAccountAPI.AssignAPIKeysCommand.newBuilder()
                .setKrakenAccountId(krakenAccountId)
                .setApiKey(apiKey)
                .setApiSecret(apiSecret)
                .build())
                .toCompletableFuture().get();
    }


    private void createAssociatedKrakenAccount(String botAccountId, String krakenAccountId, String email) throws InterruptedException, ExecutionException {
        botAccountServiceClient.registerAccount(BotAccountAPI.RegisterAccountCommand.newBuilder()
                .setId(botAccountId)
                .setEmail(email)
                .build())
                .toCompletableFuture().get();

        krakenAccountServiceClient.associateAccount(AssociateAccountCommand.newBuilder()
                .setBotAccountId(botAccountId)
                .setKrakenAccountId(krakenAccountId)
                .build())
                .toCompletableFuture().get();
    }



}
