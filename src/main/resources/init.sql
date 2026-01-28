-- =============================================
-- 초기 데이터 삽입 스크립트
-- JPA가 테이블 생성 후 실행됨
-- =============================================

-- 알람 템플릿 삽입
-- insert ignore into alarm (alarm_id, alarm_context) values (1, '${projectName}가 생성되었습니다.');

-- 프로젝트 오픈 알림
insert ignore into alarm (alarm_id, alarm_context) values (3, '프로젝트 ${projectName}가 생성되었습니다.');
