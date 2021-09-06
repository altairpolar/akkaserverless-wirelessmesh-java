package bot.exchange.kraken.account;

import bot.BotAkkaServerless;
import bot.BotMain;
import bot.account.BotAccountAPI;
import bot.account.BotAccountServiceClient;
import bot.exchange.access.BotExchangeFactory;
import bot.exchange.kraken.access.BotKrakenExchangeFactory;
import bot.exchange.kraken.access.typed.HttpApiClient;
import bot.exchange.kraken.access.typed.HttpApiClientFactory;
import bot.exchange.kraken.access.typed.KrakenAPIClient;
import bot.exchange.kraken.access.typed.KrakenApiException;
import bot.exchange.kraken.access.typed.KrakenApiMethod;
import bot.exchange.kraken.access.typed.result.AccountBalanceResult;
import bot.exchange.kraken.access.typed.result.Result;
import bot.exchange.kraken.access.typed.result.TradeBalanceResult;
import bot.exchange.kraken.access.typed.utils.StreamUtils;
import bot.exchange.kraken.account.KrakenAccountAPI.*;
import com.akkaserverless.javasdk.testkit.junit.AkkaServerlessTestkitResource;
import com.fasterxml.jackson.databind.ObjectMapper;
import com.google.common.collect.ImmutableMap;
import com.google.common.collect.Sets;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Ignore;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;
import org.knowm.xchange.Exchange;
import org.knowm.xchange.ExchangeFactory;
import org.knowm.xchange.ExchangeSpecification;
import org.knowm.xchange.dto.account.AccountInfo;
import org.knowm.xchange.dto.account.Wallet;
import org.knowm.xchange.kraken.Kraken;
import org.knowm.xchange.kraken.KrakenExchange;
import org.knowm.xchange.kraken.dto.account.KrakenTradeBalanceInfo;
import org.knowm.xchange.kraken.service.KrakenAccountServiceRaw;
import org.knowm.xchange.kraken.service.KrakenTradeServiceRaw;
import org.knowm.xchange.service.account.AccountService;
import org.mockito.MockedStatic;
import org.mockito.Mockito;
import org.mockito.stubbing.Answer;

import java.io.IOException;
import java.math.BigDecimal;
import java.util.Arrays;
import java.util.Map;
import java.util.UUID;
import java.util.concurrent.ExecutionException;

import static org.mockito.Mockito.mock;
import static org.mockito.Mockito.mockConstruction;
import static org.mockito.Mockito.when;

public class KrakenAccountServiceIntegrationTest {

    private final BotAccountServiceClient botAccountServiceClient;
    private final KrakenAccountServiceClient krakenAccountServiceClient;

    private KrakenAccountIntegrationHelper krakenAccountIntegrationHelper;

    @ClassRule
    public static final AkkaServerlessTestkitResource testkit = new AkkaServerlessTestkitResource(
            BotAkkaServerless.botAkkaServerless);

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    public KrakenAccountServiceIntegrationTest() {
        botAccountServiceClient = BotAccountServiceClient.create(testkit.getGrpcClientSettings(), testkit.getActorSystem());
        krakenAccountServiceClient = KrakenAccountServiceClient.create(testkit.getGrpcClientSettings(), testkit.getActorSystem());

        krakenAccountIntegrationHelper = new KrakenAccountIntegrationHelper(botAccountServiceClient, krakenAccountServiceClient);
    }

