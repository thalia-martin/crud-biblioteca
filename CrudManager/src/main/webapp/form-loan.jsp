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
			<h3 class="page-header">${not empty loan ? "Alterar Empréstimo" : "Adicionar Empréstimo"}</h3>

			<form action="${pageContext.request.contextPath}/loan/${action}" method="POST">
				<input type="hidden" value="${loan.getId()}" name="loanId">

				<div class="row">
					<div class="form-group col-md-6">
						<label for="userCpf">Usuário</label>
						<select id="userCpf" class="form-control selectpicker" name="userCpf"
							    autofocus="autofocus"
							    required oninvalid="this.setCustomValidity('Por favor, informe o usuário.')"
							    oninput="setCustomValidity('')">
						  <option value="" disabled ${not empty loan ? "" : "selected"}>Selecione um usuário</option>
						  <c:forEach var="user" items="${users}">
						  	<option value="${user.getCpf()}" ${loan.getUserCpf() == user.getCpf() ? "selected" : ""}>
						  		${user.getName()} - ${user.getCpf()}
						  	</option>
						  </c:forEach>
						</select>
					</div>

					<div class="form-group col-md-6">
						<label for="bookId">Livro</label>
						<select id="bookId" class="form-control selectpicker" name="bookId"
							    required oninvalid="this.setCustomValidity('Por favor, informe o livro.')"
							    oninput="setCustomValidity('')">
						  <option value="" disabled ${not empty loan ? "" : "selected"}>Selecione um livro</option>
						  <c:forEach var="book" items="${books}">
						  	<option value="${book.getId()}" ${loan.getBook().getId() == book.getId() ? "selected" : ""}>
						  		${book.getTitle()}
						  	</option>
						  </c:forEach>
						</select>
					</div>
				</div>

				<div class="row">
					<div class="form-group col-md-4">
						<label for="loanDate">Data do Empréstimo</label>
						<input type="date" class="form-control" id="loanDate" name="loanDate"
							   required oninvalid="this.setCustomValidity('Por favor, informe a data do empréstimo.')"
							   oninput="setCustomValidity('')"
							   value="${loan.getLoanDate()}">
					</div>

					<div class="form-group col-md-4">
						<label for="returnDate">Data da Devolução</label>
						<input type="date" class="form-control" id="returnDate" name="returnDate"
							   value="${loan.getReturnDate()}">
					</div>

					<div class="form-group col-md-4">
						<label for="fine">Multa (R$)</label>
						<input type="number" step="0.01" min="0" class="form-control" id="fine" name="fine"
							   placeholder="0.00"
							   value="${loan.getFine()}">
					</div>
				</div>

				<div class="row">
					<div class="form-group col-md-12">
						<label for="notes">Observação</label>
						<input type="text" class="form-control" id="notes" name="notes"
							   placeholder="Observações sobre o empréstimo"
							   value="${loan.getNotes()}">
					</div>
				</div>

				<hr />
				<div id="actions" class="row pull-right">
					<div class="col-md-12">
						<a href="${pageContext.request.contextPath}/loans" class="btn btn-default">Cancelar</a>
						<button type="submit" class="btn btn-primary">${not empty loan ? "Alterar Empréstimo" : "Criar Empréstimo"}</button>
					</div>
				</div>
			</form>
		</div>

		<script src="js/jquery.min.js"></script>
		<script src="js/bootstrap.min.js"></script>
	</body>
</html>