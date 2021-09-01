package test;


import akka.pattern.ReplyWith;
import bot.account.BotAccountAPI;
import bot.exchange.kraken.account.KrakenPositionAPI;
import bot.exchange.kraken.account.KrakenPositionDomain;
import com.akkaserverless.javasdk.Effect;
import com.akkaserverless.javasdk.Reply;
import com.akkaserverless.javasdk.eventsourcedentity.CommandContext;
import com.akkaserverless.javasdk.eventsourcedentity.CommandHandler;
import com.akkaserverless.javasdk.eventsourcedentity.EventHandler;
import com.akkaserverless.javasdk.eventsourcedentity.EventSourcedEntity;
import com.akkaserverless.protocol.Component;
import com.customer.CustomerAPI;
import com.customer.InvoiceDomain;
import com.invoice.InvoiceAPI;

@EventSourcedEntity(entityType = "invoice")
public class InvoiceEntity {

    private String invoiceId;
    private String customerId;

    @CommandHandler
    public void createInvoice(InvoiceAPI.CreateInvoiceCommand createInvoiceCommand, CommandContext ctx) {

        CustomerAPI.GetCustomerResponse getCustomerResponse = CustomerAPI.GetCustomerResponse.getDefaultInstance();

        Reply.forward(ctx.serviceCallFactory().lookup("com.customer.CustomerService", "GetCustomerCommand", CustomerAPI.GetCustomerResponse.class)
                .createCall(getCustomerResponse)).addEffects(
                /* How to invoke the EventHandler?
                        Effect.of(
                        ctx.emit(
                            InvoiceDomain.InvoiceCreated.newBuilder()
                                .setCustomerId(createInvoiceCommand.getCustomerId())
                            .build()))
        */);

    }
    @EventHandler
    public void accountAssociated(@SuppressWarnings("unused")InvoiceDomain.InvoiceCreated invoiceCreated) {
        this.customerId = invoiceCreated.getCustomerId();
    }

}
