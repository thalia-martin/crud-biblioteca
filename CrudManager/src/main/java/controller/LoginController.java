package controller;

import java.io.IOException;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;
import model.ModelException;
import model.User;
import model.dao.DAOFactory;
import model.dao.UserDAO;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = {"/login", "/logout"})
public class LoginController extends HttpServlet {

	// Nome do atributo da sessão que guarda o usuário logado.
	// O AuthFilter usa o mesmo nome.
	public static final String LOGGED_USER = "loggedUser";

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		// /logout: encerra a sessão e volta para o login
		if (req.getRequestURI().endsWith("/logout")) {
			HttpSession session = req.getSession(false);
			if (session != null)
				session.invalidate();

			resp.sendRedirect(req.getContextPath() + "/index");
			return;
		}

		// /login: se já está logado, vai direto para os empréstimos
		HttpSession session = req.getSession(false);
		if (session != null && session.getAttribute(LOGGED_USER) != null) {
			resp.sendRedirect(req.getContextPath() + "");
			return;
		}

		// senão, mostra a página de login
		req.getRequestDispatcher("/login.jsp").forward(req, resp);
	}

	@Override
	protected void doPost(HttpServletRequest req, HttpServletResponse resp)
			throws ServletException, IOException {

		String email = req.getParameter("user_login");
		String password = req.getParameter("user_pw");

		if (email == null || email.isBlank() || password == null || password.isBlank()) {
			redirectWithError(req, resp);
			return;
		}

		UserDAO dao = DAOFactory.createDAO(UserDAO.class);

		try {
			// o DAO busca pelo e-mail e confere a senha com o hash guardado
			User user = dao.authenticate(email.trim(), password);

			if (user != null) {
				// descarta a sessão antiga e cria uma nova (evita session fixation)
				HttpSession old = req.getSession(false);
				if (old != null)
					old.invalidate();

				HttpSession session = req.getSession(true);
				session.setAttribute(LOGGED_USER, user);
				session.setMaxInactiveInterval(30 * 60); // 30 minutos sem uso

				resp.sendRedirect(req.getContextPath() + "");
				return;
			}
		} catch (ModelException e) {
			// log no servidor
			e.printStackTrace();
		}

		// e-mail inexistente, senha errada ou erro: mesma mensagem para todos
		redirectWithError(req, resp);
	}

	private void redirectWithError(HttpServletRequest req, HttpServletResponse resp) throws IOException {
		resp.sendRedirect(req.getContextPath() + "/login?erro=true");
	}
}
