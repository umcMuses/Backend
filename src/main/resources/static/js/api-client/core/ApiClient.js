/**
 * API Client Core Module
 * 프레임워크 무관 범용 HTTP 클라이언트
 */
class ApiClient {
    /**
     * 클라이언트 자체 에러 코드 (서버 요청 전/후 발생)
     * 비즈니스 에러는 관리 X
     */
    static ErrorCode = {
        NETWORK: { code: 'CLIENT_NETWORK', message: '네트워크 연결을 확인해주세요' },
        TIMEOUT: { code: 'CLIENT_TIMEOUT', message: '요청 시간이 초과되었습니다' },
        PARSE: { code: 'CLIENT_PARSE', message: '응답 데이터 처리 중 오류가 발생했습니다' },
        UNKNOWN: { code: 'CLIENT_UNKNOWN', message: '알 수 없는 오류가 발생했습니다' }
    };

    constructor(options = {}) {
        this.baseURL = options.baseURL || '';
        this.timeout = options.timeout || 30000;
        this.headers = {
            'Content-Type': 'application/json',
            ...options.headers
        };
        
        // 콜백 훅
        this.hooks = {
            onRequest: options.onRequest || null,
            onResponse: options.onResponse || null,
            onSuccess: options.onSuccess || null,
            onError: options.onError || null,
            onFinally: options.onFinally || null
        };
        
        // 재시도 설정
        this.retry = {
            count: options.retry?.count || 0,
            delay: options.retry?.delay || 1000
        };
    }

    /**
     * 기본 요청 메서드
     */
    async request(method, url, options = {}) {
        const fullURL = this.baseURL + url;
        const controller = new AbortController();
        const timeoutId = setTimeout(() => controller.abort(), options.timeout || this.timeout);

        const config = {
            method,
            headers: { ...this.headers, ...options.headers },
            signal: controller.signal
        };

        if (options.body && method !== 'GET') {
            config.body = JSON.stringify(options.body);
        }

        if (this.hooks.onRequest) {
            await this.hooks.onRequest({ url: fullURL, ...config });
        }

        let lastError;
        const maxAttempts = (options.retry?.count ?? this.retry.count) + 1;

        for (let attempt = 1; attempt <= maxAttempts; attempt++) {
            try {
                const response = await fetch(fullURL, config);
                clearTimeout(timeoutId);

                const result = await this._handleResponse(response);

                // onResponse 훅
                if (this.hooks.onResponse) {
                    await this.hooks.onResponse(result);
                }

                if (result.success) {
                    // onSuccess 훅
                    if (this.hooks.onSuccess) {
                        await this.hooks.onSuccess(result);
                    }
                } else {
                    throw result;
                }

                // onFinally 훅
                if (this.hooks.onFinally) {
                    await this.hooks.onFinally();
                }

                return result;

            } catch (error) {
                lastError = this._normalizeError(error);
                
                if (attempt < maxAttempts) {
                    await this._delay(options.retry?.delay ?? this.retry.delay);
                    continue;
                }

                // onError 훅
                if (this.hooks.onError) {
                    await this.hooks.onError(lastError);
                }

                // onFinally 훅
                if (this.hooks.onFinally) {
                    await this.hooks.onFinally();
                }

                throw lastError;
            }
        }
    }

    /**
     * 응답 처리
     */
    async _handleResponse(response) {
        let body;
        
        try {
            body = await response.json();
        } catch (e) {
            body = null;
        }

        // 서버에서 정의한 포맷 그대로 반환
        if (body && typeof body.success === 'boolean') {
            return body;
        }

        // 포맷이 안 맞으면 래핑
        if (response.ok) {
            return {
                success: true,
                data: body
            };
        } else {
            return {
                success: false,
                error: {
                    code: 'HTTP_' + response.status,
                    message: response.statusText || '요청 처리 중 오류가 발생했습니다',
                    detail: body
                }
            };
        }
    }

    /**
     * 에러 정규화
     */
    _normalizeError(error) {
        // 이미 정규화된 에러
        if (error && error.success === false) {
            return error;
        }

        // AbortController 타임아웃
        if (error.name === 'AbortError') {
            return {
                success: false,
                error: ApiClient.ErrorCode.TIMEOUT
            };
        }

        // 네트워크 에러
        if (error instanceof TypeError) {
            return {
                success: false,
                error: {
                    ...ApiClient.ErrorCode.NETWORK,
                    detail: { originalMessage: error.message }
                }
            };
        }

        // 기타 에러
        return {
            success: false,
            error: {
                ...ApiClient.ErrorCode.UNKNOWN,
                detail: { originalMessage: error.message }
            }
        };
    }

    /**
     * 딜레이 유틸
     */
    _delay(ms) {
        return new Promise(resolve => setTimeout(resolve, ms));
    }

    // HTTP 메서드 단축
    // get(url, options) {
    //     return this.request('GET', url, options);
    // }

    // post(url, body, options = {}) {
    //     return this.request('POST', url, { ...options, body });
    // }

    // put(url, body, options = {}) {
    //     return this.request('PUT', url, { ...options, body });
    // }

    // patch(url, body, options = {}) {
    //     return this.request('PATCH', url, { ...options, body });
    // }

    // delete(url, options) {
    //     return this.request('DELETE', url, options);
    // }
}

// 브라우저 환경 전역 등록
if (typeof window !== 'undefined') {
    window.ApiClient = ApiClient;
}
