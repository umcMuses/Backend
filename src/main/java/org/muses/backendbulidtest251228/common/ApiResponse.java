package org.muses.backendbulidtest251228.common;

import com.fasterxml.jackson.annotation.JsonInclude;
import org.muses.backendbulidtest251228.global.apiPayload.code.error.BaseErrorCode;

/**
 * 통일된 API 응답 포맷
 * 
 * 성공: { success: true, data: {...}, page: {...} }
 * 실패: { success: false, error: { code, message, detail } }
 */
@JsonInclude(JsonInclude.Include.NON_NULL)
public class ApiResponse<T> {

    private boolean success;
    private T data;
    private PageInfo page;
    private ErrorInfo error;

    // =============== 정적 팩토리 메서드 ===============

    /**
     * 성공 응답 (데이터만)
     */
    public static <T> ApiResponse<T> success(T data) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        return response;
    }

    /**
     * 성공 응답 (데이터 + 페이징)
     */
    public static <T> ApiResponse<T> success(T data, PageInfo page) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = true;
        response.data = data;
        response.page = page;
        return response;
    }

    /**
     * 실패 응답
     */
    public static <T> ApiResponse<T> fail(String code, String message) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.error = new ErrorInfo(code, message, null);
        return response;
    }

    /**
     * 실패 응답 (상세 정보 포함)
     */
    public static <T> ApiResponse<T> fail(String code, String message, Object detail) {
        ApiResponse<T> response = new ApiResponse<>();
        response.success = false;
        response.error = new ErrorInfo(code, message, detail);
        return response;
    }

    /**
     * ErrorCode enum으로 실패 응답
     */
    public static <T> ApiResponse<T> fail(ErrorCode errorCode) {
        return fail(errorCode.getCode(), errorCode.getMessage());
    }

    public static <T> ApiResponse<T> fail(ErrorCode errorCode, Object detail) {
        return fail(errorCode.getCode(), errorCode.getMessage(), detail);
    }

    public static <T> ApiResponse<T> fail(BaseErrorCode errorCode) {
        return fail(errorCode.getCode(), errorCode.getMessage());
    }

    public static <T> ApiResponse<T> fail(BaseErrorCode errorCode, Object detail) {
        return fail(errorCode.getCode(), errorCode.getMessage(), detail);
    }

    // =============== Getter ===============

    public boolean isSuccess() {
        return success;
    }

    public T getData() {
        return data;
    }

    public PageInfo getPage() {
        return page;
    }

    public ErrorInfo getError() {
        return error;
    }

    // =============== 내부 클래스 ===============

    /**
     * 페이징 정보
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class PageInfo {
        private int offset;
        private int limit;
        private long total;

        public PageInfo(int offset, int limit, long total) {
            this.offset = offset;
            this.limit = limit;
            this.total = total;
        }

        public int getOffset() { return offset; }
        public int getLimit() { return limit; }
        public long getTotal() { return total; }
    }

    /**
     * 에러 정보
     */
    @JsonInclude(JsonInclude.Include.NON_NULL)
    public static class ErrorInfo {
        private String code;
        private String message;
        private Object detail;

        public ErrorInfo(String code, String message, Object detail) {
            this.code = code;
            this.message = message;
            this.detail = detail;
        }

        public String getCode() { return code; }
        public String getMessage() { return message; }
        public Object getDetail() { return detail; }
    }
}
