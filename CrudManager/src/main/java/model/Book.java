package model;

import java.time.LocalDate;

public class Book {
	private int id;
	private String title;
	private int publicationYear;
	private boolean available;
	private LocalDate registrationDate;
	private String gender;
	
	public Book() {
		this(0);
	}
	
	public Book(int id) {
		this.id = id;
		setTitle("");
		setPublicationYear(0);
		setAvailable(false);
		setGender("");
	}

	public int getId() {
		return id;
	}

	public String getTitle() {
		return title;
	}

	public void setTitle(String title) {
		this.title = title;
	}

	public int getPublicationYear() {
		return publicationYear;
	}

	public void setPublicationYear(int publicationYear) {
		this.publicationYear = publicationYear;
	}

	public boolean isAvailable() {
		return available;
	}

	public void setAvailable(boolean available) {
		this.available = available;
	}

	public LocalDate getRegistrationDate() {
		return registrationDate;
	}

	public void setRegistrationDate(LocalDate registrationDate) {
		this.registrationDate = registrationDate;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}
}