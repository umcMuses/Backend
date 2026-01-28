-- =============================================
-- 초기 데이터 삽입 스크립트
-- JPA가 테이블 생성 후 실행됨
-- =============================================

-- 알람 템플릿 삽입
insert ignore into alarm (alarm_id, alarm_context) values (1, '${projectName}가 생성되었습니다.');

insert ignore into alarm (alarm_id, alarm_context)
       values (2, '${projectName} QR이 발급되었습니다.');


insert ignore into alarm (alarm_id, alarm_context)
values (3, '${projectName} 결제가 취소되었습니다.');

insert ignore into alarm (alarm_id, alarm_context)
values (4, '${projectName} 펀딩이 성공했습니다.');


insert ignore into alarm (alarm_id, alarm_context)
values (5, '${projectName} 펀딩이 실패했습니다.');
