package bot.exchange.kraken.access.untyped;

import org.junit.Test;

public class KrakenAPIManyTests {

    @Test
    public void checkAPIKeys()  {

        // given
/*

        KrakenApi api = new KrakenApi();
        api.setKey(apiPublicKey);
        api.setSecret(apiSecret);

        String response;
        Map<String, String> input = new HashMap<>();

        input.put("pair", "XBTEUR");
        response = api.queryPublic(KrakenApi.Method.TICKER, input);
        System.out.println(response);

        input.clear();
        input.put("pair", "XBTEUR,XBTLTC");
        response = api.queryPublic(KrakenApi.Method.ASSET_PAIRS, input);
        System.out.println(response);

        input.clear();
        input.put("asset", "ZEUR");
        response = api.queryPrivate(KrakenApi.Method.BALANCE, input);
        System.out.println(response);

        /* KrakenHttpClient not implemented on reactive crypto API */
       /* ()     ExchangeClientFactory.http(ExchangeVendor.KRAKEN)
                .privateApi(apiPublicKey, apiSecret)
                .account()
                .balance()
                .subscribe(new Subscriber<Balance>() {
                    @Override
                    public void onSubscribe(Subscription subscription) {

                    }

                    @Override
                    public void onNext(Balance balance) {
                        System.out.println("Response=" + balance.toString());
                        Assert.assertEquals("skjas", balance.toString());
                    }

                    @Override
                    public void onError(Throwable throwable) {

                    }

                    @Override
                    public void onComplete() {

                    }
                });


        OrderPlaceResult orderPlaceResult = ExchangeClientFactory.http(ExchangeVendor.KRAKEN)
                .privateApi(apiPublicKey, apiSecret)
                .order()
                .limitOrder(
                        new CurrencyPair(Currency.BTC, Currency.KRW),
                        TradeSideType.BUY,
                        BigDecimal.valueOf(10000000.0),
                        BigDecimal.valueOf(10.0)
                )
                .block();
*/
        // log.info("{}", orderPlaceResult);

/*        int timeout = 10;
        boolean checkCertificate = true;
        KrakenAccountCalls krakenAccountCalls = new KrakenAccountCalls(
                "https://api.kraken.com",
                apiPublicKey,
                apiSecret,
                timeout,
                checkCertificate);


        // when
        final String accountResponse = krakenAccountCalls.getAccountBalance();

        // then
        System.out.println("Response=" + accountResponse);
        Assert.assertEquals("skjas", accountResponse);
*/





         /*
        HttpClient.create()
                .post()
                .uri("")
                .sendForm((req, form) -> form.multipart(true)
                        .attr("API", "liuyi")
                        .attr("password", "password"))
                .responseSingle((res, content) -> content)
                .map(byteBuf -> byteBuf.toString(CharsetUtil.UTF_8))
                .block();


        // https://github.com/elastic/apm-agent-java/pull/1307/files/2cf3d0843fa59a08d23abba00ae542ab9f8ec4b3
        HttpClient client = HttpClient.newBuilder().followRedirects(HttpClient.Redirect.NORMAL).build();
        HttpRequest request = HttpRequest.newBuilder()
                .uri(URI.create("path"))
                .build();
        client.sendAsync(request, HttpResponse.BodyHandlers.ofString()).get();


        final CompletionStage<HttpResponse> stage =
                Http.get(BotMain.botAkkaServerless.createRunner().system())
                .singleRequest(HttpRequest.GET("http://dummy.restapiexample.com/api/v1/employee/"));

        try {
            stage.whenComplete(
                    new BiConsumer<HttpResponse, Throwable>() {
                        @Override
                        public void accept(HttpResponse httpResponse, Throwable throwable) {
                            System.out.println(httpResponse.status());
                        }
                    }).toCompletableFuture().get().entity().getDataBytes().runForeach(param -> {}, materializer);
        } catch (final Exception ignore) {
        }



        KrakenAccountCalls.init(
                BotMain.botAkkaServerless.,
                BotMain.botAkkaServerless.createRunner().system());


        HttpRequest
                .GET("/example.com/some")
                .withHeaders(
                        RawHeader.create("X-CSRF-TOKEN", ""))
                .withEntity(HttpEntity.CloseDelimited)
                .withUri("")
                .addCredentials(
                        HttpCredentials.createBasicHttpCredentials("", ""));

        Http.get(BotMain.botAkkaServerless.createRunner().system())
                .connectionTo("127.0.0.1")
                .toPort(8443)
                .http2().addAttributes(


        );


        */
/*
        URL url = new URL("https://api.lifx.com/v1/lights/" + testAPIKeysCommand.getDeviceId() + "/toggle");
        HttpsURLConnection conn = (HttpsURLConnection) url.openConnection();
        conn.set
        conn.setRequestProperty("Authorization","Bearer " + event.getAccessToken());
        conn.setRequestProperty("Content-Type","application/json");
        conn.setRequestMethod("POST");

        conn.setDoOutput(true);
        DataOutputStream wr = new DataOutputStream(conn.getOutputStream());
        wr.flush();
        wr.close();

        conn.getResponseCode();

        return Empty.getDefaultInstance();

        OrderPlaceResult orderPlaceResult = ExchangeClientFactory.http(ExchangeVendor.BINANCE)
                .privateApi("accessKey", "secretKey")
                .order()
                .limitOrder(
                        CurrencyPair(Currency.BTC, Currency.KRW),
                        TradeSideType.BUY,
                        BigDecimal.valueOf(10000000.0),
                        BigDecimal.valueOf(10.0)
                )
                .block();
*/
    }

}
