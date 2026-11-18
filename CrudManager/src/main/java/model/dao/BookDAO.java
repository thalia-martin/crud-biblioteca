package model.dao;

import java.util.List;
import model.ModelException;
import model.Book;


public interface BookDAO {
	boolean save(Book book) throws ModelException ;
	boolean update(Book book) throws ModelException;
	boolean delete(Book book) throws ModelException;
	List<Book> listPage(String term, int page, int pageSize) throws ModelException;
	List<Book> listAll() throws ModelException;
	List<Book> search(String term) throws ModelException;
	int count(String term) throws ModelException;
	Book findById(int id) throws ModelException;
}

