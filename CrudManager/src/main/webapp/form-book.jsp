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
			<h3 class="page-header">Adicionar Livro</h3>
			<form action="${pageContext.request.contextPath}/book/${action}" method="POST">
				<input type="hidden" value="${book.getId()}" name="bookId">
				<div class="row">
					<div class="form-group col-md-4">
					<label for="name">Titulo</label>
						<input type="text" class="form-control" id="title" name="title" 
							   autofocus="autofocus" placeholder="Nome do livro" 
							   required oninvalid="this.setCustomValidity('Por favor, informe o titulo do livro.')"
							   oninput="setCustomValidity('')"
							   value="${book.getTitle()}">
					</div>
					
					<div class="form-group col-md-4">
					<label for="name">Ano de Publicação</label>
						<input type="text" class="form-control" id="publicationYear" name="publicationYear" 
							   autofocus="autofocus" placeholder="Ano de publicação " 
							   required oninvalid="this.setCustomValidity('Por favor, informe o ano de publicação do livro.')"
							   oninput="setCustomValidity('')"
							   value="${book.getPublicationYear()}">
					</div>
	
				<div class="form-group">
					<label for="available">Disponibilidade</label>
					<select class="form-control" id="available" name="available">
						<option value="true" ${empty book or book.available ? "selected" : ""}>Disponível</option>
						<option value="false" ${not empty book and not book.available ? "selected" : ""}>Indisponível</option>
					</select>
				</div>
				
				
			<div class="form-group">
				<label for="registrationDate">Data de cadastro</label>
				<input type="date" class="form-control" id="registrationDate" name="registrationDate"
					value="${book.registrationDate}" required>
			</div>
			
			<div class="form-group">
				<label for="gender">Gênero</label>
				<select class="form-control" id="gender" name="gender" required>
					<option value="" disabled ${not empty book ? "" : "selected"}>Selecione o gênero</option>
					<option value="Ficção" ${book.gender == 'Ficção' ? "selected" : ""}>Ficção</option>
					<option value="Romance" ${book.gender == 'Romance' ? "selected" : ""}>Romance</option>
					<option value="Fantasia" ${book.gender == 'Fantasia' ? "selected" : ""}>Fantasia</option>
					<option value="Terror" ${book.gender == 'Terror' ? "selected" : ""}>Terror</option>
					<option value="Biografia" ${book.gender == 'Biografia' ? "selected" : ""}>Biografia</option>
					<option value="Historia" ${book.gender == 'Historia' ? "selected" : ""}>História</option>
				</select>
			</div>
					<hr />
				<div id="actions" class="row pull-right"></div>
					<div class="col-md-12">
						<a href="${pageContext.request.contextPath}/books" class="btn btn-default">Cancelar</a>
						<button type="submit" class="btn btn-primary">${not empty book ? "Alterar Livro" : "Cadastrar Livro"}</button>
					</div>
				</div>
			</form>
		</div>

		<script src="js/jquery.min.js"></script>
		<script src="js/bootstrap.min.js"></script>
	</body>
</html>
