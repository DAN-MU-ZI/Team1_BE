package com.example.team1_be.domain.Group.DTO;

import java.util.List;
import java.util.stream.Collectors;

import com.example.team1_be.domain.Group.Group;
import com.example.team1_be.domain.User.User;

import lombok.Getter;

public class GetMembers {
	@Getter
	public static class Response {
		private final String groupName;
		private final String userName;
		private final List<MemberInfo> members;

		public Response(Group group, User user, List<User> users) {
			this.groupName = group.getName();
			this.userName = user.getName();
			this.members = users.stream()
				.map(MemberInfo::new)
				.collect(Collectors.toList());
		}

		@Getter
		private class MemberInfo {
			private final Long userId;
			private final String name;
			private final Boolean isAdmin;

			private MemberInfo(User user) {
				this.userId = user.getId();
				this.name = user.getName();
				this.isAdmin = user.getIsAdmin();
			}
		}
	}
}
