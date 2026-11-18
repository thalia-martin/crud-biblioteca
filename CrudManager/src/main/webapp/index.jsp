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

				     <main class="flex-grow-1 d-flex flex-column justify-content-center align-items-center text-center">
          <h1 class="my-4">Bem-vindo ao CRUD Manager Biblioteca</h1>
          <div class="d-flex gap-3 flex-wrap justify-content-center">
          
            <a href="${pageContext.request.contextPath}/users" class="btn btn-secondary btn-lg">Usuários</a>
             <a href="${pageContext.request.contextPath}/posts" class="btn btn-secondary btn-lg">Posts</a>
            <a href="${pageContext.request.contextPath}/books" class="btn btn-secondary btn-lg">Livros</a>
             <a href="${pageContext.request.contextPath}/loans" class="btn btn-secondary btn-lg">Emprestimos</a>
          </div>
        </main> 
        	 
	     	<div id="bottom" class="row">
	     		<div class="col-md-12">
			        <ul class="pagination">
			            <li class="disabled"><a>&lt; Anterior</a></li>
			            <li class="disabled"><a>1</a></li>
			            <li><a href="#">2</a></li>
			            <li><a href="#">3</a></li>
			            <li class="next"><a href="#" rel="next">Próximo &gt;</a></li>
			        </ul><!-- /.pagination -->
			    </div>
	     	</div>
		</div>
		
		<script type="text/javascript" src="js/jquery.min.js"></script>
		<script type="text/javascript" src="js/bootstrap.min.js"></script>
		<script type="text/javascript">
			$(document).ready(function() {
			    // fecha o alert após 3 segundos
			    setTimeout(function() {
			        $("#alert").slideUp(500);
			    }, 3000);
			    
			    // ao clicar no delete de algum post, pega o nome do usuário, 
			    // o id do usuário e a ação (delete) e envia para o modal 
			    $(".modal-remove").click(function () {
		            var postContent = $(this).attr('post-content');
		            var postId = $(this).attr('post-id');
		            $(".modal-body #hiddenValue").text("o post '"+postContent+"'");
		            $("#id").attr( "value", postId);
		            $("#form").attr( "action","post/delete");
		        })
			});
		</script>
	</body>
</html>
