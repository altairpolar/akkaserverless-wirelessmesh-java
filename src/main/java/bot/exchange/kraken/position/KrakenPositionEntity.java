package bot.exchange.kraken.position;

import bot.account.BotAccountAPI;
import bot.exchange.kraken.account.KrakenPositionAPI;
import bot.exchange.kraken.account.KrakenPositionDomain;
import com.akkaserverless.javasdk.Effect;
import com.akkaserverless.javasdk.Reply;
import com.akkaserverless.javasdk.ServiceCall;
import com.akkaserverless.javasdk.ServiceCallRef;
import com.akkaserverless.javasdk.eventsourcedentity.CommandContext;
import com.akkaserverless.javasdk.eventsourcedentity.CommandHandler;
import com.akkaserverless.javasdk.eventsourcedentity.EventHandler;
import com.akkaserverless.javasdk.eventsourcedentity.EventSourcedEntity;
import com.akkaserverless.javasdk.reply.ForwardReply;
import com.customer.CustomerAPI;
import com.google.protobuf.Empty;

/**
 * A Kraken Exchange Position entity.
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
public class KrakenPositionEntity {

    public static final String INVALID_KRAKEN_ACCOUNT = "Invalid Kraken Account Id. Please, specify a valid one";



    // FIELDS.
    private String positionId;

    @SuppressWarnings({"FieldCanBeLocal", "unused", "FieldMayBeFinal"})
    private String krakenAccountId;

    // COMMAND HANDLERS

    /**
     * This is the command handler for creating a Long Position as defined in protobuf.
     *
     * @param createLongPositionCommand the command message from protobuf
     * @param ctx                    the application context
     */
    @SuppressWarnings({"ThrowableNotThrown", "UnusedReturnValue"})
    @CommandHandler
    public void createLongPosition(KrakenPositionAPI.CreateLongPositionCommand createLongPositionCommand, CommandContext ctx) {

        BotAccountAPI.GetAccountResponse getAccountResponse = BotAccountAPI.GetAccountResponse.getDefaultInstance();

        Reply.forward(ctx.serviceCallFactory().lookup("bot.exchange.kraken.account.KrakenAccountService", "GetAccountCommand", BotAccountAPI.GetAccountResponse.class)
                .createCall(getAccountResponse)).addEffects(/* what to insert?*/);

        if (!getAccountResponse.getId().isEmpty()) {
            ctx.emit(KrakenPositionDomain.KrakenLongPositionOpened.newBuilder()
                    .setKrakenAccountId(createLongPositionCommand.getKrakenAccountId())
                    .setKrakenPositionId(createLongPositionCommand.getPositionId())
                    .build());
        } else {
            ctx.fail(INVALID_KRAKEN_ACCOUNT);
        }
    }

    /**
     * This is the event handler for associating a Kraken Account with a Bot Account.
     * It is here we update current state due to successful storage to the Event Log.
     *
     * @param krakenLongPositionOpened the event previously emitted in the command handler, now safely stored.
     */
    @EventHandler
    public void accountAssociated(@SuppressWarnings("unused") KrakenPositionDomain.KrakenLongPositionOpened krakenLongPositionOpened) {
        this.krakenAccountId = krakenLongPositionOpened.getKrakenAccountId();
    }

}
