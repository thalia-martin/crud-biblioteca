package controller;


import java.io.IOException;

import java.util.List;
import java.time.LocalDate;


import jakarta.servlet.ServletException;
import jakarta.servlet.annotation.WebServlet;
import jakarta.servlet.http.HttpServlet;
import jakarta.servlet.http.HttpServletRequest;
import jakarta.servlet.http.HttpServletResponse;
import model.ModelException;
import model.Book;
import model.dao.DAOFactory;
import model.dao.BookDAO;


import java.sql.SQLIntegrityConstraintViolationException;


@WebServlet(urlPatterns = {"/books", "/book/form", "/book/delete", "/book/insert", "/book/update"})
public class BooksController extends HttpServlet {


    private static final long serialVersionUID = 1L;


    @Override
    protected void doGet(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getServletPath();
       
        switch (action) {
        case "/book/form": {
            listBooks(req);
            req.setAttribute("action", "insert");
            ControllerUtil.forward(req, resp, "/form-book.jsp");
            break;
        }
        case "/book/update": {
        //    listBooks(req);
            Book book = loadBook(req);
            req.setAttribute("book", book);
            req.setAttribute("action", "update");
            ControllerUtil.forward(req, resp, "/form-book.jsp");
            break;
        }
        default:
            listBooks(req);
           
            ControllerUtil.transferSessionMessagesToRequest(req);
       
            ControllerUtil.forward(req, resp, "/books.jsp");
        }
    }
   
    @Override
    protected void doPost(HttpServletRequest req, HttpServletResponse resp) throws ServletException, IOException {
        String action = req.getServletPath();
       
        if (action == null || action.equals("") ) {
            ControllerUtil.forward(req, resp, "/index.jsp");
            return;
        }
       
        switch (action) {
        case "/book/delete":
            deleteBook(req, resp);
            break;  
        case "/book/insert": {
            insertBook(req, resp);
            break;
        }
        case "/book/update": {
            updateBook(req, resp);
            break;
        }
        default:
            System.out.println("URL inválida " + action);
            break;
        }
           
        ControllerUtil.redirect(resp, req.getContextPath() + "/books");
    }


    private Book loadBook(HttpServletRequest req) {
        String bookIdParameter = req.getParameter("bookId");
       
        int bookId = Integer.parseInt(bookIdParameter);
       
        BookDAO dao = DAOFactory.createDAO(BookDAO.class);
       
        try {
            Book book = dao.findById(bookId);
           
            if (book == null)
                throw new ModelException("Livro não encontrado para alteração");
           
            return book;
        } catch (ModelException e) {
            // log no servidor
            e.printStackTrace();
            ControllerUtil.errorMessage(req, e.getMessage());
        }
       
        return null;
    }
   
    private void listBooks(HttpServletRequest req) {
        BookDAO dao = DAOFactory.createDAO(BookDAO.class);

        String search = req.getParameter("search");
        if (search == null) search = "";
        search = search.trim();

        req.setAttribute("search", search);
        req.setAttribute("baseUrl", "/books");

        List<Book> books = null;
        try {
            books = !search.isEmpty()
                    ? dao.search(search)
                    : dao.listAll();
        } catch (ModelException e) {
            e.printStackTrace();
        }

        // se deu erro no banco, não há o que paginar
        if (books == null)
            return;

        // ----- paginação -----
        int pageSize = 3;                      // quantos itens por página
        int total = books.size();              // quantos itens existem no total

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
        req.setAttribute("books", books.subList(from, to));
        req.setAttribute("currentPage", page);
        req.setAttribute("totalPages", totalPages);
        req.setAttribute("totalItems", total);
        
    }
    //  int id, String title, int publicationYear,
        // //boolean available;
    // LocalDate registrationDate , String gender;


    private void insertBook(HttpServletRequest req, HttpServletResponse resp) {
        String bookTitle = req.getParameter("title");
        int bookPublicationYear = Integer.parseInt(req.getParameter("publicationYear"));
        LocalDate bookRegistrationDate = LocalDate.parse(req.getParameter("registrationDate"));
        boolean bookAvailable = req.getParameter("available") != null;
        String bookGender = req.getParameter("gender");

        Book book = new Book();
        book.setTitle(bookTitle);
        book.setPublicationYear(bookPublicationYear);
        book.setRegistrationDate(bookRegistrationDate);
        book.setAvailable(bookAvailable);
        book.setGender(bookGender);
        
      //  System.out.println("Gender"+req.getParameter("gender"));

       
        BookDAO dao = DAOFactory.createDAO(BookDAO.class);
       
        try {
            if (dao.save(book)) {
                ControllerUtil.sucessMessage(req, "Livro '" + book.getTitle() + "' salvo com sucesso.");
            }
            else {
                ControllerUtil.errorMessage(req, "Livro '" + book.getTitle() + "' não pode ser salvo.");
            }
               
        } catch (ModelException e) {
            // log no servidor
            e.printStackTrace();
            ControllerUtil.errorMessage(req, e.getMessage());
        }
    }
    
    //  int id, String title, int publicationYear,
    // //boolean available;
// LocalDate registrationDate , String gender;

   
    private void updateBook(HttpServletRequest req, HttpServletResponse resp) {
        String bookTitle = req.getParameter("title");
                
        int bookPublicationYear = Integer.parseInt(req.getParameter("publicationYear"));
        LocalDate bookRegistrationDate = LocalDate.parse(req.getParameter("registrationDate"));
        boolean bookAvailable = req.getParameter("available") != null;
        String bookGender = req.getParameter("gender");

        Book book = loadBook(req);
        
        book.setTitle(bookTitle);
        book.setPublicationYear(bookPublicationYear);
        book.setRegistrationDate(bookRegistrationDate);
        book.setAvailable(bookAvailable);
        book.setGender(bookGender);
       
        BookDAO dao = DAOFactory.createDAO(BookDAO.class);
       
        try {
            if (dao.update(book)) {
                ControllerUtil.sucessMessage(req, "Livro '" + book.getTitle()  + "' atualizado com sucesso.");
            }
            else {
                ControllerUtil.errorMessage(req, "Livro '" + book.getTitle()  + "' não pode ser atualizado.");
            }
               
        } catch (ModelException e) {
            // log no servidor
            e.printStackTrace();
            ControllerUtil.errorMessage(req, e.getMessage());
        }
    }
   
    private void deleteBook(HttpServletRequest req, HttpServletResponse resp) {
        String booksIdParameter = req.getParameter("id");
       
        int bookId = Integer.parseInt(booksIdParameter);
       
        BookDAO dao = DAOFactory.createDAO(BookDAO.class);
       
        try {
            Book book = dao.findById(bookId);
           
            if (book == null)
                throw new ModelException("Livro não encontrado para deleção.");
           
            if (dao.delete(book)) {
                ControllerUtil.sucessMessage(req, "Livro '" + book.getTitle() + "' deletado com sucesso.");
            }
            else {
                ControllerUtil.errorMessage(req, "Livro '" + book.getTitle()  + "' não pode ser deletado. Há dados relacionados ao usuário.");
            }
        } catch (ModelException e) {
            // log no servidor
            if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
                ControllerUtil.errorMessage(req, e.getMessage());
            }
            e.printStackTrace();
            ControllerUtil.errorMessage(req, e.getMessage());
        }
    }
}





