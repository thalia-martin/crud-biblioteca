package model.dao;

import java.sql.Date;
import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;

import model.Book;
import model.Loan;
import model.ModelException;

public class MySQLLoanDAO implements LoanDAO {

	// Base das consultas: traz o nome do usuário pelo CPF
	private static final String BASE_SELECT =
			  " SELECT l.*, u.nome AS user_name "
			+ " FROM loans l "
			+ " INNER JOIN users u ON l.user_cpf = u.cpf ";

	@Override
	public boolean save(Loan loan) throws ModelException {

		DBHandler db = new DBHandler();

		String sqlInsert = "INSERT INTO loans "
				+ " (user_cpf, loan_date, return_date, notes, fine, book_id) "
				+ " VALUES (?, ?, ?, ?, ?, ?);";

		db.prepareStatement(sqlInsert);
		db.setString(1, loan.getUserCpf());
		db.setDate(2, Date.valueOf(loan.getLoanDate()));

		if (loan.getReturnDate() != null) {
			db.setDate(3, Date.valueOf(loan.getReturnDate()));
		} else {
			db.setNullDate(3);
		}

		db.setString(4, loan.getNotes());
		db.setDouble(5, loan.getFine());
		db.setInt(6, loan.getBook().getId());

		return db.executeUpdate() > 0;
	}

	@Override
	public boolean update(Loan loan) throws ModelException {

		DBHandler db = new DBHandler();

		String sqlUpdate = "UPDATE loans "
				+ " SET user_cpf = ?,"
				+ " loan_date = ?,"
				+ " return_date = ?,"
				+ " notes = ?,"
				+ " fine = ?,"
				+ " book_id = ?"
				+ " WHERE id = ?";

		db.prepareStatement(sqlUpdate);

		db.setString(1, loan.getUserCpf());
		db.setDate(2, Date.valueOf(loan.getLoanDate()));

		if (loan.getReturnDate() != null) {
			db.setDate(3, Date.valueOf(loan.getReturnDate()));
		} else {
			db.setNullDate(3);
		}

		db.setString(4, loan.getNotes());
		db.setDouble(5, loan.getFine());
		db.setInt(6, loan.getBook().getId());
		db.setInt(7, loan.getId());

		return db.executeUpdate() > 0;
	}

	@Override
	public boolean delete(Loan loan) throws ModelException {

		DBHandler db = new DBHandler();

		String sqlDelete = " DELETE FROM loans "
				+ " WHERE id = ?;";

		db.prepareStatement(sqlDelete);
		db.setInt(1, loan.getId());

		return db.executeUpdate() > 0;
	}

	@Override
	public List<Loan> listAll() throws ModelException {

		DBHandler db = new DBHandler();

		List<Loan> loans = new ArrayList<Loan>();

		String sqlQuery = BASE_SELECT + " ORDER BY l.loan_date DESC";

		db.createStatement();
		db.executeQuery(sqlQuery);

		while (db.next()) {
			Loan l = createLoan(db);
			loans.add(l);
		}

		return loans;
	}

	// Pesquisa com filtros opcionais: texto (livro, usuário ou CPF) e
	// período da data do empréstimo. Qualquer filtro pode ser null/vazio.
	@Override
	public List<Loan> search(String term, LocalDate startDate, LocalDate endDate) throws ModelException {

		DBHandler db = new DBHandler();

		List<Loan> loans = new ArrayList<Loan>();

		boolean hasTerm = term != null && !term.isBlank();

		StringBuilder sql = new StringBuilder(BASE_SELECT);
		sql.append(" INNER JOIN books b ON l.book_id = b.id ");
		sql.append(" WHERE 1 = 1 ");

		if (hasTerm) {
			sql.append(" AND (b.title LIKE ? OR u.nome LIKE ? OR l.user_cpf LIKE ?) ");
		}
		if (startDate != null) {
			sql.append(" AND l.loan_date >= ? ");
		}
		if (endDate != null) {
			sql.append(" AND l.loan_date <= ? ");
		}

		sql.append(" ORDER BY l.loan_date DESC");

		db.prepareStatement(sql.toString());

		// os parâmetros entram na mesma ordem em que os filtros foram montados
		int i = 1;

		if (hasTerm) {
			String like = "%" + term.trim() + "%";
			db.setString(i++, like);
			db.setString(i++, like);
			db.setString(i++, like);
		}
		if (startDate != null) {
			db.setDate(i++, Date.valueOf(startDate));
		}
		if (endDate != null) {
			db.setDate(i++, Date.valueOf(endDate));
		}

		db.executeQuery();

		while (db.next()) {
			Loan l = createLoan(db);
			loans.add(l);
		}

		return loans;
	}

	@Override
	public Loan findById(int id) throws ModelException {

		DBHandler db = new DBHandler();

		String sql = BASE_SELECT + " WHERE l.id = ?;";

		db.prepareStatement(sql);
		db.setInt(1, id);
		db.executeQuery();

		Loan l = null;
		while (db.next()) {
			l = createLoan(db);
			break;
		}

		return l;
	}

	private Loan createLoan(DBHandler db) throws ModelException {
		Loan l = new Loan(db.getInt("id"));
		l.setUserCpf(db.getString("user_cpf"));
		l.setUserName(db.getString("user_name"));
		l.setLoanDate(((Date) db.getDate("loan_date")).toLocalDate());

		Date returnDate = (Date) db.getDate("return_date");
		if (returnDate != null) {
			l.setReturnDate(returnDate.toLocalDate());
		}

		l.setNotes(db.getString("notes"));
		l.setFine(db.getDouble("fine"));

		BookDAO bookDAO = DAOFactory.createDAO(BookDAO.class);
		Book book = bookDAO.findById(db.getInt("book_id"));
		l.setBook(book);

		return l;
	}
}
