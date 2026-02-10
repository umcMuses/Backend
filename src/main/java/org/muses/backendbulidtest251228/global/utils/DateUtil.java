package org.muses.backendbulidtest251228.global.utils;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;
import java.time.format.DateTimeParseException;
import java.util.List;

import org.muses.backendbulidtest251228.global.apiPayload.code.ErrorCode;
import org.muses.backendbulidtest251228.global.businessError.BusinessException;

public class DateUtil {

	private static final List<DateTimeFormatter> BIRTHDAY_FORMATS = List.of(
		DateTimeFormatter.ofPattern("yyyy-MM-dd"),
		DateTimeFormatter.ofPattern("yyyy.MM.dd"),
		DateTimeFormatter.ofPattern("yyyy/MM/dd"),
		DateTimeFormatter.ofPattern("yyyyMMdd")
	);
	private DateUtil() {}

	public static String normalizeBirthday(String birthday) {
		if (birthday == null || birthday.isBlank()) {
			throw new BusinessException(ErrorCode.BAD_REQUEST, "생년월일은 필수입니다.");
		}
		String trimmedBirthday = birthday.trim();
		for (DateTimeFormatter fmt : BIRTHDAY_FORMATS) {
			try {
				LocalDate date = LocalDate.parse(trimmedBirthday, fmt);
				return date.toString();
			} catch (DateTimeParseException ignore) {}
		}
		throw new BusinessException(ErrorCode.BAD_REQUEST, "유효하지 않은 생년월일 형식입니다.");
	}
}
