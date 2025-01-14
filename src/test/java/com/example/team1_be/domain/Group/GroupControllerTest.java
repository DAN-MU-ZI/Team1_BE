package com.example.team1_be.domain.Group;

import static org.springframework.test.web.servlet.request.MockMvcRequestBuilders.*;
import static org.springframework.test.web.servlet.result.MockMvcResultHandlers.*;
import static org.springframework.test.web.servlet.result.MockMvcResultMatchers.*;

import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.autoconfigure.web.servlet.AutoConfigureMockMvc;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.http.MediaType;
import org.springframework.test.context.jdbc.Sql;
import org.springframework.test.web.servlet.MockMvc;
import org.springframework.test.web.servlet.ResultActions;

import com.example.team1_be.domain.Group.DTO.Create;
import com.example.team1_be.domain.Group.DTO.InvitationAccept;
import com.example.team1_be.util.WithMockCustomAdminUser;
import com.example.team1_be.util.WithMockCustomMemberUser;
import com.fasterxml.jackson.databind.ObjectMapper;

@SpringBootTest
@AutoConfigureMockMvc
public class GroupControllerTest {
	@Autowired
	private MockMvc mvc;
	@Autowired
	private ObjectMapper om;
	@Autowired
	private GroupRepository groupRepository;

	@DisplayName("그룹 생성하기 성공")
	@WithMockCustomAdminUser(username = "eunjin", isAdmin = "true")
	@Sql("group-create1.sql")
	@Test
	void shouldCreateGroupSuccessfully() throws Exception {
		// given
		Create.Request requestDTO = Create.Request.builder()
			.marketName("kakao")
			.marketNumber("10-31223442")
			.mainAddress("금강로 279번길 19")
			.detailAddress("ㅁㅁ건물 2층 ㅇㅇ상가")
			.build();
		String request = om.writeValueAsString(requestDTO);

		// when
		ResultActions perform = mvc.perform(post("/api/group")
			.contentType(MediaType.APPLICATION_JSON)
			.content(request));

		// then
		perform.andDo(print());
	}

	@DisplayName("그룹 생성하기 DTO 검증 실패(멤버변수 누락)")
	@WithMockCustomAdminUser
	@Sql("group-create1.sql")
	@Test
	void shouldFailToCreateGroupWhenDtoValidationFails() throws Exception {
		// given
		Create.Request requestDTO = Create.Request.builder()
			.build();
		String request = om.writeValueAsString(requestDTO);

		// when
		ResultActions perform = mvc.perform(post("/api/group")
			.contentType(MediaType.APPLICATION_JSON)
			.content(request));

		// then
		perform.andExpect(status().isBadRequest());
		perform.andDo(print());
	}

	@DisplayName("그룹 생성하기 실패(이미 가입된 그룹 존재)")
	@WithMockCustomAdminUser
	@Sql("group-create2.sql")
	@Test
	void shouldFailToCreateGroupWhenGroupAlreadyExists() throws Exception {
		// given
		Create.Request requestDTO = Create.Request.builder()
			.marketName("kakao")
			.marketNumber("10-31223442")
			.mainAddress("금강로 279번길 19")
			.detailAddress("ㅁㅁ건물 2층 ㅇㅇ상가")
			.build();
		String request = om.writeValueAsString(requestDTO);

		// when
		ResultActions perform = mvc.perform(post("/api/group")
			.contentType(MediaType.APPLICATION_JSON)
			.content(request));

		// then
		perform.andExpect(status().isBadRequest());
		perform.andDo(print());
	}

	@DisplayName("그룹 초대장 제출 성공")
	@WithMockCustomMemberUser(username = "dksgkswn", userId = "2", kakaoId = "2")
	@Sql("group-invitationAccept1.sql")
	@Test
	void shouldSuccessfullySubmitGroupInvitation() throws Exception {
		// given
		InvitationAccept.Request requestDTO = new InvitationAccept.Request("testcode1");
		String request = om.writeValueAsString(requestDTO);

		// when
		ResultActions perform = mvc.perform(post("/api/group/invitation")
			.contentType(MediaType.APPLICATION_JSON)
			.content(request));

		// then
		perform.andExpect(status().isOk());
		perform.andDo(print());
	}

