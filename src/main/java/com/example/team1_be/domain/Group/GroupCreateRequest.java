package com.example.team1_be.domain.Group;

import lombok.AllArgsConstructor;
import lombok.Getter;
import lombok.NoArgsConstructor;

/**
 * 그룹 생성하기 Request DTO
 */
@Getter
@AllArgsConstructor
@NoArgsConstructor
public class GroupCreateRequest {
    private String marketName;      // 사업자명
    private String marketNumber;    // 사업자번호
    private String phoneNumber;     // 전화번호
    private String mainAddress;     // 주소
    private String detailAddress;   // 상세주소
}
