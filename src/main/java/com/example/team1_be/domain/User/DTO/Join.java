package com.example.team1_be.domain.User.DTO;

import lombok.AllArgsConstructor;
import lombok.Builder;
import lombok.Getter;
import lombok.NoArgsConstructor;

import javax.validation.constraints.NotBlank;

public class Join {

    @Getter
    @AllArgsConstructor
    @NoArgsConstructor
    @Builder
    public static class Request {

        private Long kakaoId;

        private String userName;

        private Boolean isAdmin;
    }

    @Getter
    public static class Response {
        private Boolean isAdmin;

        public Response(Boolean isAdmin) {
            this.isAdmin = isAdmin;
        }

    }
}