	@DisplayName("그룹 초대장 제출 실패(초대장 갱신시점이 없음)")
	@WithMockCustomMemberUser(username = "dksgkswn", userId = "2", kakaoId = "2")
	@Sql("group-invitationAccept2.sql")
	@Test
	void shouldFailToSubmitGroupInvitationWhenNoRefreshDate() throws Exception {
		// given
		InvitationAccept.Request requestDTO = new InvitationAccept.Request("testcode1");
		String request = om.writeValueAsString(requestDTO);

		// when
		ResultActions perform = mvc.perform(post("/api/group/invitation")
			.contentType(MediaType.APPLICATION_JSON)
			.content(request));

		// then
		perform.andExpect(status().isForbidden());
		perform.andDo(print());
	}

	@DisplayName("그룹 초대장 제출 실패(초대장 갱신시점 만료)")
	@WithMockCustomMemberUser(username = "dksgkswn", userId = "2", kakaoId = "2")
	@Sql("group-invitationAccept3.sql")
	@Test
	void shouldFailToSubmitGroupInvitationWhenInvitationExpired() throws Exception {
		// given
		InvitationAccept.Request requestDTO = new InvitationAccept.Request("testcode1");
		String request = om.writeValueAsString(requestDTO);

		// when
		ResultActions perform = mvc.perform(post("/api/group/invitation")
			.contentType(MediaType.APPLICATION_JSON)
			.content(request));

		// then
		perform.andExpect(status().isBadRequest());
		perform.andDo(print());
	}

	@DisplayName("그룹 초대장 DTO 실패(초대내용이 없음)")
	@WithMockCustomMemberUser(username = "dksgkswn", userId = "2", kakaoId = "2")
	@Sql("group-invitationAccept1.sql")
	@Test
	void shouldFailToSubmitGroupInvitationWhenNoInvitationContent() throws Exception {
		// given
		InvitationAccept.Request requestDTO = new InvitationAccept.Request();
		String request = om.writeValueAsString(requestDTO);

		// when
		ResultActions perform = mvc.perform(post("/api/group/invitation")
			.contentType(MediaType.APPLICATION_JSON)
			.content(request));

		// then
		perform.andExpect(status().isBadRequest());
		perform.andDo(print());
	}

	@DisplayName("그룹 초대장 확인 성공")
	@WithMockCustomMemberUser(username = "dksgkswn", userId = "2", kakaoId = "2")
	@Sql("group-invitationCheck1.sql")
	@Test
	void shouldSuccessfullyCheckGroupInvitation() throws Exception {
		// given
		String invitationKey = "testcode1";

		// when
		ResultActions perform = mvc.perform(
			get(String.format("/api/group/invitation/information?invitationKey=%s", invitationKey)));

		// then
		perform.andExpect(status().isOk());
		perform.andDo(print());
	}

	@DisplayName("그룹 초대장 확인 실패(존재하지 않는 초대장)")
	@WithMockCustomMemberUser(username = "dksgkswn", userId = "2", kakaoId = "2")
	@Sql("group-invitationCheck2.sql")
	@Test
	void shouldFailToCheckGroupInvitationWhenInvitationDoesNotExist() throws Exception {
		// given
		String invitationKey = "testcode1";

		// when
		ResultActions perform = mvc.perform(
			get(String.format("/api/group/invitation/information?invitationKey=%s", invitationKey)));

		// then
		perform.andExpect(status().isBadRequest());
		perform.andDo(print());
	}

	@DisplayName("그룹 초대장 확인 실패(초대장 만료)")
	@WithMockCustomMemberUser(username = "dksgkswn", userId = "2", kakaoId = "2")
	@Sql("group-invitationCheck3.sql")
	@Test
	void shouldFailToCheckGroupInvitationWhenInvitationExpired() throws Exception {
		// given
		String invitationKey = "testcode1";

		// when
		ResultActions perform = mvc.perform(
			get(String.format("/api/group/invitation/information?invitationKey=%s", invitationKey)));

		// then
		perform.andExpect(status().isBadRequest());
		perform.andDo(print());
	}

