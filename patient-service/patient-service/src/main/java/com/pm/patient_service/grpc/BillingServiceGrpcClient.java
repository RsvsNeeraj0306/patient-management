package com.pm.patient_service.grpc;

import org.springframework.beans.factory.annotation.Value;
import org.springframework.stereotype.Service;
import io.grpc.ManagedChannel;
import io.grpc.ManagedChannelBuilder;
import billing.BillingServiceGrpc;
import billing.BillingRequest;
import billing.BillingResponse;

@Service
public class BillingServiceGrpcClient {

    private final BillingServiceGrpc.BillingServiceBlockingStub billingServiceStub;

    public BillingServiceGrpcClient(
        @Value("${billing.service.address:localhost}") String serverAddress,
        @Value("${billing.service.port:9091}") int serverPort) {
        
        ManagedChannel channel = ManagedChannelBuilder.forAddress(serverAddress, serverPort)
                .usePlaintext()
                .build();
                
        this.billingServiceStub = BillingServiceGrpc.newBlockingStub(channel);
    }

    public BillingResponse createBillingAccount(String patientId, String name, String email) {
        BillingRequest request = BillingRequest.newBuilder()
                .setPatientId(patientId)
                .setName(name)
                .setEmail(email)
                .build();

        return billingServiceStub.createBillingAccount(request);
    }
}