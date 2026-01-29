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

-- 프로젝트 오픈 알림
insert ignore into alarm (alarm_id, alarm_context) values (103, '프로젝트 ${projectName}가 생성되었습니다.');

-- 관리자 프로젝트 심사 테스트 더미 데이터 (임시)
-- ========================================================
-- 1. 회원 데이터 (Member)
-- ========================================================

-- [ID: 100] 관리자 (Admin) - 심사 수행 주체
INSERT INTO members (id, email, name, nick_name, role, provider, created_at, updated_at, ticket_count, support_count, support_level, gender, birthday, introduction)
VALUES (100, 'admin@muses.com', '최고관리자', 'AdminMaster', 'ADMIN', 'LOCAL', NOW(), NOW(), 0, 0, 1, 1, '1990-01-01', '뮤즈 플랫폼 관리자입니다.');

-- [ID: 101] 메이커 (Maker) - 프로젝트 생성 주체
INSERT INTO members (id, email, name, nick_name, role, provider, created_at, updated_at, ticket_count, support_count, support_level, gender, birthday, introduction)
VALUES (101, 'maker@muses.com', '김아티스트', 'IndieKim', 'MAKER', 'LOCAL', NOW(), NOW(), 0, 0, 1, 0, '1995-05-05', '인디 음악을 사랑하는 아티스트입니다.');


-- ========================================================
-- CASE 1: [제출 테스트용] 작성 완료된 DRAFT 상태 프로젝트
-- API: POST /api/projects/{projectId}/submit 테스트용
-- ========================================================

-- [ID: 10] 프로젝트 (DRAFT, 5단계 저장완료)
INSERT INTO projects (project_id, user_id, title, description, thumbnail_url, status, funding_status, last_saved_step, target_amount, deadline, opening, achieve_rate, supporter_count, age_limit, region, created_at, updated_at)
VALUES (10, 101, '[제출테스트] 홍대 인디밴드 단독 콘서트', '제출 API 테스트를 위한 완벽한 프로젝트입니다.', 'https://dummyimage.com/600x400/000/fff&text=Draft', 'DRAFT', 'PREPARING', 5, 5000000, DATE_ADD(NOW(), INTERVAL 30 DAY), DATE_ADD(NOW(), INTERVAL 1 DAY), 0, 0, 'ALL', 'SEOUL', NOW(), NOW());

-- 10번 프로젝트 상세 내용 (스토리)
INSERT INTO project_contents (project_id, story_html, refund_policy, location_detail)
VALUES (10, '<h1>공연 소개</h1><p>멋진 공연입니다.</p>', '공연 3일 전 환불 불가', '서울 마포구 서교동 123');

-- 10번 프로젝트 관리자 정보
INSERT INTO project_managers (project_id, host_id, host_profile_img, host_phone, host_birth, host_address, host_bio, manager_name, manager_phone, manager_email)
VALUES (10, 101, 'https://dummyimage.com/100x100', '010-1234-5678', '19950505', '서울 마포구', '안녕하세요 김아티스트입니다.', '김매니저', '010-9876-5432', 'manager@test.com');

-- 10번 프로젝트 리워드
INSERT INTO rewards (project_id, reward_name, price, description, total_quantity, sold_quantity, type)
VALUES (10, '얼리버드 티켓', 30000, '가장 빠른 예매', 50, 0, 'TICKET');


-- ========================================================
-- CASE 2: [심사/상세조회 테스트용] 심사 대기중 (PENDING)
-- API: GET /api/admin/projects (목록) & PATCH review (승인) 테스트용
-- ========================================================

-- [ID: 20] 프로젝트 (PENDING)
INSERT INTO projects (project_id, user_id, title, description, thumbnail_url, status, funding_status, last_saved_step, target_amount, deadline, opening, achieve_rate, supporter_count, age_limit, region, created_at, updated_at)
VALUES (20, 101, '[심사대기] 2026 재즈 페스티벌', '관리자님의 승인을 기다리는 프로젝트입니다.', 'https://dummyimage.com/600x400/000/fff&text=Pending', 'PENDING', 'PREPARING', 5, 10000000, DATE_ADD(NOW(), INTERVAL 60 DAY), DATE_ADD(NOW(), INTERVAL 7 DAY), 0, 0, 'ADULT', 'BUSAN', DATE_SUB(NOW(), INTERVAL 1 HOUR), NOW());

-- 20번 프로젝트 상세 내용
INSERT INTO project_contents (project_id, story_html, refund_policy, location_detail)
VALUES (20, '<h1>재즈의 밤</h1><p>부산에서 즐기는 재즈.</p>', '행사 당일 환불 불가', '부산 해운대구');

