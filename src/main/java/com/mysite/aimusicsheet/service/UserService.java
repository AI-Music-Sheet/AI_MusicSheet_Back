package com.mysite.aimusicsheet.service;

import com.mysite.aimusicsheet.entity.User;
import com.mysite.aimusicsheet.repository.UserRepository;
import com.mysite.aimusicsheet.jwt.JwtTokenProvider;
import io.jsonwebtoken.ExpiredJwtException;
import io.jsonwebtoken.JwtException;
import lombok.RequiredArgsConstructor;
import org.springframework.security.crypto.bcrypt.BCryptPasswordEncoder;
import org.springframework.stereotype.Service;

import java.util.Map;

@Service
@RequiredArgsConstructor
public class UserService {

    private final UserRepository userRepository;
    private final BCryptPasswordEncoder passwordEncoder;
    private final JwtTokenProvider jwtTokenProvider;

   //회원가입
    public String registerUser(
            String userid,
            String username,
            String nickname,
            String password,
            String email
    ) {
        // 아이디 중복 체크
        if (userRepository.existsByUserid(userid)) {
            throw new IllegalStateException("서버 오류: 이미 존재하는 사용자 아이디입니다.");
        }
        // 이메일 중복 체크
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("서버 오류: 이미 존재하는 이메일입니다.");
        }

        // 비밀번호 해싱
        String encodedPassword = passwordEncoder.encode(password);

        // 사용자 엔티티 생성 및 저장
        User user = new User();
        user.setUserid(userid);
        user.setUsername(username);
        user.setNickname(nickname);
        user.setPassword(encodedPassword);
        user.setEmail(email);
        userRepository.save(user);

        return "회원가입 성공!";
    }

    //아이디 중복확인
    public void checkUserIdDuplicate(String userid) {
        if (userRepository.existsByUserid(userid)) {
            throw new IllegalStateException("서버 오류: 이미 존재하는 사용자 아이디입니다.");
        }
    }

   //이메일 중복확인
    public void checkEmailDuplicate(String email) {
        if (userRepository.existsByEmail(email)) {
            throw new IllegalStateException("서버 오류: 이미 존재하는 이메일입니다.");
        }
    }

    //로그인
    public Map<String, String> loginUser(String userid, String password) {
        User user = userRepository.findByUserid(userid)
                .orElseThrow(() -> new IllegalStateException("존재하지 않는 아이디입니다."));

        if (!passwordEncoder.matches(password, user.getPassword())) {
            throw new IllegalStateException("비밀번호가 일치하지 않습니다.");
        }

        String accessToken  = jwtTokenProvider.createToken(user.getId(), user.getUserid(), "ROLE_USER");
        String refreshToken = jwtTokenProvider.createRefreshToken(user.getId(), user.getUserid());

        user.setRefreshToken(refreshToken);
        userRepository.save(user);

        return Map.of(
                "accessToken",  accessToken,
                "refreshToken", refreshToken
        );
    }

    /**
     * 리프레시 토큰으로 Access/Refresh 토큰 재발급 (토큰 회전)
     */
    public Map<String, String> refreshAccessToken(String refreshToken) {
        try {
            jwtTokenProvider.validateToken(refreshToken);
        } catch (ExpiredJwtException ex) {
            throw new IllegalStateException("리프레시 토큰이 만료되었습니다.");
        } catch (JwtException ex) {
            throw new IllegalStateException("유효하지 않은 리프레시 토큰입니다.");
        }

        Long userPk = jwtTokenProvider.getUserId(refreshToken);
        String userid = jwtTokenProvider.getUsername(refreshToken);

        User user = userRepository.findById(userPk)
                .orElseThrow(() -> new IllegalStateException("사용자 정보를 찾을 수 없습니다."));
        if (!refreshToken.equals(user.getRefreshToken())) {
            throw new IllegalStateException("리프레시 토큰이 일치하지 않습니다.");
        }

        String newAccessToken  = jwtTokenProvider.createToken(user.getId(), userid, "ROLE_USER");
        String newRefreshToken = jwtTokenProvider.createRefreshToken(user.getId(), userid);

        user.setRefreshToken(newRefreshToken);
        userRepository.save(user);

        return Map.of(
                "accessToken",  newAccessToken,
                "refreshToken", newRefreshToken
        );
    }
}
