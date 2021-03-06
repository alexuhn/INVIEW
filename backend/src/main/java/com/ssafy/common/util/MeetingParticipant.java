package com.ssafy.common.util;

import java.util.HashMap;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.stereotype.Component;

import com.ssafy.common.util.bean.ChattingParticipant;
import com.ssafy.common.util.bean.ChattingUser;
import com.ssafy.db.entity.User;

@Component
public class MeetingParticipant {
	ChattingUser chattingUser;

	public MeetingParticipant(ChattingUser chattingUser) {
		this.chattingUser = chattingUser;
	}

	public List<User> getParticipantByMeetingId(String meetingId) {
		Map<String, ChattingParticipant> hashMap = chattingUser.getParticipantByMeetingId().get(meetingId);
		if (hashMap == null)
			return null;
		List<User> participantList = new LinkedList<>();
		for (String sessionId : hashMap.keySet()) {
			participantList.add(hashMap.get(sessionId).getUser());
		}
		return participantList;
	}

	public List<ChattingParticipant> getReadyParticipantByMeetingId(String meetingId) {
		Map<String, ChattingParticipant> hashMap = chattingUser.getParticipantByMeetingId().get(meetingId);
		if (hashMap == null)
			return null;
		List<ChattingParticipant> participantList = new LinkedList<>();
		ChattingParticipant participant;
		for (String sessionId : hashMap.keySet()) {
			participantList.add(hashMap.get(sessionId));
		}
		return participantList;
	}

	public void setReadyParticipantByMeetingId(String meetingId, String sessionId, String ready) {
		Map<String, ChattingParticipant> hashMap = chattingUser.getParticipantByMeetingId().get(meetingId);
		ChattingParticipant participant = chattingUser.getParticipantBySessionId().get(sessionId);
		if (hashMap == null || participant == null)
			return;
		participant.setReady(ready);
		hashMap.put(sessionId, participant);
		chattingUser.getParticipantBySessionId().put(sessionId, participant);
	}

	public boolean checkParticipant(String meetingId, String email) {
		System.out.println("checkParticipant / chattingUser : " + chattingUser);
		Map<String, ChattingParticipant> hashMap = chattingUser.getParticipantByMeetingId().get(meetingId);
		if (hashMap == null)
			return false;
		for (String sessionId : hashMap.keySet()) {
			if (hashMap.get(sessionId).getUser().getEmail().equals(email))
				return false;
		}
		return true;
	}

	public void addParticipantBySessionId(String sessionId, String meetingId, User user) {
		boolean isHost = false;
		Map<String, ChattingParticipant> hashMap = chattingUser.getParticipantByMeetingId().get(meetingId);
		if (hashMap == null) {
			hashMap = new HashMap<>();
			isHost = true;
		}
		String nickname = user.getNickname();
		ChattingParticipant participant = ChattingParticipant.builder().sessionId(sessionId).meetingId(meetingId)
				.user(user).ready("F").build();
		hashMap.put(sessionId, participant);
		chattingUser.getParticipantByMeetingId().put(meetingId, hashMap);
		chattingUser.getParticipantBySessionId().put(sessionId, participant);
		if (isHost) {
			chattingUser.getHostByMeetingId().put(meetingId, participant);
		}
	}

	public ChattingParticipant getHostByMeetingId(String meetingId) {
		ChattingParticipant participant = chattingUser.getHostByMeetingId().get(meetingId);
		return participant;
	}

	public ChattingParticipant getParticipantBySessionId(String sessionId) {
		ChattingParticipant participant = chattingUser.getParticipantBySessionId().get(sessionId);
		return participant;
	}

	public ChattingParticipant deleteParticipantBySessionId(String sessionId) {
		ChattingParticipant participant = getParticipantBySessionId(sessionId);
		if (participant == null) {
			return null;
		}
		String meetingId = participant.getMeetingId();
		Map<String, ChattingParticipant> hashMap = chattingUser.getParticipantByMeetingId().getOrDefault(meetingId,
				new HashMap<>());
		hashMap.remove(sessionId);
		if (hashMap.isEmpty()) {
			chattingUser.getParticipantByMeetingId().remove(meetingId);
			chattingUser.getHostByMeetingId().remove(meetingId);
		}
		chattingUser.getParticipantBySessionId().remove(sessionId);
		return participant;
	}

	public List<ChattingParticipant> deleteParticipantByMeetingId(String meetingId, String deleteUserId) {
		Map<String, ChattingParticipant> hashMap = chattingUser.getParticipantByMeetingId().get(meetingId);
		if (hashMap == null) {
			return null;
		}
		List<ChattingParticipant> participantList = new LinkedList<>();
		ChattingParticipant participant;
		int userId = Integer.parseInt(deleteUserId);
		for (String sessionId : hashMap.keySet()) {
			participant = getParticipantBySessionId(sessionId);
			if (participant.getUser().getUserId() == userId) {
				hashMap.remove(sessionId);
				chattingUser.getParticipantBySessionId().remove(sessionId);
				participantList.add(participant);
			}
		}
		if (hashMap.isEmpty()) {
			chattingUser.getParticipantByMeetingId().remove(meetingId);
			chattingUser.getHostByMeetingId().remove(meetingId);
		}
		return participantList;
	}

	public ChattingParticipant setHost(String meetingId, ChattingParticipant host) {
		List<ChattingParticipant> participantList = getReadyParticipantByMeetingId(meetingId);
		String originalHostId = host.getSessionId();
		for (ChattingParticipant participant : participantList) {
			if (!originalHostId.equals(participant.getSessionId())) {
				chattingUser.getHostByMeetingId().put(meetingId, participant);
				return participant;
			}
		}
		return null;
	}

}