-- 20번 프로젝트 관리자 정보
INSERT INTO project_managers (project_id, host_id, host_profile_img, host_phone, host_birth, host_address, host_bio)
VALUES (20, 101, 'https://dummyimage.com/100x100', '010-1111-2222', '19950505', '부산 해운대구', '재즈 아티스트입니다.', NULL, NULL, NULL);

-- 20번 프로젝트 리워드
INSERT INTO rewards (project_id, reward_name, price, description, total_quantity, sold_quantity, type)
VALUES
    (20, 'VIP석 입장권', 100000, '무대 바로 앞 좌석', 10, 0, 'TICKET'),
    (20, '일반석 입장권', 50000, '자유 좌석', 100, 0, 'TICKET');


-- ========================================================
-- CASE 3: [반려 테스트용] 심사 대기중 (PENDING) -> 반려할 것
-- API: PATCH review (반려) 테스트용
-- ========================================================

-- [ID: 21] 프로젝트 (PENDING - 반려 대상)
INSERT INTO projects (project_id, user_id, title, description, thumbnail_url, status, funding_status, last_saved_step, target_amount, deadline, opening, achieve_rate, supporter_count, age_limit, region, created_at, updated_at)
VALUES (21, 101, '[심사대기] 내용이 부실한 프로젝트', '반려 사유를 테스트하기 위한 프로젝트입니다.', 'https://dummyimage.com/600x400/000/fff&text=BadProject', 'PENDING', 'PREPARING', 5, 500000, DATE_ADD(NOW(), INTERVAL 10 DAY), NOW(), 0, 0, 'ALL', 'SEOUL', DATE_SUB(NOW(), INTERVAL 2 HOUR), NOW());

INSERT INTO project_contents (project_id, story_html, refund_policy) VALUES (21, '<p>내용 없음</p>', '환불 안됨');
INSERT INTO project_managers (project_id, host_id, host_phone) VALUES (21, 101, '010-0000-0000');
INSERT INTO rewards (project_id, reward_name, price, type) VALUES (21, '기부', 1000, 'NONE');


-- ========================================================
-- CASE 4: [목록 조회용] 이미 승인된 프로젝트 (APPROVED)
-- API: GET /api/admin/projects?status=APPROVED 테스트용
-- ========================================================

-- [ID: 30] 프로젝트 (APPROVED)
INSERT INTO projects (project_id, user_id, title, description, thumbnail_url, status, funding_status, last_saved_step, target_amount, deadline, opening, achieve_rate, supporter_count, age_limit, region, created_at, updated_at)
VALUES (30, 101, '[승인됨] 성수동 팝업 스토어', '이미 승인되어 오픈 예정인 프로젝트입니다.', 'https://dummyimage.com/600x400/000/fff&text=Approved', 'APPROVED', 'SCHEDULED', 5, 3000000, DATE_ADD(NOW(), INTERVAL 40 DAY), DATE_ADD(NOW(), INTERVAL 5 DAY), 0, 0, 'ALL', 'SEOUL', DATE_SUB(NOW(), INTERVAL 1 DAY), NOW());

-- 30번 승인 이력 (Audit Log)
INSERT INTO project_audit (project_id, admin_id, previous_status, current_status, reason, created_at)
VALUES (30, 100, 'PENDING', 'APPROVED', '기획 의도가 명확하여 승인함.', DATE_SUB(NOW(), INTERVAL 1 DAY));


-- ========================================================
-- CASE 5: [목록 조회용] 이미 반려된 프로젝트 (REJECTED)
-- API: GET /api/admin/projects?status=REJECTED 테스트용
-- ========================================================

-- [ID: 40] 프로젝트 (REJECTED)
INSERT INTO projects (project_id, user_id, title, description, thumbnail_url, status, funding_status, last_saved_step, target_amount, deadline, opening, achieve_rate, supporter_count, age_limit, region, created_at, updated_at)
VALUES (40, 101, '[반려됨] 위험한 불꽃놀이 축제', '안전 문제로 반려된 프로젝트입니다.', 'https://dummyimage.com/600x400/000/fff&text=Rejected', 'REJECTED', 'PREPARING', 5, 100000000, DATE_ADD(NOW(), INTERVAL 50 DAY), NOW(), 0, 0, 'ALL', 'GANGWON', DATE_SUB(NOW(), INTERVAL 3 DAY), NOW());

-- 40번 반려 이력 (Audit Log)
INSERT INTO project_audit (project_id, admin_id, previous_status, current_status, reason, created_at)
VALUES (40, 100, 'PENDING', 'REJECTED', '화재 위험성 및 안전 대책 미비로 인해 반려합니다.', DATE_SUB(NOW(), INTERVAL 2 DAY));