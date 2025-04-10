package com.lms.api.admin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import java.util.List;
import java.util.Map;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.jdbc.core.JdbcTemplate;
import org.springframework.test.context.ActiveProfiles;
import org.springframework.transaction.annotation.Transactional;

@Slf4j
@DisplayName("학사보고서")
@Transactional
@SpringBootTest
@ActiveProfiles("test")
public class ReservationGroupUpdateJdbcTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @BeforeEach
  void setUp() {
    // 예약 삭제
    jdbcTemplate.execute("DELETE FROM reservation");
    // 예약 추가
    String sql = """
        INSERT INTO reservation (course_id, user_id, teacher_id, date, start_time, end_time, report_yn, attendance_status, created_on, modified_on)
        VALUES 
            (1001, 'user1', 'teacher1', '2024-09-23', '06:00:00', '06:30:00', 0, 'R', now(), now()),
            (1001, 'user1', 'teacher1', '2024-09-23', '06:30:00', '07:00:00', 0, 'R', now(), now()),
            (1001, 'user1', 'teacher1', '2024-09-23', '07:00:00', '07:30:00', 1, 'Y', now(), now()),
            (1001, 'user1', 'teacher1', '2024-09-23', '11:00:00', '11:30:00', 0, 'R', now(), now()),
            (1001, 'user1', 'teacher1', '2024-09-23', '11:30:00', '12:00:00', 0, 'R', now(), now()),
            (1002, 'user2', 'teacher2', '2024-09-24', '08:00:00', '08:30:00', 0, 'R', now(), now()),
            (1002, 'user2', 'teacher2', '2024-09-24', '08:30:00', '09:00:00', 0, 'R', now(), now()),
            (1003, 'user3', 'teacher3', '2024-09-25', '09:00:00', '09:30:00', 0, 'R', now(), now()),
            (1003, 'user3', 'teacher3', '2024-09-25', '09:30:00', '10:00:00', 1, 'Y', now(), now()),
            (1003, 'user3', 'teacher3', '2024-09-25', '10:00:00', '10:30:00', 0, 'R', now(), now()),
            (1004, 'user4', 'teacher4', '2024-09-26', '10:30:00', '11:00:00', 1, 'Y', now(), now()),
            (1004, 'user4', 'teacher4', '2024-09-26', '11:00:00', '11:30:00', 0, 'R', now(), now()),
            (1004, 'user4', 'teacher4', '2024-09-26', '11:30:00', '12:00:00', 0, 'R', now(), now());
        """;

    jdbcTemplate.execute(sql);
  }

  private void updateGroupIds() {
    // group_id 초기화
    jdbcTemplate.execute("UPDATE reservation SET group_id = NULL;");

    // 각각의 SET 문을 개별로 실행
    jdbcTemplate.execute("SET @current_group_id := 0;");
    jdbcTemplate.execute("SET @previous_teacher_id := NULL;");
    jdbcTemplate.execute("SET @previous_user_id := NULL;");
    jdbcTemplate.execute("SET @previous_date := NULL;");
    jdbcTemplate.execute("SET @previous_end_time := NULL;");

    // group_id 업데이트
    jdbcTemplate.execute("""
            UPDATE reservation r
            JOIN (
                SELECT
                    r.id,
                    @current_group_id := IF(
                        r.course_id = @previous_course_id
                            AND r.teacher_id = @previous_teacher_id
                            AND r.user_id = @previous_user_id
                            AND r.date = @previous_date
                            AND r.start_time = @previous_end_time,
                        @current_group_id, -- 연속된 예약이면 같은 group_id 유지
                        @current_group_id + 1 -- 연속되지 않으면 새로운 group_id 할당
                    ) AS group_id,
                    @previous_course_id := r.course_id,
                    @previous_teacher_id := r.teacher_id,
                    @previous_user_id := r.user_id,
                    @previous_date := r.date,
                    @previous_end_time := r.end_time
                FROM reservation r
                ORDER BY r.course_id, r.teacher_id, r.user_id, r.date, r.start_time
            ) AS temp
            ON r.id = temp.id
            SET r.group_id = temp.group_id;
        """);

  }

  private void updateGroupIds(Long courseId, String teacherId, String userId, String date) {
    // group_id 초기화 (필터링된 레코드만)
    jdbcTemplate.update("UPDATE reservation SET group_id = NULL WHERE course_id = ? AND teacher_id = ? AND user_id = ? AND date = ?",
        courseId, teacherId, userId, date);

    // 각각의 SET 문을 개별로 실행
    jdbcTemplate.execute("SET @current_group_id := 0;");
    jdbcTemplate.execute("SET @previous_teacher_id := NULL;");
    jdbcTemplate.execute("SET @previous_user_id := NULL;");
    jdbcTemplate.execute("SET @previous_date := NULL;");
    jdbcTemplate.execute("SET @previous_end_time := NULL;");

    // group_id 업데이트 (필터링된 레코드만)
    jdbcTemplate.update("""
            UPDATE reservation r
            JOIN (
                SELECT
                    r.id,
                    @current_group_id := IF(
                        r.course_id = @previous_course_id
                            AND r.teacher_id = @previous_teacher_id
                            AND r.user_id = @previous_user_id
                            AND r.date = @previous_date
                            AND r.start_time = @previous_end_time,
                        @current_group_id, -- 연속된 예약이면 같은 group_id 유지
                        @current_group_id + 1 -- 연속되지 않으면 새로운 group_id 할당
                    ) AS group_id,
                    @previous_course_id := r.course_id,
                    @previous_teacher_id := r.teacher_id,
                    @previous_user_id := r.user_id,
                    @previous_date := r.date,
                    @previous_end_time := r.end_time
                FROM reservation r
                WHERE r.course_id = ? AND r.teacher_id = ? AND r.user_id = ? AND r.date = ?
                ORDER BY r.start_time
            ) AS temp
            ON r.id = temp.id
            SET r.group_id = temp.group_id;
        """, courseId, teacherId, userId, date);
  }

  // 학사보고서가 미작성된 예약 그룹 조회
  private List<Map<String, Object>> findReservationsWithoutReport() {
    String sql = """
            SELECT DISTINCT r.*
            FROM reservation r
                     JOIN (
                SELECT group_id
                FROM reservation
                GROUP BY group_id
                HAVING MAX(report_yn) = 0  -- 그룹 내에서 하나라도 report_yn = 1 이 있으면 제외
            ) AS no_report_groups ON r.group_id = no_report_groups.group_id
            ORDER BY r.date, r.start_time;
        """;
    return jdbcTemplate.queryForList(sql);
  }

  @Test
  void test() {
    // 데이터가 제대로 삽입되었는지 검증
    int count = jdbcTemplate.queryForObject("SELECT COUNT(*) FROM reservation", Integer.class);
    assertEquals(13, count, "총 13개의 예약 레코드가 삽입되어야 합니다.");

    // group_id 업데이트 실행
//    updateGroupIds();
    updateGroupIds(1001L, "teacher1", "user1", "2024-09-23");
    updateGroupIds(1002L, "teacher2", "user2", "2024-09-24");
    updateGroupIds(1003L, "teacher3", "user3", "2024-09-25");
    updateGroupIds(1004L, "teacher4", "user4", "2024-09-26");

    // 학사보고서 미작성 그룹 조회
    List<Map<String, Object>> noReportGroups = findReservationsWithoutReport();
    assertEquals(4, noReportGroups.size(), "총 4개의 미작성 학사보고서 레코드가 있어야 합니다.");
  }
}
