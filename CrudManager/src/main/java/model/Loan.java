package model;

import java.time.LocalDate;
import java.time.format.DateTimeFormatter;

public class Loan {

	private static final DateTimeFormatter BR_DATE = DateTimeFormatter.ofPattern("dd/MM/yyyy");
	
	 private int id;
	    private LocalDate loanDate;
	    private LocalDate returnDate;
	    private String notes;
	    private double fine;
	    private String userCpf;
	    private String userName;
		private Book book;
		
		


	    public Loan() {
	        this.fine = 0.0;
	    }
	    
		public Loan(int id) {
			this.id = id;
			
		}

		public int getId() {
			return id;
		}

		public void setId(int id) {
			this.id = id;
		}

		public String getLoanDateFormatted() {
			return loanDate == null ? "" : loanDate.format(BR_DATE);
		}

		public String getReturnDateFormatted() {
			return returnDate == null ? "" : returnDate.format(BR_DATE);
		}
		public LocalDate getLoanDate() {
			return loanDate;
		}

		public void setLoanDate(LocalDate loanDate) {
			this.loanDate = loanDate;
		}

		public LocalDate getReturnDate() {
			return returnDate;
		}

		public void setReturnDate(LocalDate returnDate) {
			this.returnDate = returnDate;
		}

		public String getNotes() {
			return notes;
		}

		public void setNotes(String notes) {
			this.notes = notes;
		}

		public double getFine() {
			return fine;
		}

		public void setFine(double fine) {
			this.fine = fine;
		}
		
		public Book getBook() {
			return book;
		}

		public void setBook(Book book) {
			this.book = book;
		}

		public String getUserCpf() {
			return userCpf;
		}

		public void setUserCpf(String userCpf) {
			this.userCpf = userCpf;
		}

		public String getUserName() {
			return userName;
		}

		public void setUserName(String userName) {
			this.userName = userName;
		}
		
		
		
		
}