<jsp:directive.page contentType="text/html; charset=UTF-8" />
<%@ taglib prefix = "c" uri = "http://java.sun.com/jsp/jstl/core" %>
<!DOCTYPE html>
<html lang="pt-br">
	<head>
		<%@include file="base-head.jsp"%>
	</head>
	<body>
		<%@include file="modal.html"%>
		<%@include file="nav-menu.jsp"%>

		<div id="container" class="container-fluid">
			<div id="alert" style="${not empty message ? 'display: block;' : 'display: none;'}" class="alert alert-dismissable ${alertType eq 1 ? 'alert-success' : 'alert-danger'}">
			  <a href="#" class="close" data-dismiss="alert" aria-label="close">&times;</a>
			  ${message}
			</div>

	 	 	<div id="top" class="row">
	 			<div class="col-md-3">
			        <h3>Empréstimos</h3>
			    </div>
			 
	    <div class="col-md-6">
	        <form action="${pageContext.request.contextPath}/loans" method="get">
	            <div class="input-group h2">
	                <input name="search" class="form-control" type="text"
	                       placeholder="Pesquisar empréstimos" value="${search}">
	                <span class="input-group-btn">
	                    <button class="btn btn-danger" type="submit">
	                        <span class="glyphicon glyphicon-search"></span>
	                    </button>
	                </span>
	            </div>
	        </form>
	    </div>
	
	    <div class="col-md-3">
	        <a href="${pageContext.request.contextPath}/loan/form"
	           class="btn btn-danger pull-right h2">
	            <span class="glyphicon glyphicon-plus"></span> Adicionar Empréstimos
	        </a>
	    </div>
	</div>
	     	</div>
	 <c:if test="${not empty param.search}">
	     		<p>
	     			${empty loans ? 0 : loans.size()} resultado(s) para "<c:out value="${param.search}" />"

	     		</p>
	     	</c:if>
	 
	     	<hr />

	     	<div id="list" class="row">
	     		<div class="table-responsive col-md-12">
	     			<p>Quantidade de Empréstimos: ${empty loans ? 0 : loans.size()}</p>

			        <table class="table table-striped table-hover" cellspacing="0" cellpadding="0">
			            <thead>
			                <tr>
			                    <th>Data do Empréstimo</th>
			                    <th>Data da Devolução</th>
			                    <th>Observação</th>
			                    <th>Multa</th>
			                    <th>Nome do Usuário</th>
			                    <th>Título do Livro</th>
			                    <th>Editar</th>
			                    <th>Excluir</th>
			                 </tr>
			            </thead>
			            <tbody>
			            	<c:forEach var="loan" items="${loans}">
						    <tr>
						        <td>${loan.loanDateFormatted}</td>
						        <td>${loan.returnDateFormatted}</td>
						        <td><c:out value="${loan.notes}" /></td>
						        <td>${loan.fine}</td>
						        <td><c:out value="${loan.userName}" /></td>
						        <td><c:out value="${loan.book.title}" /></td>
						        <td class="actions">
						            <a class="btn btn-info btn-xs"
						               href="${pageContext.request.contextPath}/loan/update?loanId=${loan.id}">
						                <span class="glyphicon glyphicon-edit"></span>
						            </a>
						        </td>
						        <td class="actions">
						            <a class="btn btn-danger btn-xs modal-remove"
						               loan-id="${loan.id}"
						               loan-name="<c:out value='${loan.userName}' />"
						               data-toggle="modal" data-target="#delete-modal" href="#">
						                <span class="glyphicon glyphicon-trash"></span>
						            </a>
						        </td>
						    </tr>
						</c:forEach>

						<c:if test="${empty loans}">
							<tr>
								<td colspan="8" class="text-center">Nenhum empréstimo encontrado.</td>
							</tr>
						</c:if>
			            </tbody>
			         </table>

			     </div>
	     	</div>

	     	<div id="bottom" class="row">
	<div class="col-md-12">
		<c:if test="${totalPages > 1}">

			<%-- filtros que acompanham cada link de página --%>
			<c:set var="filtros">&search=<c:out value='${param.search}' />&startDate=<c:out value='${param.startDate}' />&endDate=<c:out value='${param.endDate}' /></c:set>

			<ul class="pagination">

				<c:if test="${currentPage > 1}">
					<li><a href="${pageContext.request.contextPath}/loans?page=${currentPage - 1}${filtros}">&lt; Anterior</a></li>
				</c:if>

				<c:forEach var="i" begin="1" end="${totalPages}">
					<li class="${i == currentPage ? 'active' : ''}">
						<a href="${pageContext.request.contextPath}/loans?page=${i}${filtros}">${i}</a>
					</li>
				</c:forEach>

				<c:if test="${currentPage < totalPages}">
					<li><a href="${pageContext.request.contextPath}/loans?page=${currentPage + 1}${filtros}">Próximo &gt;</a></li>
				</c:if>

			</ul>
		</c:if>
	</div>
</div>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/jquery.min.js"></script>
		<script type="text/javascript" src="${pageContext.request.contextPath}/js/bootstrap.min.js"></script>
		<script type="text/javascript">
			$(document).ready(function() {
			    // fecha o alert após 3 segundos
			    setTimeout(function() {
			        $("#alert").slideUp(500);
			    }, 3000);

			    // ao clicar em excluir, pega o nome do usuário e o id do empréstimo
			    // e envia para o modal junto com a ação (delete)
			    $(".modal-remove").click(function () {
			        $(".modal-body #hiddenValue").text("o empréstimo de '" + $(this).attr('loan-name') + "'");
			        $("#id").attr("value", $(this).attr('loan-id'));
			        $("#form").attr("action", "${pageContext.request.contextPath}/loan/delete");
			    });

			});
		</script>
	</body>
</html>
