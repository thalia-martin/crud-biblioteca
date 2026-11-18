package controller;

import java.io.IOException;
import java.time.LocalDate;
import java.time.format.DateTimeParseException;
import java.util.List;

import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.Book;
import model.Loan;
import model.ModelException;
import model.dao.DAOFactory;
import model.dao.LoanDAO;

@SuppressWarnings("serial")
@WebServlet(urlPatterns = {"/loans", "/loan/form", "/loan/delete", "/loan/insert", "/loan/update"})
public class LoansController extends HttpServlet {

	@Override
	protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
		String action = req.getRequestURI();

		switch (action) {
		case "/crud-manager/loan/form": {
			CommonsController.listUsers(req);
			CommonsController.listBooks(req);
			req.setAttribute("action", "insert");
			ControllerUtil.forward(req, resp, "/form-loan.jsp");
			break;
		}
		case "/crud-manager/loan/update": {
			CommonsController.listUsers(req);
			CommonsController.listBooks(req);
			Loan l = loadLoan(req);
			req.setAttribute("loan", l);
			req.setAttribute("action", "update");
			ControllerUtil.forward(req, resp, "/form-loan.jsp");
			break;
		}
		default: {
			// filtros opcionais: ?search=texto&startDate=2026-01-01&endDate=2026-12-31
			listLoans(req,
					req.getParameter("search"),
					parseDate(req.getParameter("startDate")),
					parseDate(req.getParameter("endDate")));

			ControllerUtil.transferSessionMessagesToRequest(req);

			ControllerUtil.forward(req, resp, "/loans.jsp");
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
		case "/crud-manager/loan/delete":
			deleteLoan(req, resp);
			break;
		case "/crud-manager/loan/insert": {
			insertLoan(req, resp);
			break;
		}
		case "/crud-manager/loan/update": {
			updateLoan(req, resp);
			break;
		}
		default:
			System.out.println("URL inválida " + action);
		}

		ControllerUtil.redirect(resp, req.getContextPath() + "/loans");
	}

	private Loan loadLoan(HttpServletRequest req) {
		String loanIdParameter = req.getParameter("loanId");

		int loanId = Integer.parseInt(loanIdParameter);

		LoanDAO dao = DAOFactory.createDAO(LoanDAO.class);

		try {
			Loan l = dao.findById(loanId);

			if (l == null)
				throw new ModelException("Empréstimo não encontrado para alteração");

			return l;
		} catch (ModelException e) {
			// log no servidor
			e.printStackTrace();
			ControllerUtil.errorMessage(req, e.getMessage());
		}

		return null;
	}

	// Converte "2026-11-16" (formato do input type="date") em LocalDate.
	// Valor vazio ou inválido vira null, ou seja, o filtro é ignorado.
	private LocalDate parseDate(String value) {
		if (value == null || value.isBlank())
			return null;

		try {
			return LocalDate.parse(value);
		} catch (DateTimeParseException e) {
			return null;
		}
	}

	// Preenche o Loan com os dados enviados pelo formulário
	private void fillLoan(Loan loan, HttpServletRequest req) {
		String userCpf = req.getParameter("userCpf");
		int bookId = Integer.parseInt(req.getParameter("bookId"));
		String loanDate = req.getParameter("loanDate");
		String returnDate = req.getParameter("returnDate");
		String notes = req.getParameter("notes");
		String fine = req.getParameter("fine");

		loan.setUserCpf(userCpf);
		loan.setBook(new Book(bookId));

		// Se a data do empréstimo vier vazia, usa a data de hoje
		loan.setLoanDate(loanDate == null || loanDate.isBlank()
				? LocalDate.now()
				: LocalDate.parse(loanDate));

		// A data de devolução é opcional (livro ainda não devolvido)
		loan.setReturnDate(returnDate == null || returnDate.isBlank()
				? null
				: LocalDate.parse(returnDate));

		loan.setNotes(notes);

		loan.setFine(fine == null || fine.isBlank()
				? 0.0
				: Double.parseDouble(fine.replace(",", ".")));
	}

	private void updateLoan(HttpServletRequest req, HttpServletResponse resp) {
		Loan loan = loadLoan(req);

		if (loan == null)
			return;

		fillLoan(loan, req);

		LoanDAO dao = DAOFactory.createDAO(LoanDAO.class);

		try {
			if (dao.update(loan)) {
				ControllerUtil.sucessMessage(req, "Empréstimo #" + loan.getId() + " atualizado com sucesso.");
			} else {
				ControllerUtil.errorMessage(req, "Empréstimo #" + loan.getId() + " não pode ser atualizado.");
			}
		} catch (ModelException e) {
			// log no servidor
			e.printStackTrace();
			ControllerUtil.errorMessage(req, e.getMessage());
		}
	}

	private void insertLoan(HttpServletRequest req, HttpServletResponse resp) {
		Loan loan = new Loan();
		fillLoan(loan, req);

		LoanDAO dao = DAOFactory.createDAO(LoanDAO.class);

		try {
			if (dao.save(loan)) {
				ControllerUtil.sucessMessage(req, "Empréstimo salvo com sucesso.");
			} else {
				ControllerUtil.errorMessage(req, "Empréstimo não pode ser salvo.");
			}

		} catch (ModelException e) {
			// log no servidor
			e.printStackTrace();
			ControllerUtil.errorMessage(req, e.getMessage());
		}
	}

	private void deleteLoan(HttpServletRequest req, HttpServletResponse resp) {
		String loanIdParameter = req.getParameter("id");

		int loanId = Integer.parseInt(loanIdParameter);

		LoanDAO dao = DAOFactory.createDAO(LoanDAO.class);

		try {
			Loan l = dao.findById(loanId);

			if (l == null)
				throw new ModelException("Empréstimo não encontrado para deleção");

			if (dao.delete(l)) {
				ControllerUtil.sucessMessage(req, "Empréstimo #" + l.getId() + " deletado com sucesso.");
			} else {
				ControllerUtil.errorMessage(req, "Empréstimo #" + l.getId() + " não pode ser deletado.");
			}
		} catch (ModelException e) {
			// log no servidor
			e.printStackTrace();
			ControllerUtil.errorMessage(req, e.getMessage());
		}
	}

	private void listLoans(HttpServletRequest req, String search, LocalDate startDate, LocalDate endDate) {
		LoanDAO dao = DAOFactory.createDAO(LoanDAO.class);

		// se o usuário informou as datas invertidas, troca a ordem
		if (startDate != null && endDate != null && startDate.isAfter(endDate)) {
			LocalDate tmp = startDate;
			startDate = endDate;
			endDate = tmp;
		}

		boolean filtered = (search != null && !search.isBlank())
				|| startDate != null
				|| endDate != null;

		List<Loan> loans = null;
		try {
			loans = filtered
					? dao.search(search, startDate, endDate)
					: dao.listAll();
		} catch (ModelException e) {
			e.printStackTrace();
		}

		// se deu erro no banco, não há o que paginar
		if (loans == null)
			return;

		// ----- paginação -----
		int pageSize = 3;                      // quantos itens por página
		int total = loans.size();              // quantos itens existem no total

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
		req.setAttribute("loans", loans.subList(from, to));
		req.setAttribute("currentPage", page);
		req.setAttribute("totalPages", totalPages);
		req.setAttribute("totalItems", total);
	}
	}