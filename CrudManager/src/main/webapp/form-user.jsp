<jsp:directive.page contentType="text/html; charset=UTF-8" />
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="pt-br">
	<head>
		<%@include file="base-head.jsp"%>
	</head>
	<body>
		<%@include file="nav-menu.jsp"%>

		<div id="container" class="container-fluid">
			<h3 class="page-header">${not empty user ? "Alterar Usuário" : "Adicionar Usuário"}</h3>

			<form action="${pageContext.request.contextPath}/user/${action}" method="POST">
				<input type="hidden" value="${user.id}" name="userId">

				<div class="row">
					<div class="form-group col-md-4">
						<label for="name">Nome</label>
						<input type="text" class="form-control" id="name" name="name"
							   autofocus="autofocus" placeholder="Nome do usuário"
							   required oninvalid="this.setCustomValidity('Por favor, informe o nome do usuário.')"
							   oninput="setCustomValidity('')"
							   value="<c:out value='${user.name}' />">
					</div>

					<div class="form-group col-md-4">
						<label for="gender">Sexo</label>
						<select id="gender" class="form-control selectpicker" name="gender"
							    required oninvalid="this.setCustomValidity('Por favor, informe o sexo')"
							    oninput="setCustomValidity('')">
							<option value="" disabled ${not empty user ? "" : "selected"}>Selecione o sexo</option>
							<option value="F" ${user.gender == 'F' ? "selected" : ""}>Feminino</option>
							<option value="M" ${user.gender == 'M' ? "selected" : ""}>Masculino</option>
						</select>
					</div>

					<div class="form-group col-md-4">
						<label for="mail">E-mail</label>
						<input type="email" class="form-control" id="mail" name="mail"
							   placeholder="E-mail do usuário (usado no login)"
							   required oninvalid="this.setCustomValidity('Por favor, informe um e-mail válido.')"
							   oninput="setCustomValidity('')"
							   value="<c:out value='${user.email}' />">
					</div>
				</div>

				<div class="row">
					<div class="form-group col-md-4">
						<label for="cpf">CPF</label>
						<input type="text" class="form-control" id="cpf" name="cpf"
							   maxlength="11" pattern="[0-9]{11}" inputmode="numeric"
							   placeholder="Somente números (11 dígitos)"
							   required oninvalid="this.setCustomValidity('Informe o CPF com 11 números, sem pontos e traço.')"
							   oninput="setCustomValidity('')"
							   value="<c:out value='${user.cpf}' />">
					</div>

					<div class="form-group col-md-4">
						<label for="password">Senha</label>
						<input type="password" class="form-control" id="password" name="password"
							   minlength="6" autocomplete="new-password"
							   placeholder="${empty user ? 'Mínimo de 6 caracteres' : 'Deixe em branco para manter a atual'}"
							   ${empty user ? "required" : ""}
							   oninvalid="this.setCustomValidity('Informe uma senha com no mínimo 6 caracteres.')"
							   oninput="setCustomValidity('')">
					</div>
				</div>

				<hr />
				<div id="actions" class="row pull-right">
					<div class="col-md-12">
						<a href="${pageContext.request.contextPath}/users" class="btn btn-default">Cancelar</a>
						<button type="submit" class="btn btn-primary">${not empty user ? "Alterar Usuário" : "Cadastrar Usuário"}</button>
					</div>
				</div>
			</form>
		</div>

		<script src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
		<script src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
	</body>
</html>
