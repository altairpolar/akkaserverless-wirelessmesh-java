package bot.exchange.kraken.position;

import bot.BotAkkaServerless;
import bot.account.BotAccountServiceClient;
import bot.exchange.kraken.account.KrakenAccountIntegrationHelper;
import bot.exchange.kraken.account.KrakenAccountServiceClient;
import bot.exchange.kraken.account.KrakenPositionAPI;
import bot.exchange.kraken.account.KrakenPositionServiceClient;
import com.akkaserverless.javasdk.testkit.junit.AkkaServerlessTestkitResource;
import org.junit.Assert;
import org.junit.ClassRule;
import org.junit.Rule;
import org.junit.Test;
import org.junit.rules.ExpectedException;

import java.util.UUID;
import java.util.concurrent.ExecutionException;

public class KrakenPositionServiceIntegrationTest {

    private final KrakenPositionServiceClient krakenPositionServiceClient;

    private final KrakenAccountIntegrationHelper krakenAccountIntegrationHelper;

    @ClassRule
    public static final AkkaServerlessTestkitResource testkit = new AkkaServerlessTestkitResource(
            BotAkkaServerless.botAkkaServerless);

    @Rule
    public ExpectedException exceptionRule = ExpectedException.none();

    public KrakenPositionServiceIntegrationTest() {
        BotAccountServiceClient botAccountServiceClient = BotAccountServiceClient.create(testkit.getGrpcClientSettings(), testkit.getActorSystem());
        KrakenAccountServiceClient krakenAccountServiceClient = KrakenAccountServiceClient.create(testkit.getGrpcClientSettings(), testkit.getActorSystem());
        this.krakenPositionServiceClient = KrakenPositionServiceClient.create(testkit.getGrpcClientSettings(), testkit.getActorSystem());

        krakenAccountIntegrationHelper = new KrakenAccountIntegrationHelper(botAccountServiceClient, krakenAccountServiceClient);
    }

    @Test
    public void createLongPosition() throws ExecutionException, InterruptedException {

        // given
        String positionId = UUID.randomUUID().toString();

        // when
        createLongPositionWithId(positionId);

        // then

        // All Ok. No exception.
        Assert.assertTrue(true);

    }

    @Test
    public void createLongPosition_failsIfExchangeAccountDoesNotExist()  {

        // given
        String positionId = UUID.randomUUID().toString();
        String krakenAccountId = UUID.randomUUID().toString();

        // DO NOT create the account.
        // krakenAccountIntegrationHelper.createAssociatedKrakenAccount(krakenAccountId);

        // when
        exceptionRule.expect(ExecutionException.class);
        exceptionRule.expectMessage(KrakenPositionEntity.INVALID_KRAKEN_ACCOUNT);
        krakenPositionServiceClient.createLongPosition(KrakenPositionAPI.CreateLongPositionCommand.newBuilder()
                .setPositionId(positionId)
                .setKrakenAccountId(krakenAccountId)
                .build());

        // then
        Assert.fail("Should trow Exception before");

    }

    @Test
    public void createLongPosition_failsIfExchangeAccountDisabled()  {


    }

    @Test
    public void getPosition()  {

    }

    @Test
    public void getPosition_failsIfNotExists()  {

    }

    @Test
    public void getPosition_withHistory()  {

    }

    @Test
    public void splitPosition()  {

    }

    @Test
    public void mergePosition()  {

    }

    @Test
    public void mergePosition_failsIfIncompatiblePositions()  {

    }

    @Test
    public void holdPosition()  {

    }

    @Test
    public void holdPosition_failsIfAlreadyHolded()  {

    }

    @Test
    public void releasePosition()  {

    }

    @Test
    public void releasePosition_failsIfNotHolded()  {

    }

    @Test
    public void dcaPosition()  {

    }

    @Test
    public void dcaPosition_updatesIfAlreadyDCAed()  {

    }

    @Test
    public void shortPosition()  {

    }

    @Test
    public void shortPosition_sellsAsWasLong()  {

    }

    @Test
    public void shortPosition_failsIfAlreadyShorted()  {

    }

    @Test
    public void cancelPosition()  {

    }

    @Test
    public void cancelPosition_failsIfAlreadyCancelled()  {

    }

    @Test
    public void closePosition()  {

    }

    @Test
    public void closePosition_failsIfAlreadyClosed()  {

    }

    @Test
    public void closePosition_sellsIfLong()  {

    }

    @Test
    public void closePosition_buysIfShort()  {

    }

    private void createLongPositionWithId(String positionId) throws ExecutionException, InterruptedException {
        String krakenAccountId = UUID.randomUUID().toString();
        krakenAccountIntegrationHelper.createAssociatedKrakenAccount(krakenAccountId);

        krakenPositionServiceClient.createLongPosition(KrakenPositionAPI.CreateLongPositionCommand.newBuilder()
                .setPositionId(positionId)
                .setKrakenAccountId(krakenAccountId)
                .build());
    }

}
