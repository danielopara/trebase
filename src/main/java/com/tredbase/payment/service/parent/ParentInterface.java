package com.tredbase.payment.service.parent;

import com.tredbase.payment.response.BaseResponse;

import java.util.UUID;

public interface ParentInterface {
    // returning all parents from the db
    BaseResponse getAllParents();

    // returns a specific parent from the db
    BaseResponse getParent(UUID id);
}
