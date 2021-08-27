package bot.account;

import bot.account.AccountEntity;
import com.akkaserverless.javasdk.eventsourcedentity.CommandContext;
import org.junit.Test;
import org.mockito.*;
import bot.account.AccountManagementDomain.*;
import bot.account.AccountManagementAPI.*;

import java.util.UUID;

pendientes los tests de kraken.

public class AccountEntityUnitTest {

    @Test
    public void registerAccount_happyCase() {

        // given
        String accountId = UUID.randomUUID().toString();
        String email = "me@you.com";

        CommandContext context = Mockito.mock(CommandContext.class);
        AccountEntity account = new AccountEntity(accountId);
        AccountRegistered accountAdded = AccountRegistered.newBuilder()
                .setId(AccountId.newBuilder()
                        .setId(accountId))
                .setEmail(email)
                .build();

        // when
        account.registerAccount(RegisterAccountCommand.newBuilder()
                .setAccountId(accountId)
                .setEmail(email)
                .build(), context);

        // then
        Mockito.verify(context).emit(accountAdded);

        // Simulate event callback to drive state change.
        account.accountRegistered(accountAdded);

    }

    @Test
    public void registerAccount_errorSameIdTwice() {

        // given
        CommandContext context = Mockito.mock(CommandContext.class);

        String accountId = UUID.randomUUID().toString();
        String email = "me@you.com";

        AccountEntity account = new AccountEntity(accountId);
        AccountRegistered accountAdded = AccountRegistered.newBuilder()
                .setId(AccountId.newBuilder()
                        .setId(accountId))
                .setEmail(email)
                .build();

        account.registerAccount(RegisterAccountCommand.newBuilder()
                .setAccountId(accountId)
                .setEmail(email)
                .build(), context);

        // Simulate event callback to drive state change.
        account.accountRegistered(accountAdded);

        // when
        account.registerAccount(RegisterAccountCommand.newBuilder()
                .setAccountId(accountId)
                .setEmail(email)
                .build(), context);

        // then
        Mockito.verify(context).fail(AccountEntity.ACCOUNT_IS_ALREADY_REGISTERED);
    }

    @Test
    public void registerAccount_invalidEmail() {

        // given
        CommandContext context = Mockito.mock(CommandContext.class);

        String accountId = UUID.randomUUID().toString();
        String email = UUID.randomUUID().toString();

        AccountEntity account = new AccountEntity(accountId);

        // when
        account.registerAccount(RegisterAccountCommand.newBuilder()
                .setAccountId(accountId)
                .setEmail(email)
                .build(), context);

        // then
        Mockito.verify(context).fail(AccountEntity.ACCOUNT_EMAIL_IS_NOT_A_VALID_EMAIL);

    }



}
