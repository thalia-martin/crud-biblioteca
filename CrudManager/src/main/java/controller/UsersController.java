package controller;

import java.io.IOException;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ModelException;
import model.PasswordUtil;
import model.User;
import model.dao.DAOFactory;
import model.dao.UserDAO;

@WebServlet(urlPatterns = {"/users", "/user/form", "/user/delete", "/user/insert", "/user/update"})
public class UsersController extends HttpServlet {

	private static final long serialVersionUID = 1L;
	private static final int MIN_PASSWORD_LENGTH = 6;

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getRequestURI();

		switch (action) {
		case "/crud-manager/user/form": {
			req.setAttribute("action", "insert");
			ControllerUtil.forward(req, resp, "/form-user.jsp");
			break;
		}
		case "/crud-manager/user/update": {
			User user = loadUser(req);

			// usuário não encontrado: volta para a lista (a mensagem de erro já foi registrada)
			if (user == null) {
				ControllerUtil.redirect(resp, req.getContextPath() + "/users");
				break;
			}

			req.setAttribute("user", user);
			req.setAttribute("action", "update");
			ControllerUtil.forward(req, resp, "/form-user.jsp");
			break;
		}
		default: {
			// ?search=termo filtra a lista; sem o parâmetro, lista todos
			listUsers(req, req.getParameter("search"));

			ControllerUtil.transferSessionMessagesToRequest(req);

			ControllerUtil.forward(req, resp, "/users.jsp");
		}
		}
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getRequestURI();

		if (action == null || action.equals("")) {
			ControllerUtil.forward(req, resp, "/index.jsp");
			return;
		}

		switch (action) {
		case "/crud-manager/user/delete":
			deleteUser(req, resp);
			break;
		case "/crud-manager/user/insert": {
			insertUser(req, resp);
			break;
		}
		case "/crud-manager/user/update": {
			updateUser(req, resp);
			break;
		}
		default:
			System.out.println("URL inválida " + action);
			break;
		}

		ControllerUtil.redirect(resp, req.getContextPath() + "/users");
	}

	private User loadUser(HttpServletRequest req) {
		String userIdParameter = req.getParameter("userId");

		int userId = Integer.parseInt(userIdParameter);

		UserDAO dao = DAOFactory.createDAO(UserDAO.class);

		try {
			User user = dao.findById(userId);

			if (user == null)
				throw new ModelException("Usuário não encontrado para alteração");

			return user;
		} catch (ModelException e) {
			// log no servidor
			e.printStackTrace();
			ControllerUtil.errorMessage(req, e.getMessage());
		}

		return null;
	}

	private void listUsers(HttpServletRequest req, String search) {
		UserDAO dao = DAOFactory.createDAO(UserDAO.class);

		List<User> users = null;
		try {
			users = (search != null && !search.isBlank())
					? dao.search(search.trim())
					: dao.listAll();
		} catch (ModelException e) {
			e.printStackTrace();
		}

		if (users == null)
			return;

		// ----- paginação -----
		int pageSize = 3;                      // quantos itens por página
		int total = users.size();              // quantos itens existem no total

		// total de páginas = itens / itens por página, arredondado para cima
		int totalPages = (int) Math.ceil((double) total / pageSize);
		if (totalPages < 1)
			totalPages = 1;

		// página pedida na URL (?page=2); se não vier ou for inválida, usa a 1
		int page = 1;
		try {
			page = Integer.parseInt(req.getParameter("page"));
		} catch (NumberFormatException e) {
			page = 1;
		}
		if (page < 1)
			page = 1;
		if (page > totalPages)
			page = totalPages;

		// quais itens aparecem: da posição "from" até "to"
		int from = (page - 1) * pageSize;
		int to = Math.min(from + pageSize, total);

		// manda para a JSP só os itens da página e os números dos botões
		req.setAttribute("users", users.subList(from, to));
		req.setAttribute("currentPage", page);
		req.setAttribute("totalPages", totalPages);
		req.setAttribute("totalItems", total);
	}
	
	
	// Copia os dados do formulário para o User (CPF só com números)
	private void fillUser(User user, HttpServletRequest req) {
		String cpf = req.getParameter("cpf");

		user.setName(req.getParameter("name").trim());
		user.setGender(req.getParameter("gender"));
		user.setEmail(req.getParameter("mail").trim());
		user.setCpf(cpf == null ? "" : cpf.replaceAll("\\D", ""));
	}

	private void insertUser(HttpServletRequest req, HttpServletResponse resp) {
		String password = req.getParameter("password");

		if (password == null || password.length() < MIN_PASSWORD_LENGTH) {
			ControllerUtil.errorMessage(req, "A senha deve ter pelo menos " + MIN_PASSWORD_LENGTH + " caracteres.");
			return;
		}

		User user = new User();
		fillUser(user, req);
		user.setPasswordHash(PasswordUtil.hash(password));

		UserDAO dao = DAOFactory.createDAO(UserDAO.class);

		try {
			if (dao.save(user)) {
				ControllerUtil.sucessMessage(req, "Usuário '" + user.getName() + "' salvo com sucesso.");
			}
			else {
				ControllerUtil.errorMessage(req, "Usuário '" + user.getName() + "' não pode ser salvo.");
			}

		} catch (ModelException e) {
			showError(req, e, "CPF ou e-mail já cadastrado.");
		}
	}

	private void updateUser(HttpServletRequest req, HttpServletResponse resp) {
		User user = loadUser(req);

		// loadUser já registrou a mensagem de erro
		if (user == null)
			return;

		fillUser(user, req);

		// senha é opcional na edição: em branco mantém a atual
		String password = req.getParameter("password");
		if (password != null && !password.isEmpty()) {
			if (password.length() < MIN_PASSWORD_LENGTH) {
				ControllerUtil.errorMessage(req, "A senha deve ter pelo menos " + MIN_PASSWORD_LENGTH + " caracteres.");
				return;
			}
			user.setPasswordHash(PasswordUtil.hash(password));
		}

		UserDAO dao = DAOFactory.createDAO(UserDAO.class);

		try {
			if (dao.update(user)) {
				ControllerUtil.sucessMessage(req, "Usuário '" + user.getName() + "' atualizado com sucesso.");
			}
			else {
				ControllerUtil.errorMessage(req, "Usuário '" + user.getName() + "' não pode ser atualizado.");
			}

		} catch (ModelException e) {
			showError(req, e, "CPF ou e-mail já cadastrado.");
		}
	}

	private void deleteUser(HttpServletRequest req, HttpServletResponse resp) {
		String userIdParameter = req.getParameter("id");

		int userId = Integer.parseInt(userIdParameter);

		UserDAO dao = DAOFactory.createDAO(UserDAO.class);

		try {
			User user = dao.findById(userId);

			if (user == null)
				throw new ModelException("Usuário não encontrado para deleção.");

			if (dao.delete(user)) {
				ControllerUtil.sucessMessage(req, "Usuário '" + user.getName() + "' deletado com sucesso.");
			}
			else {
				ControllerUtil.errorMessage(req, "Usuário '" + user.getName() + "' não pode ser deletado. Há dados relacionados ao usuário.");
			}
		} catch (ModelException e) {
			showError(req, e, "Há dados relacionados ao usuário.");
		}
	}

	// Registra o erro no servidor e mostra uma mensagem clara para o usuário
	private void showError(HttpServletRequest req, ModelException e, String integrityMessage) {
		e.printStackTrace();

		if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
			ControllerUtil.errorMessage(req, integrityMessage);
		} else {
			ControllerUtil.errorMessage(req, e.getMessage());
		}
	}
}
