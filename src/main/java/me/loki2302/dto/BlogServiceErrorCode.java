package me.loki2302.dto;

public enum BlogServiceErrorCode {
	InternalError,
	NoSuchPost,
	NoPermissionsToAccessPost,
	BadUserNameOrPassword,
	UserAlreadyRegistered,
	ValidationError,
	NoSuchSession,
	SessionExpired
}