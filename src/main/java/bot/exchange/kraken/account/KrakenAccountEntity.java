package bot.exchange.kraken.account;


import bot.BotMain;
import bot.exchange.kraken.access.KrakenApi;
import bot.exchange.kraken.access.typed.HttpApiClientFactory;
import bot.exchange.kraken.access.typed.KrakenAPIClient;
import bot.exchange.kraken.access.typed.KrakenApiException;
import bot.exchange.kraken.access.typed.result.AccountBalanceResult;
import  bot.exchange.kraken.account.KrakenAccountDomain.*;
import  bot.exchange.kraken.account.KrakenAccountAPI.*;
import com.akkaserverless.javasdk.EntityId;
import com.akkaserverless.javasdk.eventsourcedentity.CommandContext;
import com.akkaserverless.javasdk.eventsourcedentity.CommandHandler;
import com.akkaserverless.javasdk.eventsourcedentity.EventHandler;
import com.akkaserverless.javasdk.eventsourcedentity.EventSourcedEntity;
import com.google.protobuf.Empty;
import common.domain.BotDomainEntity;

import java.io.IOException;
import java.security.InvalidKeyException;
import java.security.NoSuchAlgorithmException;
import java.util.HashMap;
import java.util.Map;
import java.util.concurrent.ExecutionException;

/**
 * A Kraken Exchange Account entity.
 * <p>
 * As an entity, I will be seeded with my current state upon loading and thereafter will completely serve the
 * backend needs for a particular device. There is no practical limit to how many of these entities you can have,
 * across the application cluster. Look at each instance of this entity as being roughly equivalent to a row in a
 * database, only each one is completely addressable and in memory.
 * <p>
 * Event sourcing was selected in order to have complete traceability into the behavior of accounts for the purposes
 * of security, analytics and simulation.
 */
@EventSourcedEntity(entityType = "exchange-kraken-account")
public class KrakenAccountEntity implements BotDomainEntity<KrakenAccountState> {

    public static final String BOT_AND_KRAKEN_IDS_CANNOT_BE_EMPTY = "Bot and Kraken Account Ids cannot be empty";
    public static final String KRAKEN_ACCOUNT_IS_ALREADY_ASSOCIATED = "Bot Account is already associated with this Kraken Account";
    public static final String KRAKEN_ACCOUNT_IS_ALREADY_DISSOCIATED = "Bot Account is already dissociated from this Kraken Account";
    public static final String KRAKEN_API_KEY_SHORTEN_THAN_10 = "API Key must contain at least 10 characters";
    public static final String KRAKEN_API_SECRET_SHORTEN_THAN_10 = "API Secret must contain at least 10 characters";

    @SuppressWarnings({"FieldCanBeLocal", "unused"})
    private String krakenAccountId;

    private String botAccountId;

    private boolean isAssociated = false;

    private boolean isDissociated = false;

    private String apiKey = "";

    private String apiSecret = "";

    /**
     * Constructor.
     *
     * @param krakenAccountId This will be the entity id.
     */
    public KrakenAccountEntity(@EntityId String krakenAccountId) {
        this.krakenAccountId = krakenAccountId;
    }


    // COMMANDS

    /**
     * This is the command handler for adding an account as defined in protobuf.
     *
     * @param associateAccountCommand the command message from protobuf
     * @param ctx                    the application context
     * @return Empty (unused)
     */
    @SuppressWarnings("ThrowableNotThrown")
    @CommandHandler
    public Empty associateAccount(AssociateAccountCommand associateAccountCommand, CommandContext ctx) {
        if (associateAccountCommand.getBotAccountId().equals("")
                || associateAccountCommand.getKrakenAccountId().equals("")) {
            ctx.fail(BOT_AND_KRAKEN_IDS_CANNOT_BE_EMPTY);
        } else
        if (isAssociated) {
            ctx.fail(KRAKEN_ACCOUNT_IS_ALREADY_ASSOCIATED);
        } else {
            KrakenAccountAssociated event = KrakenAccountAssociated.newBuilder()
                    .setKrakenAccountId(associateAccountCommand.getKrakenAccountId())
                    .setBotAccountId(associateAccountCommand.getBotAccountId())
                    .build();

            ctx.emit(event);
        }

        return Empty.getDefaultInstance();
    }