	@DisplayName("그룹 초대장 확인 실패(초대장 미갱신)")
	@WithMockCustomMemberUser(username = "dksgkswn", userId = "2", kakaoId = "2")
	@Sql("group-invitationCheck4.sql")
	@Test
	void shouldFailToCheckGroupInvitationWhenInvitationNotRefreshed() throws Exception {
		// given
		String invitationKey = "testcode1";

		// when
		ResultActions perform = mvc.perform(
			get(String.format("/api/group/invitation/information?invitationKey=%s", invitationKey)));

		// then
		perform.andExpect(status().isForbidden());
		perform.andDo(print());
	}

	@DisplayName("그룹원 조회 성공")
	@WithMockCustomAdminUser
	@Sql("group-getMembers1.sql")
	@Test
	void shouldSuccessfullyRetrieveGroupMembers() throws Exception {
		// when
		ResultActions perform = mvc.perform(get("/api/group"));

		// then
		perform.andExpect(status().isOk());
		perform.andDo(print());
	}

	@DisplayName("그룹원 조회 실패(멤버 등록안됨)")
	@WithMockCustomAdminUser
	@Sql("group-getMembers2.sql")
	@Test
	void shouldFailToRetrieveGroupMembersWhenNoRegisteredMember() throws Exception {
		// when
		ResultActions perform = mvc.perform(get("/api/group"));

		// then
		perform.andExpect(status().isOk());
		perform.andDo(print());
	}

	@DisplayName("그룹원 조회 성공(공백)")
	@WithMockCustomAdminUser
	@Sql("group-getMembers3.sql")
	@Test
	void shouldSuccessfullyRetrieveGroupMembersWhenGroupIsEmpty() throws Exception {
		// when
		ResultActions perform = mvc.perform(get("/api/group"));

		// then
		perform.andExpect(status().isOk());
		perform.andDo(print());
	}

	@DisplayName("그룹 초대링크 발급 성공")
	@WithMockCustomAdminUser(username = "eunjin", isAdmin = "true")
	@Sql("group-getInvitation1.sql")
	@Test
	void shouldSuccessfullyIssueGroupInvitationLink() throws Exception {
		// when
		ResultActions perform = mvc.perform(get("/api/group/invitation"));

		// then
		perform.andExpect(status().isOk());
		perform.andDo(print());
	}

	@DisplayName("그룹 초대링크 발급 실패(그룹 미등록으로 멤버가 아님)")
	@WithMockCustomAdminUser
	@Sql("group-getInvitation2.sql")
	@Test
	void shouldFailToIssueGroupInvitationLinkWhenNotGroupMember() throws Exception {
		// when
		ResultActions perform = mvc.perform(get("/api/group/invitation"));

		// then
		perform.andExpect(status().isBadRequest());
		perform.andDo(print());
	}

	@DisplayName("그룹 초대링크 발급 실패(그룹장 아님)")
	@WithMockCustomAdminUser(username = "dksgkswn", userId = "2", kakaoId = "2")
	@Sql("group-getInvitation3.sql")
	@Test
	void shouldFailToIssueGroupInvitationLinkWhenNotGroupLeader() throws Exception {
		// when
		ResultActions perform = mvc.perform(get("/api/group/invitation"));

		// then
		perform.andExpect(status().isBadRequest());
		perform.andDo(print());
	}

	@DisplayName("그룹 생성시 Auditing 확인")
	@WithMockCustomAdminUser(isAdmin = "true")
	@Sql("group-create1.sql")
	@Test
	void shouldCheckUserAuditingWhenCreatingGroup() throws Exception {
		// given
		Create.Request requestDTO = Create.Request.builder()
			.marketName("kakao")
			.marketNumber("10-31223442")
			.mainAddress("금강로 279번길 19")
			.detailAddress("ㅁㅁ건물 2층 ㅇㅇ상가")
			.build();
		String request = om.writeValueAsString(requestDTO);

		// when
		ResultActions perform = mvc.perform(post("/api/group")
			.contentType(MediaType.APPLICATION_JSON)
			.content(request));

		// then
		perform.andDo(print());
	}
}
