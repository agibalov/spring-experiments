package com.loki2302.dto;

public enum BlogServiceErrorCode {
	InternalError,
	NoSuchPost,
	NoPermissionsToAccessPost,
	BadUserNameOrPassword,
	UserAlreadyRegistered,
	ValidationError
}