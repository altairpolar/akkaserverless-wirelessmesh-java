package bot.account;

import com.akkaserverless.javasdk.eventsourcedentity.CommandContext;
import org.junit.Test;
import org.mockito.*;
import bot.account.BotAccountDomain.*;
import bot.account.BotAccountAPI.*;

import java.util.UUID;

public class BotAccountEntityUnitTest {

    @Test
    public void registerAccount_happyCase() {

        // given
        String accountId = UUID.randomUUID().toString();
        String email = "me@you.com";

        CommandContext context = Mockito.mock(CommandContext.class);
        BotAccountEntity account = new BotAccountEntity(accountId);
        BotAccountRegistered accountAdded = BotAccountRegistered.newBuilder()
                .setId(accountId)
                .setEmail(email)
                .build();

        // when
        account.registerAccount(RegisterAccountCommand.newBuilder()
                .setId(accountId)
                .setEmail(email)
                .build(), context);

        // then
        Mockito.verify(context).emit(accountAdded);

        // Simulate event callback to drive state change.
        account.accountRegistered(accountAdded);

    }

    @SuppressWarnings("ThrowableNotThrown")
    @Test
    public void registerAccount_errorSameIdTwice() {

        // given
        CommandContext context = Mockito.mock(CommandContext.class);

        String accountId = UUID.randomUUID().toString();
        String email = "me@you.com";

        BotAccountEntity account = new BotAccountEntity(accountId);
        BotAccountRegistered botAccountRegistered = BotAccountRegistered.newBuilder()
                .setId(accountId)
                .setEmail(email)
                .build();

        account.registerAccount(RegisterAccountCommand.newBuilder()
                .setId(accountId)
                .setEmail(email)
                .build(), context);

        // Simulate event callback to drive state change.
        account.accountRegistered(botAccountRegistered);

        // when
        account.registerAccount(RegisterAccountCommand.newBuilder()
                .setId(accountId)
                .setEmail(email)
                .build(), context);

        // then
        Mockito.verify(context).fail(BotAccountEntity.ACCOUNT_IS_ALREADY_REGISTERED);
    }

    @SuppressWarnings("ThrowableNotThrown")
    @Test
    public void registerAccount_invalidEmail() {

        // given
        CommandContext context = Mockito.mock(CommandContext.class);

        String accountId = UUID.randomUUID().toString();
        String email = UUID.randomUUID().toString();

        BotAccountEntity account = new BotAccountEntity(accountId);

        // when
        account.registerAccount(RegisterAccountCommand.newBuilder()
                .setId(accountId)
                .setEmail(email)
                .build(), context);

        // then
        Mockito.verify(context).fail(BotAccountEntity.ACCOUNT_EMAIL_IS_NOT_A_VALID_EMAIL);

    }



}
