package com.thoainguyen.client;

import com.github.tomakehurst.wiremock.client.WireMock;
import com.thoainguyen.Application;
import com.thoainguyen.service.VerificationUserService;
import com.thoainguyen.util.ApplicationUtil;
import java.util.concurrent.CompletableFuture;
import org.junit.jupiter.api.Assertions;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.context.properties.EnableConfigurationProperties;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.cloud.contract.wiremock.AutoConfigureWireMock;
import org.springframework.test.context.ContextConfiguration;
import org.springframework.test.context.junit.jupiter.SpringExtension;

@SpringBootTest
@EnableConfigurationProperties
@ExtendWith(SpringExtension.class)
@ContextConfiguration(classes = {Application.class})
@AutoConfigureWireMock(port = 8082)
public class VerificationServiceTest {

  @Autowired
  private VerificationUserService verificationUserService;

  @Test
  public void fallBackWillBeTriggered_when_kycApiIsSlow_and_tooManyRequests() throws InterruptedException {
    WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/kyc/thoai"))
      .willReturn(WireMock.aResponse()
        .withBody(ApplicationUtil.SUCCESS_MESSAGE)
        .withFixedDelay(3000)
        .withStatus(200)));

    for (int i = 1; i < 6; i++) {
      simulateApiRequest();
    }

    Thread.sleep(1000);
    String response = verificationUserService.verify("thoai");
    Assertions.assertEquals(response, ApplicationUtil.FALLBACK_MESSAGE);
  }

  @Test
  public void fallBackWillNotBeTriggered_when_kycApiIsSlow_but_notTooManyRequests() throws InterruptedException {
    WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/kyc/thoai"))
      .willReturn(WireMock.aResponse()
        .withBody(ApplicationUtil.SUCCESS_MESSAGE)
        .withFixedDelay(3000)
        .withStatus(200)));

    for (int i = 1; i < 2; i++) {
      simulateApiRequest();
    }

    Thread.sleep(1000);
    String response = verificationUserService.verify("thoai");
    Assertions.assertEquals(response, ApplicationUtil.SUCCESS_MESSAGE);
  }

  @Test
  public void fallBackWillNotBeTriggered_when_tooManyRequests_but_kycApiIsNotSlow() throws InterruptedException {
    WireMock.stubFor(WireMock.post(WireMock.urlPathEqualTo("/kyc/thoai"))
      .willReturn(WireMock.aResponse()
        .withBody(ApplicationUtil.SUCCESS_MESSAGE)
        .withStatus(200)));

    for (int i = 1; i < 8; i++) {
      simulateApiRequest();
    }

    Thread.sleep(1000);
    String response = verificationUserService.verify("thoai");
    Assertions.assertEquals(response, ApplicationUtil.SUCCESS_MESSAGE);
  }

  private void simulateApiRequest() {
    CompletableFuture.supplyAsync(() -> verificationUserService.verify("thoai"));
  }
}
