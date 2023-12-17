# Introduction

Bulkhead pattern improves system resilience by creating separate, isolated compartments for different parts of a system. 
It’s inspired by the bulkheads on ships that prevent the entire vessel from flooding if one section is breached.

Within this project - an E-commerce system using an external KYC API to verify newly registration users, 
we will apply Bulkhead to the **VerificationService** using **Resilience4j** library to 
limit only a few concurrent requests to the registration feature when the KYC API is very slow. 
We will also create an integration test to verify whether bulkhead works as our expectiation.
  
## Tech stack

JDK 11, Gradle, Spring boot, Spring Feign, Resilience4j, Wiremock

We will use Spring Feign to make RESTful requests to the KYC service and enable bulkhead to the **VerificationService**. The source
code is really straightforward, the **VerificationServiceClient** makes request to the KYC API which is consumed by the
**VerificationUserService** in order to return the verification result to customers via **VerificationUserEndpoint**.

Bulkhead is applied to the **VerificationService* via the below configurations:
```shell
resilience4j.bulkhead:
  instances:
    verificationUserService:
      maxConcurrentCalls: 5
      maxWaitDuration: 2000ms
```
We will write an integration test (**VerificationServiceTest**) to verify how Bulkhead works by a few scenarios.

## How to get started?
We can start this project via below gradle commands:
```shell
gradle build
gradle bootRun
```
To run the integration test
```shell
gradle build
gradle test
```

