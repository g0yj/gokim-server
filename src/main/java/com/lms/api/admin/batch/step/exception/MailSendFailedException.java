package com.lms.api.admin.batch.step.exception;
/**
 * 💡 이메일 전송 중 실패한 경우 발생시키는 예외.
 * - 네트워크 장애
 * - SMTP 인증 실패
 * - 시간 초과
 * 등 다양한 이유를 래핑할 수 있다.
 */
public class MailSendFailedException  extends RuntimeException {
    public MailSendFailedException (String message) {
        super(message);
    }

    public MailSendFailedException (String message, Throwable cause) {
        super(message, cause);
    }
}
