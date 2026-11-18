package model.dao;

import java.time.LocalDate;
import java.util.List;
import model.ModelException;
import model.Loan;


public interface LoanDAO {
	boolean save(Loan loan) throws ModelException ;
	boolean update(Loan loan) throws ModelException;
	boolean delete(Loan loan) throws ModelException;
	List<Loan> listAll() throws ModelException;
	List<Loan> search(String term, LocalDate startDate, LocalDate endDate) throws ModelException;
	Loan findById(int id) throws ModelException;
}

