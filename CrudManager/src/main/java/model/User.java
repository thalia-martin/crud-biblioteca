package model;

public class User {
	private int id;
	private String name;
	private String gender;
	private String email;
	private String cpf;

	// Hash da senha. Só é preenchido ao cadastrar ou trocar a senha.
	// Quando é null, o DAO não altera a senha que já está no banco.
	private String passwordHash;

	public User() {
		this(0);
	}

	public User(int id) {
		this.id = id;
		setName("");
		setGender("");
		setEmail("");
		setCpf("");
	}

	public String getName() {
		return name;
	}

	public void setName(String name) {
		this.name = name;
	}

	public String getGender() {
		return gender;
	}

	public void setGender(String gender) {
		this.gender = gender;
	}

	public String getEmail() {
		return email;
	}

	public void setEmail(String email) {
		this.email = email;
	}

	public String getCpf() {
		return cpf;
	}

	public void setCpf(String cpf) {
		this.cpf = cpf;
	}

	public String getPasswordHash() {
		return passwordHash;
	}

	public void setPasswordHash(String passwordHash) {
		this.passwordHash = passwordHash;
	}

	public int getId() {
		return id;
	}
}
