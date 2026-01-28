-- 0. 프로필 미설정 계정 (GUEST)
INSERT INTO members (created_at, updated_at, email, passwd, name, role, provider)
VALUES (NOW(), NOW(), 'lys0000@google.com', '1223', '박지성', 'GUEST', 'LOCAL');

-- 1. 관리자 계정 (ADMIN)
INSERT INTO members (created_at, updated_at, email, passwd, name, nick_name, introduction, birthday, gender, role, provider)
VALUES (NOW(), NOW(), 'admin@muses.com', '0000', '뮤즈관리자', 'AdminMuse', '안녕하세요.', '20030813', 0,'ADMIN', 'LOCAL');

-- 2. 일반 사용자 1 (MAKER)
INSERT INTO members (created_at, updated_at, email, passwd, name, nick_name, introduction, birthday, gender, role, provider)
VALUES (NOW(), NOW(), 'maker@muses.com', '0000', '김철수', 'RapperLee', '반가워요 메이커입니다.','20001223',0,'MAKER', 'LOCAL');

-- 3. 일반 사용자 2 (CREATOR)
INSERT INTO members (created_at, updated_at, email, passwd, name, nick_name, introduction, birthday, gender, role, provider)
VALUES (NOW(), NOW(), 'creator@muses.com', '0000', '이유리', 'MusicLover', '저는 뮤지션이에요', '19970201', 1,'CREATOR', 'LOCAL');

-- 4. 소셜 로그인 사용자
-- INSERT INTO members (created_at, updated_at, email, password, name, nickname, role, provider)
-- VALUES (NOW(), NOW(), 'social@kakao.com', '', '박지성', 'KakaoUser', 'MAKER', 'KAKAO');

-- 알람 템플릿
INSERT INTO alarm (alarm_id, alarm_context) VALUES (1, '${projectName} 프로젝트가 생성되었습니다.');
INSERT INTO alarm (alarm_id, alarm_context) VALUES (2, '${projectName} 프로젝트가 승인되었습니다.');
INSERT INTO alarm (alarm_id, alarm_context) VALUES (3, '${projectName} 프로젝트 펀딩이 시작되었습니다.');
INSERT INTO alarm (alarm_id, alarm_context) VALUES (4, '${projectName} 프로젝트 펀딩이 성공적으로 완료되었습니다.');
INSERT INTO alarm (alarm_id, alarm_context) VALUES (5, '${projectName} 프로젝트에 ${makerName}님이 후원하셨습니다.');
INSERT INTO alarm (alarm_id, alarm_context) VALUES (6, '${projectName} 프로젝트의 ${rewardName} 리워드를 후원하셨습니다.');