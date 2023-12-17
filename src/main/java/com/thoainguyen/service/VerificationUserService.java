package com.thoainguyen.service;

import com.thoainguyen.client.VerificationServiceClient;
import com.thoainguyen.util.ApplicationUtil;
import io.github.resilience4j.bulkhead.annotation.Bulkhead;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

@Component
public class VerificationUserService {

  private static final Logger LOGGER = LoggerFactory.getLogger(VerificationUserService.class);
  private final VerificationServiceClient verificationServiceClient;

  @Autowired
  public VerificationUserService( VerificationServiceClient verificationServiceClient) {
    this.verificationServiceClient = verificationServiceClient;
  }

  @Bulkhead(name = "verificationUserService", fallbackMethod = "verifyDefault")
  public String verify(String userName) {
    return verificationServiceClient.verify(userName);
  }

  public String verifyDefault(String userName, Throwable throwable) { return ApplicationUtil.FALLBACK_MESSAGE; }
}