    @Test
    public void associateAccount() throws ExecutionException, InterruptedException {

        // given
        String botAccountId = UUID.randomUUID().toString();
        String krakenAccountId = UUID.randomUUID().toString();
        String email = "me@you.com";

        // when
        krakenAccountIntegrationHelper.createAssociatedKrakenAccount(botAccountId, krakenAccountId, email);

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
        String krakenAccountId = UUID.randomUUID().toString();
        krakenAccountIntegrationHelper.createAssociatedKrakenAccount(krakenAccountId);

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
        String krakenAccountId = UUID.randomUUID().toString();
        krakenAccountIntegrationHelper.createAssociatedKrakenAccount(krakenAccountId);

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
        String krakenAccountId = UUID.randomUUID().toString();
        krakenAccountIntegrationHelper.createAssociatedKrakenAccount(krakenAccountId);

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
    public void testAPIKeys() throws ExecutionException, InterruptedException, IOException {

        // mocks
        KrakenExchange exchange = mock(KrakenExchange.class);
        try (MockedStatic<BotExchangeFactory> botExchangeFactoryMockedStatic = Mockito.mockStatic(BotExchangeFactory.class)) {
            botExchangeFactoryMockedStatic.when(() -> BotExchangeFactory.createExchange(KrakenExchange.class))
                    .thenReturn(exchange);
        }
        KrakenAccountServiceRaw krakenAccountServiceRaw = mock(KrakenAccountServiceRaw.class);
        try (MockedStatic<BotKrakenExchangeFactory> botKrakenExchangeFactoryMockedStatic = Mockito.mockStatic(BotKrakenExchangeFactory.class)) {
            botKrakenExchangeFactoryMockedStatic.when(() -> BotKrakenExchangeFactory.createKrakenAccountServiceRaw(exchange))
                    .thenReturn(krakenAccountServiceRaw);
        }
        AccountService accountService = mock(AccountService.class);
        when(exchange.getAccountService()).thenReturn(accountService);
        Wallet wallet = new Wallet("id", "name", Sets.newHashSet(), Sets.newHashSet(), BigDecimal.valueOf(1), BigDecimal.valueOf(2));
        when(accountService.getAccountInfo()).thenReturn(new AccountInfo(wallet));
        KrakenTradeBalanceInfo krakenTradeBalanceInfo = new KrakenTradeBalanceInfo(
                BigDecimal.valueOf(1), BigDecimal.valueOf(2), BigDecimal.valueOf(3), BigDecimal.valueOf(4), BigDecimal.valueOf(5), BigDecimal.valueOf(6), BigDecimal.valueOf(7), BigDecimal.valueOf(8));
        when(krakenAccountServiceRaw.getKrakenTradeBalance()).thenReturn(krakenTradeBalanceInfo);

        // given
        String krakenAccountId = UUID.randomUUID().toString();
        krakenAccountIntegrationHelper.createAssociatedKrakenAccount(krakenAccountId);

        String apiPublicKey = "dmDSFDSdskjds329dsk2393320329032";
        String apiSecretKey = "jksfnsakfj238dslfweo85u32wljasfs";

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

    private void mockAPIMethod(
            @SuppressWarnings("rawtypes") Map<KrakenApiMethod, ? extends Result> methods) {
        BotMain.httpApiClientFactory = mock(HttpApiClientFactory.class);
        methods.forEach((method,result) ->
        {
            try {
                @SuppressWarnings("rawtypes") HttpApiClient mockClient = mock(HttpApiClient.class);
                //noinspection unchecked
                when(BotMain.httpApiClientFactory.getHttpApiClient(null, null, method))
                        .thenReturn(mockClient);
                //noinspection unchecked
                when(mockClient.callPrivate(KrakenAPIClient.BASE_URL, method, result.getClass())).thenReturn(result);

            } catch (KrakenApiException e) {
                e.printStackTrace();
            }
        });
    }

    @Test
    public void testUpdateBalance() throws ExecutionException, InterruptedException, IOException {

        // mocks
        mockAPIMethod(
                ImmutableMap.of(
                KrakenApiMethod.ACCOUNT_BALANCE
                , new ObjectMapper().readValue(
                        StreamUtils.getResourceAsString(this.getClass(), "json/account_balance.mock.json")
                        , AccountBalanceResult.class),
                KrakenApiMethod.TRADE_BALANCE
                , new ObjectMapper().readValue(
                        StreamUtils.getResourceAsString(this.getClass(), "json/trade_balance.mock.json")
                        , TradeBalanceResult.class)));

        String krakenAccountId = UUID.randomUUID().toString();
        krakenAccountIntegrationHelper.createAssociatedKrakenAccountWithAPIKeys(krakenAccountId);

        // when
        UpdateBalanceResponse updateBalanceResponse = krakenAccountServiceClient.updateBalance(UpdateBalanceCommand.newBuilder()
                .setKrakenAccountId(krakenAccountId)
                .build())
                .toCompletableFuture().get();

        // then
        Assert.assertEquals("0.0000", updateBalanceResponse.getCostBasis());
        Assert.assertEquals("230.6645", updateBalanceResponse.getEquity());
        Assert.assertEquals("260.1645", updateBalanceResponse.getEquivalentBalance());
        Assert.assertEquals("0.0000", updateBalanceResponse.getFloatingValuation());
        Assert.assertEquals("230.6645", updateBalanceResponse.getFreeMargin());
        Assert.assertEquals("0.0000", updateBalanceResponse.getMarginAmount());
        Assert.assertEquals("", updateBalanceResponse.getMarginLevel());
        Assert.assertEquals("230.6645", updateBalanceResponse.getTradeBalance());
        Assert.assertEquals("0.0000", updateBalanceResponse.getUnrealizedNetProfitLoss());

    }
}
