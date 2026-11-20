package com.paymentapilite.integrations;



public record AuthorizationResponse(
        String status,
        AuthorizationData data
) {

    public record AuthorizationData (Boolean authorization) {
    }
}
