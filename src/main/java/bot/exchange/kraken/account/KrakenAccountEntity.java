package bot.exchange.kraken.account;


import akka.japi.Option;
import bot.BotMain;
import bot.exchange.kraken.access.typed.KrakenAPIClient;
import bot.exchange.kraken.access.typed.KrakenApiException;
import bot.exchange.kraken.access.typed.result.AccountBalanceResult;
import bot.exchange.kraken.access.typed.result.TradeBalanceResult;
import  bot.exchange.kraken.account.KrakenAccountDomain.*;
import  bot.exchange.kraken.account.KrakenAccountAPI.*;
import com.akkaserverless.javasdk.EntityId;
import com.akkaserverless.javasdk.eventsourcedentity.CommandContext;
import com.akkaserverless.javasdk.eventsourcedentity.CommandHandler;
import com.akkaserverless.javasdk.eventsourcedentity.EventHandler;
import com.akkaserverless.javasdk.eventsourcedentity.EventSourcedEntity;
import com.google.common.collect.Sets;
import com.google.protobuf.Empty;
import common.domain.BotDomainEntity;
import common.type.DecimalTransformer;

import java.math.BigDecimal;
import java.util.Collection;
import java.util.Set;
import java.util.stream.Collectors;

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

    KrakenAPIClient client = new KrakenAPIClient(BotMain.httpApiClientFactory);

    public static final String BOT_AND_KRAKEN_IDS_CANNOT_BE_EMPTY = "Bot and Kraken Account Ids cannot be empty";
    public static final String KRAKEN_ACCOUNT_IS_ALREADY_ASSOCIATED = "Bot Account is already associated with this Kraken Account";
    public static final String KRAKEN_ACCOUNT_IS_ALREADY_DISSOCIATED = "Bot Account is already dissociated from this Kraken Account";
    public static final String KRAKEN_API_KEY_SHORTEN_THAN_10 = "API Key must contain at least 10 characters";
    public static final String KRAKEN_API_SECRET_SHORTEN_THAN_10 = "API Secret must contain at least 10 characters";

    @SuppressWarnings({"FieldCanBeLocal", "unused", "FieldMayBeFinal"})
    private String krakenAccountId;

    private String botAccountId;

    private boolean isAssociated = false;
    private boolean isDissociated = false;

    private String apiKey = "";
    private String apiSecret = "";

    private final Set<KrakenAccountStateBalanceForAsset> assets = Sets.newHashSet();

    private Option<BigDecimal> costBasis;
    private Option<BigDecimal> equity;
    private Option<BigDecimal> equivalentBalance;
    private Option<BigDecimal> marginAmount;
    private Option<BigDecimal> freeMargin;
    private Option<BigDecimal> marginLevel;
    private Option<BigDecimal> unrealizedNetProfitLoss;
    private Option<BigDecimal> tradeBalance;
    private Option<BigDecimal> floatingValuation;
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
    @SuppressWarnings({"ThrowableNotThrown", "UnusedReturnValue"})
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
    public TestAPIKeysResponse testAPIKeys(@SuppressWarnings("unused") TestAPIKeysCommand testAPIKeysCommand, @SuppressWarnings("unused") CommandContext ctx) {

        // TYPED API
        try {
            // Execute one call to verify it can successfully connect.
            client.getAccountBalance();
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

    }


    /**
     * This is the command handler for adding an account as defined in protobuf.
     *
     * @param updateBalanceCommand the command message from protobuf
     * @param ctx                    the application context
     * @return Empty (unused)
     */
    @SuppressWarnings("ThrowableNotThrown")
    @CommandHandler
    public UpdateBalanceResponse updateBalance(@SuppressWarnings("unused") UpdateBalanceCommand updateBalanceCommand, CommandContext ctx) {

        AccountBalanceResult accountBalanceResult;
        TradeBalanceResult tradeBalanceResult;
        try {
            accountBalanceResult = client.getAccountBalance();
            tradeBalanceResult = client.getTradeBalance();
        } catch (KrakenApiException e) {
            ctx.fail(e.getMessage());
            return UpdateBalanceResponse.getDefaultInstance();
        }

        KrakenBalanceUpdated event = KrakenBalanceUpdated.newBuilder()
                .addAllAssets(accountBalanceResult.getResult().entrySet().stream().map(e ->
                        KrakenBalanceUpdatedForAsset.newBuilder()
                                .setAsset(e.getKey())
                                .setAmount(e.getValue().toPlainString())
                                .build())
                        .collect(Collectors.toSet()))
                .setCostBasis(DecimalTransformer.safeConvertToString(Option.some(tradeBalanceResult.getResult().costBasis)))
                .setEquity(DecimalTransformer.safeConvertToString(Option.some(tradeBalanceResult.getResult().equity)))
                .setEquivalentBalance(DecimalTransformer.safeConvertToString(Option.some(tradeBalanceResult.getResult().equivalentBalance)))
                .setMarginAmount(DecimalTransformer.safeConvertToString(Option.some(tradeBalanceResult.getResult().marginAmount)))
                .setFreeMargin(DecimalTransformer.safeConvertToString(Option.some(tradeBalanceResult.getResult().freeMargin)))
                .setMarginLevel(DecimalTransformer.safeConvertToString(Option.some(tradeBalanceResult.getResult().marginLevel)))
                .setUnrealizedNetProfitLoss(DecimalTransformer.safeConvertToString(Option.some(tradeBalanceResult.getResult().unrealizedNetProfitLoss)))
                .setTradeBalance(DecimalTransformer.safeConvertToString(Option.some(tradeBalanceResult.getResult().tradeBalance)))
                .setFloatingValuation(DecimalTransformer.safeConvertToString(Option.some(tradeBalanceResult.getResult().floatingValuation)))
                .build();

        ctx.emit(event);

        return UpdateBalanceResponse.newBuilder()
                .addAllAssets(accountBalanceResult.getResult().entrySet().stream().map(e ->
                        UpdateBalanceResponseForAsset.newBuilder()
                                .setAsset(e.getKey())
                                .setAmount(e.getValue().toPlainString())
                                .build())
                        .collect(Collectors.toSet()))
                .setCostBasis(DecimalTransformer.safeConvertToString(Option.some(tradeBalanceResult.getResult().costBasis)))
                .setEquity(DecimalTransformer.safeConvertToString(Option.some(tradeBalanceResult.getResult().equity)))
                .setEquivalentBalance(DecimalTransformer.safeConvertToString(Option.some(tradeBalanceResult.getResult().equivalentBalance)))
                .setMarginAmount(DecimalTransformer.safeConvertToString(Option.some(tradeBalanceResult.getResult().marginAmount)))
                .setFreeMargin(DecimalTransformer.safeConvertToString(Option.some(tradeBalanceResult.getResult().freeMargin)))
                .setMarginLevel(DecimalTransformer.safeConvertToString(Option.some(tradeBalanceResult.getResult().marginLevel)))
                .setUnrealizedNetProfitLoss(DecimalTransformer.safeConvertToString(Option.some(tradeBalanceResult.getResult().unrealizedNetProfitLoss)))
                .setTradeBalance(DecimalTransformer.safeConvertToString(Option.some(tradeBalanceResult.getResult().tradeBalance)))
                .setFloatingValuation(DecimalTransformer.safeConvertToString(Option.some(tradeBalanceResult.getResult().floatingValuation)))
                .build();

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
    @SuppressWarnings("unused")
    @EventHandler
    public void apiKeysAssigned(@SuppressWarnings("unused") KrakenAPIKeysAssigned krakenAPIKeysAssigned) {
        this.apiKey = krakenAPIKeysAssigned.getApiKey();
        this.apiSecret = krakenAPIKeysAssigned.getApiSecret();

        // Update client with a new one, with the specific keys.

        KrakenAPIClient client = new KrakenAPIClient(this.apiKey, this.apiSecret, BotMain.httpApiClientFactory);

    }

    /**
     * This is the event handler for updating account's Balance.
     * It is here we update current state due to successful storage to the Event Log.
     *
     * @param krakenBalanceUpdated the event previously emitted in the command handler, now safely stored.
     */
    @SuppressWarnings("unused")
    @EventHandler
    public void balanceUpdated(KrakenBalanceUpdated krakenBalanceUpdated) {

        updateBalanceDetails(krakenBalanceUpdated.getCostBasis(), krakenBalanceUpdated.getEquity(), krakenBalanceUpdated.getEquivalentBalance(), krakenBalanceUpdated.getMarginAmount(), krakenBalanceUpdated.getFreeMargin(), krakenBalanceUpdated.getMarginLevel(), krakenBalanceUpdated.getUnrealizedNetProfitLoss(), krakenBalanceUpdated.getTradeBalance(), krakenBalanceUpdated.getFloatingValuation(), krakenBalanceUpdated.getAssetsList().stream().map(e ->
                KrakenAccountStateBalanceForAsset.newBuilder()
                        .setAsset(e.getAsset())
                        .setAmount(e.getAmount())
                        .build()
        ).collect(Collectors.toSet()));
    }

    private void updateBalanceDetails(String costBasis, String equity, String equivalentBalance, String marginAmount, String freeMargin, String marginLevel, String unrealizedNetProfitLoss, String tradeBalance, String floatingValuation, final Collection<KrakenAccountStateBalanceForAsset> assets) {
        this.assets.addAll(assets);
        this.costBasis = DecimalTransformer.safeConvertFromString(costBasis);
        this.equity = DecimalTransformer.safeConvertFromString(equity);
        this.equivalentBalance = DecimalTransformer.safeConvertFromString(equivalentBalance);
        this.marginAmount = DecimalTransformer.safeConvertFromString(marginAmount);
        this.freeMargin = DecimalTransformer.safeConvertFromString(freeMargin);
        this.marginLevel = DecimalTransformer.safeConvertFromString(marginLevel);
        this.unrealizedNetProfitLoss = DecimalTransformer.safeConvertFromString(unrealizedNetProfitLoss);
        this.tradeBalance = DecimalTransformer.safeConvertFromString(tradeBalance);
        this.floatingValuation = DecimalTransformer.safeConvertFromString(floatingValuation);
    }

    // SNAPSHOTS
    @Override
    public KrakenAccountState snapshot() {
        return KrakenAccountState.newBuilder()
                .setBotAccountId(this.botAccountId)
                .setApiKey(this.apiKey)
                .setApiSecret(this.apiSecret)
                .addAllAssets(assets)
                .setCostBasis(DecimalTransformer.safeConvertToString(this.costBasis))
                .setEquity(DecimalTransformer.safeConvertToString(this.equity))
                .setEquivalentBalance(DecimalTransformer.safeConvertToString(this.equivalentBalance))
                .setMarginAmount(DecimalTransformer.safeConvertToString(this.marginAmount))
                .setFreeMargin(DecimalTransformer.safeConvertToString(this.freeMargin))
                .setMarginLevel(DecimalTransformer.safeConvertToString(this.marginLevel))
                .setUnrealizedNetProfitLoss(DecimalTransformer.safeConvertToString(this.unrealizedNetProfitLoss))
                .setTradeBalance(DecimalTransformer.safeConvertToString(this.tradeBalance))
                .setFloatingValuation(DecimalTransformer.safeConvertToString(this.floatingValuation))
                .build();
    }

    @Override
    public void handleSnapshot(KrakenAccountState state) {
        this.botAccountId = state.getBotAccountId();
        this.apiKey = state.getApiKey();
        this.apiSecret = state.getApiSecret();

        updateBalanceDetails(state.getCostBasis(), state.getEquity(), state.getEquivalentBalance(), state.getMarginAmount(), state.getFreeMargin(), state.getMarginLevel(), state.getUnrealizedNetProfitLoss(), state.getTradeBalance(), state.getFloatingValuation(), state.getAssetsList());

    }

}
