package com.lms.api.admin.service;

import static org.junit.jupiter.api.Assertions.assertEquals;

import com.lms.api.common.dto.UserType;
import com.lms.api.common.entity.UserEntity;
import com.lms.api.common.repository.UserRepository;
import java.time.LocalDate;
import java.time.LocalTime;
import java.util.List;
import lombok.extern.slf4j.Slf4j;
import org.junit.jupiter.api.AfterEach;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.test.context.ActiveProfiles;

@Slf4j
@SpringBootTest
//@Transactional
@ActiveProfiles("test")
public class ReservationGroupUpdateAsyncTest {

  @Autowired
  private ReservationRepository reservationRepository;

  @Autowired
  private UserRepository userRepository;

  @Autowired
  private TeacherRepository teacherRepository;

  @Autowired
  private CourseRepository courseRepository;

  @Autowired
  private ReservationGroupService reservationGroupService;

  private ReservationEntity reservation;
  private CourseEntity course;
  private UserEntity studentUser;
  private TeacherEntity teacher;

  @BeforeEach
  void setUp() throws Exception {
    studentUser = new UserEntity();
    studentUser.setId("student1");
    studentUser.setName("Student User 1");
    studentUser.setIsReceiveEmail(YN.Y);
    studentUser.setIsReceiveSms(YN.Y);
    studentUser.setType(UserType.S);

    teacher = new TeacherEntity();
    teacher.setUserEntity(studentUser);
    teacher.setType(TeacherType.HT);
    teacherRepository.save(teacher);

    course = new CourseEntity();
    course.setUserEntity(studentUser);
    course.setTeacherEntity(teacher);
    course.setLessonCount(10);
    course.setCountChangeReason("Test");
    course.setAttendanceCount(0);
    course.setEndDate(LocalDate.now().plusMonths(1));
    course.setIsCompletion(YN.N);
    courseRepository.save(course);

    reservation = new ReservationEntity();
    reservation.setDate(LocalDate.of(2024, 9, 23));
    reservation.setStartTime(LocalTime.of(6, 0));
    reservation.setEndTime(LocalTime.of(6, 30));
    reservation.setUserEntity(studentUser);
    reservation.setTeacherEntity(teacher);
    reservation.setCourseEntity(course);
    reservation.setAttendanceStatus(AttendanceStatus.R);

    reservationRepository.save(reservation);
    log.debug("## reservation: {}", reservation.getId());
    Thread.sleep(2000);
  }

  @AfterEach
  void tearDown() {
    reservationRepository.delete(reservation);
    courseRepository.delete(course);
    teacherRepository.delete(teacher);
    userRepository.delete(studentUser);
  }

  @Test
  @DisplayName("예약 생성 시 groupId 업데이트 테스트")
  void testReservationGroupIdUpdateOnCreate() {
    // 데이터베이스에서 저장된 예약 조회
    ReservationEntity savedReservation = reservationRepository.findById(reservation.getId()).orElseThrow();

    // 예약의 groupId가 예상대로 설정되었는지 검증
//    assertEquals(1, savedReservation.getGroupId(), "groupId는 1이어야 합니다.");
  }

  @Test
  @DisplayName("예약 삭제 시 관련된 예약의 groupId 업데이트 테스트")
  void testReservationGroupIdUpdateOnDelete() {
    // 예약 삭제
    reservationRepository.delete(reservation);

    // 삭제된 예약 이후 관련된 예약들의 groupId가 재조정되었는지 확인
    List<ReservationEntity> remainingReservations = reservationRepository.findByCourseIdAndUserIdAndTeacherIdAndDate(
        reservation.getCourseEntity().getId(),
        reservation.getUserEntity().getId(),
        reservation.getTeacherEntity().getUserId(),
        reservation.getDate()
    );

    // 예약이 없어진 후에도 groupId가 재조정되었는지 확인
    assertEquals(0, remainingReservations.size(), "남은 예약이 없어야 합니다.");
  }
}
