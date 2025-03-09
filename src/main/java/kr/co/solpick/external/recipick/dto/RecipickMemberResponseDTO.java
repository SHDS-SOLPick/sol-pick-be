package kr.co.solpick.external.recipick.dto;

import lombok.Data;

@Data
public class RecipickMemberResponseDTO {
    private int memberId;
    private String email;
    private String name;
    private String nickname;
    private String gender;
    private String birth;
}