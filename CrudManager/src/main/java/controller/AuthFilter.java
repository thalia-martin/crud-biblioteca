package controller;

import java.io.IOException;

import jakarta.servlet.Filter;
import jakarta.servlet.FilterChain;
import jakarta.servlet.ServletException;
import jakarta.servlet.ServletRequest;
import jakarta.servlet.ServletResponse;
import jakarta.servlet.annotation.WebFilter;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import jakarta.servlet.http.HttpSession;

// Intercepta todas as URLs: sem login, só a tela de login e os arquivos
// estáticos (css, js, imagens, fontes) são liberados.
@WebFilter("/*")
public class AuthFilter implements Filter {

	// Mude para false SÓ para testar sem login. Volte para true depois!
	private static final boolean LOGIN_REQUIRED = true;

	@Override
	public void doFilter(ServletRequest request, ServletResponse response, FilterChain chain)
			throws IOException, ServletException {

		HttpServletRequest req = (HttpServletRequest) request;
		HttpServletResponse resp = (HttpServletResponse) response;

		// caminho sem o contexto: /crud-manager/loans vira /loans
		String path = req.getRequestURI().substring(req.getContextPath().length());

		HttpSession session = req.getSession(false);
		boolean loggedIn = session != null
				&& session.getAttribute(LoginController.LOGGED_USER) != null;

		boolean isStatic = isStaticResource(path);
		boolean isLoginPage = path.equals("/login") || path.equals("/login.jsp");

		if (!LOGIN_REQUIRED || isStatic || isLoginPage || loggedIn) {

			// páginas não ficam em cache: depois do logout, o botão Voltar
			// do navegador não mostra telas protegidas
			if (!isStatic) {
				resp.setHeader("Cache-Control", "no-store, no-cache, must-revalidate");
				resp.setHeader("Pragma", "no-cache");
				resp.setDateHeader("Expires", 0);
			}

			chain.doFilter(request, response);
			return;
		} else {
			resp.sendRedirect(req.getContextPath() + "/login");
		}
	}

	private boolean isStaticResource(String path) {
		String p = path.toLowerCase();

		return p.startsWith("/css/") || p.startsWith("/js/")
				|| p.startsWith("/fonts/") || p.startsWith("/img/")
				|| p.endsWith(".css") || p.endsWith(".js") || p.endsWith(".map")
				|| p.endsWith(".png") || p.endsWith(".jpg") || p.endsWith(".jpeg")
				|| p.endsWith(".gif") || p.endsWith(".svg") || p.endsWith(".ico")
				|| p.endsWith(".woff") || p.endsWith(".woff2") || p.endsWith(".ttf");
	}
}