    @SuppressWarnings("ThrowableNotThrown")
    @CommandHandler
    public Empty dissociateAccount(DissociateAccountCommand dissociateAccountCommand, CommandContext ctx) {
        if (isDissociated) {
            ctx.fail(KRAKEN_ACCOUNT_IS_ALREADY_DISSOCIATED);
        } else {
            KrakenAccountDissociated event = KrakenAccountDissociated.newBuilder()
                    .setKrakenAccountId(dissociateAccountCommand.getKrakenAccountId())
                    .build();

            ctx.emit(event);
        }

        return Empty.getDefaultInstance();
    }

    @SuppressWarnings("ThrowableNotThrown")
    @CommandHandler
    public Empty assignAPIKeys(AssignAPIKeysCommand assignAPIKeysCommand, CommandContext ctx) {
        if (assignAPIKeysCommand.getApiKey().length() < 10) {
            ctx.fail(KRAKEN_API_KEY_SHORTEN_THAN_10);
        } else if (assignAPIKeysCommand.getApiSecret().length() < 10) {
            ctx.fail(KRAKEN_API_SECRET_SHORTEN_THAN_10);
        } else {
            KrakenAPIKeysAssigned event = KrakenAPIKeysAssigned.newBuilder()
                    .setKrakenAccountId(assignAPIKeysCommand.getKrakenAccountId())
                    .setApiKey(assignAPIKeysCommand.getApiKey())
                    .setApiSecret(assignAPIKeysCommand.getApiSecret())
                    .build();

            ctx.emit(event);
        }

        return Empty.getDefaultInstance();
    }

    @CommandHandler
    public TestAPIKeysResponse testAPIKeys(TestAPIKeysCommand testAPIKeysCommand, CommandContext ctx) {

        // TYPED API
        HttpApiClientFactory httpApiClientFactory = new HttpApiClientFactory();
        KrakenAPIClient client = new KrakenAPIClient(this.apiKey, this.apiSecret, httpApiClientFactory);
        try {
            AccountBalanceResult result = client.getAccountBalance();
            System.out.println(result);
        } catch (KrakenApiException e) {
            return TestAPIKeysResponse.newBuilder()
                    .setSuccess(false)
                    .setErrorMessage(e.getMessage())
                    .build();
        }

        return TestAPIKeysResponse.newBuilder()
                .setSuccess(true)
                .setErrorMessage("")
                .build();

/*

        UNTYPED API

        KrakenApi api = new KrakenApi();

        String response;
        Map<String, String> input = new HashMap<>();
        input.clear();
        input.put("asset", "ZEUR");
        try {
            response = api.queryPrivate(KrakenApi.Method.BALANCE, input);
        } catch (IOException e) {
            ctx.fail(e.getMessage());
        } catch (InvalidKeyException e) {
            ctx.fail(e.getMessage());
        } catch (NoSuchAlgorithmException e) {
            ctx.fail(e.getMessage());
        }
*/
    }

    // EVENTS

    /**
     * This is the event handler for associating a Kraken Account with a Bot Account.
     * It is here we update current state due to successful storage to the Event Log.
     *
     * @param krakenAccountAssociated the event previously emitted in the command handler, now safely stored.
     */
    @EventHandler
    public void accountAssociated(@SuppressWarnings("unused") KrakenAccountAssociated krakenAccountAssociated) {
        this.isAssociated = true;
    }

    /**
     * This is the event handler for dissociating a Kraken Account with a Bot Account.
     * It is here we update current state due to successful storage to the Event Log.
     *
     * @param krakenAccountDissociated the event previously emitted in the command handler, now safely stored.
     */
    @EventHandler
    public void accountDissociated(@SuppressWarnings("unused") KrakenAccountDissociated krakenAccountDissociated) {
        this.isDissociated = true;
    }

    /**
     * This is the event handler for changing API Keys.
     * It is here we update current state due to successful storage to the Event Log.
     *
     * @param krakenAPIKeysAssigned the event previously emitted in the command handler, now safely stored.
     */
    @EventHandler
    public void apiKeysAssigned(@SuppressWarnings("unused") KrakenAPIKeysAssigned krakenAPIKeysAssigned) {
        this.apiKey = krakenAPIKeysAssigned.getApiKey();
        this.apiSecret = krakenAPIKeysAssigned.getApiSecret();
    }

    // SNAPSHOTS
    @Override
    public KrakenAccountState snapshot() {
        return KrakenAccountState.newBuilder()
                .setBotAccountId(this.botAccountId)
                .setApiKey(this.apiKey)
                .setApiSecret(this.apiSecret)
                .build();
    }

    @Override
    public void handleSnapshot(KrakenAccountState state) {
        this.botAccountId = state.getBotAccountId();
        this.apiKey = state.getApiKey();
        this.apiSecret = state.getApiSecret();

    }

}
