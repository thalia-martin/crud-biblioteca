package model.dao;


import java.sql.Date;
import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;


import model.ModelException;
import model.Book;
import model.dao.BookDAO;



public class MySQLBookDAO implements BookDAO {


    @Override
    public boolean save(Book book) throws ModelException {
       
        DBHandler db = new DBHandler();
        //  int id, String title, int publicationYear,
        // //boolean available;
    // LocalDate registrationDate , String gender;
   
        String sqlInsert = "INSERT INTO books VALUES "
                + " (DEFAULT, ?, ?, ?, ?, ?);";
       
        db.prepareStatement(sqlInsert);
        db.setString(1, book.getTitle());
        db.setInt(2, book.getPublicationYear());
        db.setInt(3, book.isAvailable() ? 1 : 0);
        db.setDate(4, java.sql.Date.valueOf(book.getRegistrationDate()));
        db.setString(5, book.getGender());


         
        return db.executeUpdate() > 0;
    }
    @Override
    public boolean update(Book book) throws ModelException {
       
        DBHandler db = new DBHandler();
       
        String sqlUpdate = "UPDATE books "
                            + "SET title = ?, "
                            + "publication_year = ?, "
                            + "available = ?, "
                            + "registration_date = ?, "
                            + "gender = ? "
                         + "WHERE id = ?";
       
        db.prepareStatement(sqlUpdate);
       
        db.setString(1, book.getTitle());
        db.setInt(2, book.getPublicationYear());
        db.setInt(3, book.isAvailable() ? 1 : 0);
        db.setDate(4, java.sql.Date.valueOf(book.getRegistrationDate()));
        db.setString(5, book.getGender());
        db.setInt(6, book.getId());
       
        return db.executeUpdate() > 0;
    }


    @Override
    public boolean delete(Book book) throws ModelException {
       
        DBHandler db = new DBHandler();
       
        String sqlDelete = " DELETE FROM books "
                         + " WHERE id = ?;";


        db.prepareStatement(sqlDelete);    
        db.setInt(1, book.getId());
       
        try {
           
            return db.executeUpdate() > 0;
           
        } catch (ModelException e) {
            if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                return false;
            }
           
            throw e;
        }
    }


    @Override
    public List<Book> listAll() throws ModelException {
    	DBHandler db = new DBHandler();

    	List<Book> books = new ArrayList<Book>();

    	db.createStatement();
    	db.executeQuery("SELECT * FROM books ORDER BY id");

    	while (db.next()) {
    		books.add(createBook(db));
    	}

    	return books;
    }

    @Override
    public List<Book> search(String term) throws ModelException {
    	DBHandler db = new DBHandler();

    	List<Book> books = new ArrayList<Book>();

    	db.prepareStatement("SELECT * FROM books WHERE title LIKE ? ORDER BY id");
    	db.setString(1, "%" + term + "%");
    	db.executeQuery();

    	while (db.next()) {
    		books.add(createBook(db));
    	}

    	return books;
    }

    @Override
    public Book findById(int id) throws ModelException {
       
        DBHandler db = new DBHandler();
               
        String sql = "SELECT * FROM books WHERE id = ?";
       
        db.prepareStatement(sql);
        db.setInt(1, id);
        db.executeQuery();
       
        Book b = null;
        while (db.next()) {
            b = createBook(db);
            break;
        }
       
        return b;
    }
   
    //  int id, String title, int publicationYear,
        // //boolean available;
    // LocalDate registrationDate , String gender;
   
    private Book createBook(DBHandler db) throws ModelException {
        Book b = new Book(db.getInt("id"));
        b.setTitle(db.getString("title"));
        b.setPublicationYear(db.getInt("publication_year"));
        b.setRegistrationDate(((Date) db.getDate("registration_date")).toLocalDate());
        b.setGender(db.getString("gender"));
       
        return b;
    }
    
    
    @Override
    public List<Book> listPage(String term, int page, int pageSize) throws ModelException {
        DBHandler db = new DBHandler();
        List<Book> books = new ArrayList<Book>();

        db.prepareStatement("SELECT * FROM books WHERE title LIKE ? ORDER BY id LIMIT ? OFFSET ?");
        db.setString(1, "%" + term + "%");
        db.setInt(2, pageSize);
        db.setInt(3, (page - 1) * pageSize);
        db.executeQuery();

        while (db.next()) {
            books.add(createBook(db));
        }
        return books;
    }

    @Override
    public int count(String term) throws ModelException {
        DBHandler db = new DBHandler();
        db.prepareStatement("SELECT COUNT(*) AS total FROM books WHERE title LIKE ?");
        db.setString(1, "%" + term + "%");
        db.executeQuery();

        int total = 0;
        if (db.next()) {
            total = db.getInt("total");
        }
        return total;
    }

    
}



