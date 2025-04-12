package com.tredbase.payment.service.user;

import com.tredbase.payment.dto.LoginDto;
import com.tredbase.payment.response.BaseResponse;

public interface UserInterface {
    BaseResponse accountLogin(LoginDto loginDto);
}
