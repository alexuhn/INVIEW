package com.ssafy.api.service;

import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.http.ResponseEntity;
import org.springframework.stereotype.Service;

import com.ssafy.api.request.UserUpdatePutReq;
import com.ssafy.common.model.response.BaseResponseBody;
import com.ssafy.db.entity.User;

@Service("myPageService")
public class MyPageServiceImpl implements MyPageService {

	@Autowired
	UserServiceImpl userServiceImpl;

	@Override
	public ResponseEntity<? extends BaseResponseBody> modifyUser(int userId, UserUpdatePutReq userUpdateInfo) { // 회원정보
																												// 수정
		User user = userServiceImpl.getUserByUserId(userId);

		if (userServiceImpl.getUserByUserId(userId).getNickname() != userUpdateInfo.getNickname()) { // 닉네임을 변경하는 경우
			if (userServiceImpl.getUserByNickname(userUpdateInfo.getNickname()) == null) // 닉네임 중복 검사
				return ResponseEntity.status(409).body(BaseResponseBody.of(409, "이미 등록된 닉네임입니다."));
			else
				user.setNickname(userUpdateInfo.getNickname()); // 닉네임 변경
		}

		if (userServiceImpl.getUserByUserId(userId).getPassword() != userUpdateInfo.getPassword()) // 패스워드를 변경하는 경우
			user.setPassword(userServiceImpl.passwordEncoder.encode(userUpdateInfo.getPassword())); // 패스워드 암호화하여 db에 저장

		userServiceImpl.userRepository.save(user);
		return ResponseEntity.status(200).body(BaseResponseBody.of(200, "회원 정보 수정 성공"));
	}
}