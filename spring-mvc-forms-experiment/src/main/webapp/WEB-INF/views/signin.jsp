<%@ page isELIgnored="false" %>
<%@ page contentType="text/html;charset=UTF-8" language="java" session="false" %>
<%@ taglib uri="http://www.springframework.org/tags/form" prefix="form" %>
<%@ taglib uri="http://www.springframework.org/tags" prefix="spring" %>
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>

<h1>Sign in</h1>
<form:form method="post" commandName="signInModel" action="/sign-in" cssClass="form-horizontal">
	<spring:bind path="email">
		<div class="control-group <%= status.isError() ? "error" : "" %>">
			<label class="control-label">Email</label>
			<div class="controls">
				<form:input path="email" />
				<c:if test="${status.error}">
					<span class="help-inline">
						${status.errorMessage}
					</span>
				</c:if>				
			</div>
		</div>
	</spring:bind>
	<spring:bind path="password">
		<div class="control-group <%= status.isError() ? "error" : "" %>">
			<label class="control-label">Password</label>
			<div class="controls">
				<form:password path="password" />
				<c:if test="${status.error}">
					<span class="help-inline">
						${status.errorMessage}
					</span>
				</c:if>
			</div>
		</div>	
	</spring:bind>
	<div class="control-group">
		<div class="controls">
			<button class="btn btn-large" type="submit">Sign In</button>
		</div>
	</div>
	<c:if test="${not empty message}">
		<div class="alert alert-error">
			${message}
		</div>
	</c:if>
</form:form>
<p>Use <code>a@a</code> as email, to get error message</p>
