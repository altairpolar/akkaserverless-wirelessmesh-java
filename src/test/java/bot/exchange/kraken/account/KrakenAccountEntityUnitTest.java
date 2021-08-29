package bot.exchange.kraken.account;

import bot.account.BotAccountEntity;
import bot.account.BotAccountAPI.RegisterAccountCommand;
import bot.account.BotAccountDomain.BotAccountRegistered;
import bot.exchange.kraken.account.KrakenAccountDomain.*;
import com.akkaserverless.javasdk.eventsourcedentity.CommandContext;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.mockito.Mockito;
import java.util.UUID;

public class KrakenAccountEntityUnitTest {

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    @Test
    public void associateAccount() {

        // given
        CommandContext context = Mockito.mock(CommandContext.class);

        // A Bot Account.
        String botAccountId = UUID.randomUUID().toString();
        String email = "me@you.com";

        @SuppressWarnings("unused")
        BotAccountEntity botAccountEntity = registerTestAccount(botAccountId, email, context);

        // A Kraken Account.
        String krakenAccountId = UUID.randomUUID().toString();
        KrakenAccountEntity krakenAccountEntity = new KrakenAccountEntity(krakenAccountId);

        KrakenAccountAssociated krakenAccountAssociated = KrakenAccountAssociated.newBuilder()
                .setKrakenAccountId(krakenAccountId)
                .setBotAccountId(botAccountId)
                .build();

        // when
        krakenAccountEntity.associateAccount(KrakenAccountAPI.AssociateAccountCommand.newBuilder()
                .setKrakenAccountId(krakenAccountId)
                .setBotAccountId(botAccountId)
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
                .setKrakenAccountId(krakenAccountId)
                .setBotAccountId(botAccountId)
                .build();

        // when
        krakenAccountEntity.associateAccount(KrakenAccountAPI.AssociateAccountCommand.newBuilder()
                .setKrakenAccountId(krakenAccountId)
                .setBotAccountId(botAccountId)
                .build(), context);

        // then
        Mockito.verify(context).emit(krakenAccountAssociated);
        // fail("aa"); // emit(krakenAccountAdded);

        // Simulate event callback to drive state change.
        krakenAccountEntity.accountAssociated(krakenAccountAssociated);

    }

    @Test
    public void dissociateAccount() {

        // given

        CommandContext context = Mockito.mock(CommandContext.class);

        // An aleatory, invalid Bot Account Id.
        String botAccountId = UUID.randomUUID().toString();

        // A Kraken Account.
        String krakenAccountId = UUID.randomUUID().toString();
        KrakenAccountEntity krakenAccountEntity = new KrakenAccountEntity(krakenAccountId);

        KrakenAccountDissociated krakenAccountDissociated = KrakenAccountDissociated.newBuilder()
                .setKrakenAccountId(krakenAccountId)
                .build();

        // when
        krakenAccountEntity.dissociateAccount(KrakenAccountAPI.DissociateAccountCommand.newBuilder()
                .setKrakenAccountId(krakenAccountId)
                .build(), context);

        // then
        Mockito.verify(context).emit(krakenAccountDissociated);
        // fail("aa"); // emit(krakenAccountAdded);

        // Simulate event callback to drive state change.
        krakenAccountEntity.accountDissociated(krakenAccountDissociated);

    }

}
