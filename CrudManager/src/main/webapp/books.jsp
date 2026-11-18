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
			        <h3>Livros</h3>
			    </div>
			 
	    <div class="col-md-6">
	        <form action="${pageContext.request.contextPath}/books" method="get">
	            <div class="input-group h2">
	                <input name="search" class="form-control" type="text"
	                       placeholder="Pesquisar livros" value="${search}">
	                <span class="input-group-btn">
	                    <button class="btn btn-danger" type="submit">
	                        <span class="glyphicon glyphicon-search"></span>
	                    </button>
	                </span>
	            </div>
	        </form>
	    </div>
	
	    <div class="col-md-3">
	        <a href="${pageContext.request.contextPath}/book/form"
	           class="btn btn-danger pull-right h2">
	            <span class="glyphicon glyphicon-plus"></span> Adicionar Livro
	        </a>
	    </div>
	</div>
	     	</div>
	 <c:if test="${not empty param.search}">
	     		<p>
	     			${empty books ? 0 : books.size()} resultado(s) para "<c:out value="${param.search}" />"

	     		</p>
	     	</c:if>
	 
	     	<hr />
	     	
	     	<div id="list" class="row">
	     		<div class="table-responsive col-md-12">
			        <table class="table table-striped table-hover" cellspacing="0" cellpadding="0">
			            <thead>
			            
			                <tr>
			                    <th>Titulo</th>
			                    <th>Ano de Publicação</th>
			                    <th>Disponivel</th>
			                    <th>Data do Registro</th>
			                    <th>Genero</th>
			                    <th>Editar</th>
			                    <th>Excluir</th>
			                 </tr>
			            </thead>
			            <tbody>
			            	<c:forEach var="book" items="${books}">
								<tr>
				                    <td>${book.getTitle()}</td>
				                    <td>${book.getPublicationYear()}</td>
				                    <td>${book.isAvailable() ? "Sim" : "Não"}</td>		
				                    <td>${book.getRegistrationDate()}</td>			
				                    <td>${book.getGender()}</td>					                    		                    
				                    <td class="actions">
				                        <a class="btn btn-info btn-xs" 
				                           href="${pageContext.request.contextPath}/book/update?bookId=${book.getId()}" >
				                           <span class="glyphicon glyphicon-edit"></span>
				                        </a>
				                    </td>
				                    <td class="actions">
				                        <a class="btn btn-danger btn-xs modal-remove"
				                           book-id="${book.getId()}" 
				                           book-title="${book.getTitle()}" data-toggle="modal" 
				                           data-target="#delete-modal"  href="#"><span 
				                           class="glyphicon glyphicon-trash"></span></a>
				                    </td>
				                </tr>
							</c:forEach>
			            </tbody>
			         </table>
			 
			     </div>
	     	</div>
	 
	     	<div id="bottom" class="row">
			<div class="col-md-12">
				<c:set var="listUrl" value="/users" />
		

			<div id="bottom" class="row">
	<div class="col-md-12">
		<c:if test="${totalPages > 1}">
			<ul class="pagination">

				<c:if test="${currentPage > 1}">
					<li><a href="${pageContext.request.contextPath}/books?page=${currentPage - 1}&search=<c:out value='${search}' />">&lt; Anterior</a></li>
				</c:if>

				<c:forEach var="i" begin="1" end="${totalPages}">
					<li class="${i == currentPage ? 'active' : ''}">
						<a href="${pageContext.request.contextPath}/books?page=${i}&search=<c:out value='${search}' />">${i}</a>
					</li>
				</c:forEach>

				<c:if test="${currentPage < totalPages}">
					<li><a href="${pageContext.request.contextPath}/books?page=${currentPage + 1}&search=<c:out value='${search}' />">Próximo &gt;</a></li>
				</c:if>

			</ul>
		</c:if>
	</div>
</div>
		
			</div>
		</div>		
		
		</div>
		
		<script src="js/jquery.min.js"></script>
		<script src="js/bootstrap.min.js"></script>
		<script type="text/javascript">
			$(document).ready(function() {
			    // fecha o alert após 3 segundos
			    setTimeout(function() {
			        $("#alert").slideUp(500);
			    }, 3000);
			    
			    // ao clicar no delete de algum post, pega o nome do usuário, 
			    // o id do usuário e a ação (delete) e envia para o modal 
			     // o id do usuário e a ação (delete) e envia para o modal 
			    $(".modal-remove").click(function () {
		            var bookTitle = $(this).attr('book-title');
		            var bookId = $(this).attr('book-id');
		            $(".modal-body #hiddenValue").text("o livro '"+ bookTitle +"'");
		            $("#id").attr( "value", bookId);
		            $("#form").attr( "action","${pageContext.request.contextPath}/book/delete");
		        })
			});
		</script>
	</body>
</html>
