package org.zerock.testapiserver.controller;

import lombok.RequiredArgsConstructor;
import lombok.extern.log4j.Log4j2;
import org.springframework.http.ResponseEntity;
import org.springframework.web.bind.annotation.PostMapping;
import org.springframework.web.bind.annotation.RequestBody;
import org.springframework.web.bind.annotation.RequestMapping;
import org.springframework.web.bind.annotation.RestController;
import org.zerock.testapiserver.dto.MemberDTO;
import org.zerock.testapiserver.service.MemberService;
import org.zerock.testapiserver.util.CustomRegisterException;

@RestController
@RequiredArgsConstructor
@RequestMapping("/api/member")
@Log4j2
public class MemberController {

    private final MemberService memberService;


    //회원가입
    @PostMapping("/register")
    public ResponseEntity<String> register(@RequestBody MemberDTO memberDTO) {
        try {
            memberService.registerMember(memberDTO);
            return ResponseEntity.ok("회원가입이 완료되었습니다.");
        } catch (CustomRegisterException e) {
            return ResponseEntity.badRequest().body(e.getMessage());
        }
    }

}
