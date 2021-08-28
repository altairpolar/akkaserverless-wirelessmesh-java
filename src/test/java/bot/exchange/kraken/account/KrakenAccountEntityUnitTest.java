package bot.exchange.kraken.account;

import bot.account.BotAccountEntity;
import bot.account.BotAccountAPI.RegisterAccountCommand;
import bot.account.BotAccountDomain.BotAccountRegistered;
import bot.exchange.kraken.account.KrakenAccountDomain.*;
import com.akkaserverless.javasdk.eventsourcedentity.CommandContext;
import org.junit.Test;
import org.mockito.Mockito;

import java.util.UUID;

public class KrakenAccountEntityUnitTest {

    @Test
    public void associateAccount_happyCase() {

        // given
        CommandContext context = Mockito.mock(CommandContext.class);

        // A Bot Account.
        String accountId = UUID.randomUUID().toString();
        String email = "me@you.com";

        @SuppressWarnings("unused")
        BotAccountEntity botAccountEntity = registerTestAccount(accountId, email, context);

        // A Kraken Account.
        String krakenAccountId = UUID.randomUUID().toString();
        KrakenAccountEntity krakenAccountEntity = new KrakenAccountEntity(krakenAccountId);

        KrakenAccountAssociated krakenAccountAssociated = KrakenAccountAssociated.newBuilder()
                .setBotAccountId(accountId)
                .build();

        // when
        krakenAccountEntity.associateAccount(KrakenAccountAPI.AssociateAccountCommand.newBuilder()
                .setBotAccountId(accountId)
                .build(), context);

        // then
        Mockito.verify(context).emit(krakenAccountAssociated);
                // fail("aa"); // emit(krakenAccountAdded);

        // Simulate event callback to drive state change.
        krakenAccountEntity.accountAssociated(krakenAccountAssociated);

    }

    private BotAccountEntity registerTestAccount(String accountId, String email, CommandContext context) {
        BotAccountEntity account = new BotAccountEntity(accountId);

        account.registerAccount(RegisterAccountCommand.newBuilder()
                .setId(accountId)
                .setEmail(email)
                .build(), context);

        // Verify it has been successfully created.
        BotAccountRegistered accountRegistered = BotAccountRegistered.newBuilder()
                .setId(accountId)
                .setEmail(email)
                .build();

        Mockito.verify(context).emit(accountRegistered);

        // Simulate event callback to drive state change.
        account.accountRegistered(accountRegistered);

        return account;

    }

    @Test
    public void associateAccount_okIfInvalidBotAccount() {

        // given
        CommandContext context = Mockito.mock(CommandContext.class);

        // An aleatory, invalid Bot Account Id.
        String botAccountId = UUID.randomUUID().toString();

        // A Kraken Account.
        String krakenAccountId = UUID.randomUUID().toString();
        KrakenAccountEntity krakenAccountEntity = new KrakenAccountEntity(krakenAccountId);

        KrakenAccountAssociated krakenAccountAssociated = KrakenAccountAssociated.newBuilder()
                .setBotAccountId(botAccountId)
                .build();

        // when
        krakenAccountEntity.associateAccount(KrakenAccountAPI.AssociateAccountCommand.newBuilder()
                .setBotAccountId(botAccountId)
                .build(), context);

        // then
        Mockito.verify(context).emit(krakenAccountAssociated);
        // fail("aa"); // emit(krakenAccountAdded);

        // Simulate event callback to drive state change.
        krakenAccountEntity.accountAssociated(krakenAccountAssociated);

    }

    @Test
    public void dissociateAccount_happyCase() {

        // given

        CommandContext context = Mockito.mock(CommandContext.class);

        // An aleatory, invalid Bot Account Id.
        String botAccountId = UUID.randomUUID().toString();

        // A Kraken Account.
        String krakenAccountId = UUID.randomUUID().toString();
        KrakenAccountEntity krakenAccountEntity = new KrakenAccountEntity(krakenAccountId);

        KrakenAccountDissociated krakenAccountDissociated = KrakenAccountDissociated.newBuilder()
                .setBotAccountId(botAccountId)
                .build();

        // when
        krakenAccountEntity.dissociateAccount(KrakenAccountAPI.DissociateAccountCommand.newBuilder()
                .setBotAccountId(botAccountId)
                .build(), context);

        // then
        Mockito.verify(context).emit(krakenAccountDissociated);
        // fail("aa"); // emit(krakenAccountAdded);

        // Simulate event callback to drive state change.
        krakenAccountEntity.accountDissociated(krakenAccountDissociated);

    }

}
