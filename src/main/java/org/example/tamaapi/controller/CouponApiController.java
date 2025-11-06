package org.example.tamaapi.controller;

import lombok.RequiredArgsConstructor;
import org.example.tamaapi.common.auth.jwt.TokenProvider;
import org.example.tamaapi.command.MemberAddressRepository;
import org.example.tamaapi.command.MemberRepository;
import org.example.tamaapi.service.CacheService;
import org.example.tamaapi.service.MemberService;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.web.bind.annotation.*;

@RestController
@RequiredArgsConstructor
public class CouponApiController {

}
