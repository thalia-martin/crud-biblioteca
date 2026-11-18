<jsp:directive.page contentType="text/html; charset=UTF-8" />
<%@ taglib prefix="c" uri="http://java.sun.com/jsp/jstl/core" %>
<%@ taglib prefix="fmt" uri="http://java.sun.com/jsp/jstl/fmt" %>
<!DOCTYPE html>
<html lang="pt-br">
	<head>
		<meta charset="UTF-8" />
		<meta name="viewport" content="width=device-width, initial-scale=1.0" />
		<title>Entrar - Empréstimo de Livros</title>
		<link rel="stylesheet" href="${pageContext.request.contextPath}/css/bootstrap.min.css" />

		<style>
			body { background: #f5f5f5; }
			.login-box { max-width: 380px; margin: 80px auto 0; }
			.login-footer { margin-top: 40px; text-align: center; color: #777; }
		</style>
	</head>
	<body>
		<nav class="navbar navbar-inverse navbar-static-top">
			<div class="container">
				<span class="navbar-brand">Empréstimo de Livros</span>
			</div>
		</nav>

		<div class="container">
			<div class="login-box panel panel-default">
				<div class="panel-body">
					<h3 class="text-center">Entrar</h3>

					<c:if test="${param.erro == 'true'}">
						<div class="alert alert-danger">E-mail ou senha inválidos.</div>
					</c:if>

					<form action="${pageContext.request.contextPath}/login" method="POST">
						<div class="form-group">
							<label for="user_login_id">E-mail</label>
							<input type="email" class="form-control" id="user_login_id" name="user_login"
								   autofocus="autofocus" required />
						</div>

						<div class="form-group">
							<label for="user_pw_id">Senha</label>
							<input type="password" class="form-control" id="user_pw_id" name="user_pw" required />
						</div>

						<button type="submit" class="btn btn-primary btn-block btn-lg">Entrar</button>
					</form>
				</div>
			</div>

			<jsp:useBean id="date" class="java.util.Date" />
			<fmt:formatDate value="${date}" pattern="yyyy" var="currentYear" />
			<p class="login-footer">&copy; <c:out value="${currentYear}" /> Empréstimo de Livros</p>
		</div>
	</body>
</html>
