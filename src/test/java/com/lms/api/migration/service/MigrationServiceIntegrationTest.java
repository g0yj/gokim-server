package com.lms.api.migration.service;

import org.junit.jupiter.api.Test;
import org.springframework.beans.factory.annotation.Autowired;
import org.springframework.boot.test.context.SpringBootTest;
import org.springframework.context.annotation.Profile;
import org.springframework.test.context.ActiveProfiles;

@SpringBootTest
@ActiveProfiles("mig")
@Profile("migration")
class MigrationServiceIntegrationTest {

    @Autowired
    private UserMigrationService userMigrationService;
    @Autowired
    private TeacherMigrationService teacherMigrationService;
    @Autowired
    private ScheduleMigrationService scheduleMigrationService;
    @Autowired
    private ReservationMigrationService reservationMigrationService;
    @Autowired
    private RefundMigrationService refundMigrationService;
    @Autowired
    private PaymentMigrationService paymentMigrationService;
    @Autowired
    private OrderMigrationService orderMigrationService;
    @Autowired
    private OrderProductMigrationService orderProductMigrationService;
    @Autowired
    private MemberConsultationMigrationService memberConsultationMigrationService;
    @Autowired
    private LdfMigrationService ldfMigrationService;
    @Autowired
    private CourseMigrationService courseMigrationService;
    @Autowired
    private CourseHistoryMigrationService courseHistoryMigrationService;
    @Autowired
    private ConsultationMigrationService consultationMigrationService;
    @Autowired
    private LevelTestMigrationService levelTestMigrationService;
    @Autowired
    private DatabaseResetService databaseResetService;
    @Autowired
    private ConsultationHistoryMigrationService consultationHistoryMigrationService;
    @Autowired
    private CorrectionDataService correctionDataService;
    @Autowired
    private ConsultationLogService consultationLogService;
    @Autowired
    private UserPhoneUpdateService userPhoneUpdateService;
    @Autowired
    private ConsultationPhoneUpdateService consultationPhoneUpdateService;

    @Test
    void testUserMigration() throws Exception {
        userMigrationService.migrationBatch();
        userPhoneUpdateService.updateUserPhones();
    }

    @Test
    void testTeacherMigration() throws Exception {
        teacherMigrationService.migrationBatch();
    }

    @Test
    void testScheduleMigration() throws Exception {
        scheduleMigrationService.migrationBatch();
    }

    @Test
    void testReservationMigration() throws Exception {
        reservationMigrationService.migrationBatch();
    }

    @Test
    void testRefundMigration() throws Exception {
        refundMigrationService.migrationBatch();
    }

    @Test
    void testPaymentMigration() throws Exception {
        paymentMigrationService.migrationBatch();
    }

    @Test
    void testOrderMigration() throws Exception {
        orderMigrationService.migrationBatch();
    }

    @Test
    void testOrderProductMigration() throws Exception {
        orderProductMigrationService.migrationBatch();
    }

    @Test
    void testMemberConsultationMigration() throws Exception {
        memberConsultationMigrationService.migrationBatch();
    }

    @Test
    void testLdfMigration() throws Exception {
        ldfMigrationService.migrationBatch();
    }

    @Test
    void testCourseMigration() throws Exception {
        courseMigrationService.migrationBatch();
    }

    @Test
    void testCourseHistoryMigration() throws Exception {
        courseHistoryMigrationService.migrationBatch();
    }

    @Test
    void testConsultationMigration() throws Exception {
        consultationMigrationService.migrationBatch();
        consultationPhoneUpdateService.updateConsultationPhones();
    }

    @Test
    void testConsultationHistoryMigration() throws Exception {
        consultationHistoryMigrationService.migrationBatch();
    }

    @Test
    void testLevelTestMigration() throws Exception {
        levelTestMigrationService.migrationBatch();
    }

    @Test
    void testConsultationLogMigration() throws Exception {
        consultationLogService.migrationBatch();
    }

    @Test
    void testCorrectionDataService() throws Exception {
        correctionDataService.update();
    }

    @Test
    void testAllMigrations() throws Exception {
        databaseResetService.truncateTables();
        testUserMigration();
        testTeacherMigration();
        testScheduleMigration();
        testReservationMigration();
        testRefundMigration();
        testPaymentMigration();
        testOrderMigration();
        testOrderProductMigration();
        testMemberConsultationMigration();
        testLdfMigration();
        testCourseMigration();
        testCourseHistoryMigration();
        testConsultationMigration();
        testConsultationHistoryMigration();
        testLevelTestMigration();
        testConsultationLogMigration();
        correctionDataService.update();
    }
}
