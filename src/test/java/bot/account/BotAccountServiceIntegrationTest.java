package bot.account;

import bot.BotMain;
import com.akkaserverless.javasdk.testkit.junit.AkkaServerlessTestkitResource;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import static org.junit.Assert.*;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

import bot.account.BotAccountAPI.*;
import org.junit.rules.ExpectedException;

public class BotAccountServiceIntegrationTest {

    private final BotAccountServiceClient accountServiceClient;

    @ClassRule
    public static final AkkaServerlessTestkitResource testkit = new AkkaServerlessTestkitResource(
            BotMain.botAkkaServerless);

    public BotAccountServiceIntegrationTest() {
        accountServiceClient = BotAccountServiceClient.create(testkit.getGrpcClientSettings(), testkit.getActorSystem());
    }

    @Test
    public void registerAccount_happyCase() throws ExecutionException, InterruptedException {

        // given
        String accountId = UUID.randomUUID().toString();
        String email = "me@you.com";

        // when
        accountServiceClient.registerAccount(RegisterAccountCommand.newBuilder()
                .setId(accountId)
                .setEmail(email)
                .build())
                .toCompletableFuture().get();

        // then

        // All Ok. No exception.
        Assert.assertTrue(true);

    }

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void registerAccount_failsIfInvalidEmail() throws ExecutionException, InterruptedException {

        // given
        String accountId = UUID.randomUUID().toString();
        String email = "invalid email";

        // when
        exceptionRule.expect(ExecutionException.class);
        exceptionRule.expectMessage(BotAccountEntity.ACCOUNT_EMAIL_IS_NOT_A_VALID_EMAIL);
        accountServiceClient.registerAccount(RegisterAccountCommand.newBuilder()
                        .setId(accountId)
                        .setEmail(email)
                        .build())
                        .toCompletableFuture().get();

        // then
        Assert.fail(); // Should not arrive here.

    }

    @Test
    public void registerAccount_failsIfSameIdTwice() throws ExecutionException, InterruptedException {

        // given
        String accountId = UUID.randomUUID().toString();
        String email = "email@here.com";

        // when
        accountServiceClient.registerAccount(RegisterAccountCommand.newBuilder()
                .setId(accountId)
                .setEmail(email)
                .build())
                .toCompletableFuture().get();

        // then
        exceptionRule.expect(ExecutionException.class);
        exceptionRule.expectMessage(BotAccountEntity.ACCOUNT_IS_ALREADY_REGISTERED);
        accountServiceClient.registerAccount(RegisterAccountCommand.newBuilder()
                .setId(accountId)
                .setEmail(email)
                .build())
                .toCompletableFuture().get();

    }

    @Test
    public void getAccount_happyCase() throws ExecutionException, InterruptedException {

        // given
        String accountId = UUID.randomUUID().toString();
        String email = "email@here.com";

        accountServiceClient.registerAccount(RegisterAccountCommand.newBuilder()
                .setId(accountId)
                .setEmail(email)
                .build())
                .toCompletableFuture().get();

        // when
        GetAccountResponse getAccountResponse = accountServiceClient.getAccount(GetAccountCommand.newBuilder()
                .setId(accountId)
                .build())
                .toCompletableFuture().get();

        // then
        assertEquals(accountId, getAccountResponse.getId());
        assertEquals(email, getAccountResponse.getEmail());

    }

    @Test
    public void getAccount_failsIfIdNotExists() throws ExecutionException, InterruptedException {

        // given
        String accountId = UUID.randomUUID().toString();
        String email = "email@here.com";

        accountServiceClient.registerAccount(RegisterAccountCommand.newBuilder()
                .setId(accountId)
                .setEmail(email)
                .build())
                .toCompletableFuture().get();

        // when
        exceptionRule.expect(ExecutionException.class);
        exceptionRule.expectMessage(BotAccountEntity.ACCOUNT_DOES_NOT_EXIST);
        accountServiceClient.getAccount(GetAccountCommand.newBuilder()
                .setId(UUID.randomUUID().toString())
                .build())
                .toCompletableFuture().get();

        // then
        fail(); // Should never arrive.

    }



    @Test
    public void getAccount_failsIfDeactivated() throws ExecutionException, InterruptedException {

        // given
        String accountId = UUID.randomUUID().toString();
        String email = "email@here.com";

        accountServiceClient.registerAccount(RegisterAccountCommand.newBuilder()
                .setId(accountId)
                .setEmail(email)
                .build())
                .toCompletableFuture().get();

        // when
        exceptionRule.expect(ExecutionException.class);
        exceptionRule.expectMessage(BotAccountEntity.ACCOUNT_DOES_NOT_EXIST);
        accountServiceClient.getAccount(GetAccountCommand.newBuilder()
                .setId(UUID.randomUUID().toString())
                .build())
                .toCompletableFuture().get();

        // then
        fail(); // Should never arrive.

    }

    @Test
    public void deactivateAccount() throws ExecutionException, InterruptedException {

        // given
        String accountId = UUID.randomUUID().toString();
        String email = "me@you.com";
        accountServiceClient.registerAccount(RegisterAccountCommand.newBuilder()
                .setId(accountId)
                .setEmail(email)
                .build())
                .toCompletableFuture().get();

        // when
        accountServiceClient.deactivateAccount(DeactivateAccountCommand.newBuilder()
                .setId(accountId)
                .build())
                .toCompletableFuture().get();

        // then

        // All Ok. No exception.
        Assert.assertTrue(true);

    }

    @Test
    public void deactivateAccount_failsIfNotExists() throws ExecutionException, InterruptedException {

        // given
        String accountId = UUID.randomUUID().toString();

        // when
        exceptionRule.expect(ExecutionException.class);
        exceptionRule.expectMessage(BotAccountEntity.ACCOUNT_DOES_NOT_EXIST);
        accountServiceClient.deactivateAccount(DeactivateAccountCommand.newBuilder()
                .setId(accountId)
                .build())
                .toCompletableFuture().get();

        // then
        fail(); // Should not arrive.

    }

}
