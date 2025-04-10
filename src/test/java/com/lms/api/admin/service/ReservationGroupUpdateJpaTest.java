package com.lms.api.admin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lms.api.common.dto.UserType;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalTime;
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
public class ReservationGroupUpdateJpaTest {

  @Autowired
  private JdbcTemplate jdbcTemplate;

  @Autowired
  private ReservationRepository reservationRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TeacherRepository teacherRepository;

  @Autowired
  private CourseRepository courseRepository;

  @BeforeEach
  void setUp() {
    // 예약 삭제
    jdbcTemplate.execute("DELETE FROM reservation");
    // 학생용 User 생성 또는 조회
    UserEntity studentUser1 = new UserEntity();
    studentUser1.setId("student1");
    studentUser1.setName("Student User 1");
    studentUser1.setIsReceiveEmail(YN.Y);
    studentUser1.setIsReceiveSms(YN.Y);
    studentUser1.setType(UserType.S);
    userRepository.save(studentUser1);

    UserEntity studentUser2 = new UserEntity();
    studentUser2.setId("student2");
    studentUser2.setName("Student User 2");
    studentUser2.setIsReceiveEmail(YN.Y);
    studentUser2.setIsReceiveSms(YN.Y);
    studentUser2.setType(UserType.A);
    userRepository.save(studentUser2);

    UserEntity studentUser3 = new UserEntity();
    studentUser3.setId("student3");
    studentUser3.setLoginId("student3");
    studentUser3.setName("Student User 3");
    studentUser3.setIsReceiveEmail(YN.Y);
    studentUser3.setIsReceiveSms(YN.Y);
    studentUser3.setType(UserType.A);
    userRepository.save(studentUser3);

    UserEntity studentUser4 = new UserEntity();
    studentUser4.setId("student4");
    studentUser4.setLoginId("student4");
    studentUser4.setName("Student User 4");
    studentUser4.setIsReceiveEmail(YN.Y);
    studentUser4.setIsReceiveSms(YN.Y);
    studentUser4.setType(UserType.A);
    userRepository.save(studentUser4);

    // 강사용 User 생성 또는 조회
    UserEntity teacherUser1 = new UserEntity();
    teacherUser1.setId("teacherUser1");
    teacherUser1.setLoginId("teacherUser1");
    teacherUser1.setName("Teacher User 1");
    teacherUser1.setIsReceiveEmail(YN.Y);
    teacherUser1.setIsReceiveSms(YN.Y);
    teacherUser1.setType(UserType.T);

    UserEntity teacherUser2 = new UserEntity();
    teacherUser2.setId("teacherUser2");
    teacherUser2.setLoginId("teacherUser2");
    teacherUser2.setName("Teacher User 2");
    teacherUser2.setIsReceiveEmail(YN.Y);
    teacherUser2.setIsReceiveSms(YN.Y);
    teacherUser2.setType(UserType.T);

    UserEntity teacherUser3 = new UserEntity();
    teacherUser3.setId("teacherUser3");
    teacherUser3.setLoginId("teacherUser3");
    teacherUser3.setName("Teacher User 3");
    teacherUser3.setIsReceiveEmail(YN.Y);
    teacherUser3.setIsReceiveSms(YN.Y);
    teacherUser3.setType(UserType.T);

    UserEntity teacherUser4 = new UserEntity();
    teacherUser4.setId("teacherUser4");
    teacherUser4.setLoginId("teacherUser4");
    teacherUser4.setName("Teacher User 4");
    teacherUser4.setIsReceiveEmail(YN.Y);
    teacherUser4.setIsReceiveSms(YN.Y);
    teacherUser4.setType(UserType.T);

    TeacherEntity teacher1 = new TeacherEntity();
    teacher1.setUserEntity(teacherUser1);
    teacher1.setType(TeacherType.HT);  // 강사 유형 설정
    teacherRepository.save(teacher1);

    TeacherEntity teacher2 = new TeacherEntity();
    teacher2.setUserEntity(teacherUser2);
    teacher2.setType(TeacherType.HT);  // 강사 유형 설정
    teacherRepository.save(teacher2);

    TeacherEntity teacher3 = new TeacherEntity();
    teacher3.setUserEntity(teacherUser3);
    teacher3.setType(TeacherType.HT);  // 강사 유형 설정
    teacherRepository.save(teacher3);

    TeacherEntity teacher4 = new TeacherEntity();
    teacher4.setUserEntity(teacherUser4);
    teacher4.setType(TeacherType.HT);  // 강사 유형 설정
    teacherRepository.save(teacher4);

    // 강의 생성
    CourseEntity course1 = new CourseEntity();
    course1.setUserEntity(studentUser1);  // 학생과 연결
    course1.setTeacherEntity(teacher1);   // 강사와 연결
    course1.setLessonCount(10);
    course1.setCountChangeReason("10");
    course1.setAttendanceCount(10);
    course1.setEndDate(LocalDate.now());
    course1.setIsCompletion(YN.N);
    courseRepository.save(course1);

    CourseEntity course2 = new CourseEntity();
    course2.setUserEntity(studentUser2);
    course2.setTeacherEntity(teacher2);
    course2.setLessonCount(10);
    course2.setCountChangeReason("10");
    course2.setAttendanceCount(10);
    course2.setEndDate(LocalDate.now());
    course2.setIsCompletion(YN.N);
    courseRepository.save(course2);

    CourseEntity course3 = new CourseEntity();
    course3.setUserEntity(studentUser3);
    course3.setTeacherEntity(teacher3);
    course3.setLessonCount(10);
    course3.setCountChangeReason("10");
    course3.setAttendanceCount(10);
    course3.setEndDate(LocalDate.now());
    course3.setIsCompletion(YN.N);
    courseRepository.save(course3);

    CourseEntity course4 = new CourseEntity();
    course4.setUserEntity(studentUser4);
    course4.setTeacherEntity(teacher4);
    course4.setLessonCount(10);
    course4.setCountChangeReason("10");
    course4.setAttendanceCount(10);
    course4.setEndDate(LocalDate.now());
    course4.setIsCompletion(YN.N);
    courseRepository.save(course4);

    // 예약 데이터 삽입
    reservationRepository.save(
        new ReservationEntity(LocalDate.of(2024, 9, 23), LocalTime.of(6, 0), LocalTime.of(6, 30),
            AttendanceStatus.R, course1, studentUser1, teacher1));
    reservationRepository.save(
        new ReservationEntity(LocalDate.of(2024, 9, 23), LocalTime.of(6, 30), LocalTime.of(7, 0),
            AttendanceStatus.R, course1, studentUser1, teacher1));
    reservationRepository.save(
        new ReservationEntity(LocalDate.of(2024, 9, 23), LocalTime.of(7, 0), LocalTime.of(7, 30),
            AttendanceStatus.R, course1, studentUser1, teacher1));
    reservationRepository.save(
        new ReservationEntity(LocalDate.of(2024, 9, 23), LocalTime.of(11, 0), LocalTime.of(11, 30),
            AttendanceStatus.R, course1, studentUser1, teacher1));
    reservationRepository.save(
        new ReservationEntity(LocalDate.of(2024, 9, 23), LocalTime.of(11, 30), LocalTime.of(12, 0),
            AttendanceStatus.R, course1, studentUser1, teacher1));

    reservationRepository.save(
        new ReservationEntity(LocalDate.of(2024, 9, 24), LocalTime.of(8, 0), LocalTime.of(8, 30),
            AttendanceStatus.R, course2, studentUser2, teacher2));
    reservationRepository.save(
        new ReservationEntity(LocalDate.of(2024, 9, 24), LocalTime.of(8, 30), LocalTime.of(9, 0),
            AttendanceStatus.R, course2, studentUser2, teacher2));

    reservationRepository.save(
        new ReservationEntity(LocalDate.of(2024, 9, 25), LocalTime.of(9, 0), LocalTime.of(9, 30),
            AttendanceStatus.R, course3, studentUser3, teacher3));
    reservationRepository.save(
        new ReservationEntity(LocalDate.of(2024, 9, 25), LocalTime.of(9, 30), LocalTime.of(10, 0),
            AttendanceStatus.R, course3, studentUser3, teacher3));
    reservationRepository.save(
        new ReservationEntity(LocalDate.of(2024, 9, 25), LocalTime.of(10, 0), LocalTime.of(10, 30),
            AttendanceStatus.R, course3, studentUser3, teacher3));

    reservationRepository.save(
        new ReservationEntity(LocalDate.of(2024, 9, 26), LocalTime.of(10, 30), LocalTime.of(11, 0),
            AttendanceStatus.R, course4, studentUser4, teacher4));
    reservationRepository.save(
        new ReservationEntity(LocalDate.of(2024, 9, 26), LocalTime.of(11, 0), LocalTime.of(11, 30),
            AttendanceStatus.R, course4, studentUser4, teacher4));
    reservationRepository.save(
        new ReservationEntity(LocalDate.of(2024, 9, 26), LocalTime.of(11, 30), LocalTime.of(12, 0),
            AttendanceStatus.R, course4, studentUser4, teacher4));
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

  private void updateGroupIds2() {
    // SQL 문들을 하나의 문자열로 작성하고 세미콜론으로 구분
    String sql = """
            UPDATE reservation SET group_id = NULL;
            
            SET @current_group_id := 0;
            SET @previous_teacher_id := NULL;
            SET @previous_user_id := NULL;
            SET @previous_date := NULL;
            SET @previous_end_time := NULL;

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
        """;

    // 한 번에 실행
    jdbcTemplate.execute(sql);
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
    updateGroupIds();

    // 학사보고서 미작성 그룹 조회
    List<Map<String, Object>> noReportGroups = findReservationsWithoutReport();
    assertEquals(4, noReportGroups.size(), "총 4개의 미작성 학사보고서 레코드가 있어야 합니다.");
  }
}
