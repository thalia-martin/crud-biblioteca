package model.dao;

import java.sql.SQLIntegrityConstraintViolationException;
import java.util.ArrayList;
import java.util.List;

import model.ModelException;
import model.PasswordUtil;
import model.User;

public class MySQLUserDAO implements UserDAO {

	@Override
	public boolean save(User user) throws ModelException {

		DBHandler db = new DBHandler();

		// Lista as colunas para não depender da ordem delas na tabela
		String sqlInsert = "INSERT INTO users "
				+ " (nome, sexo, email, cpf, password_hash) "
				+ " VALUES (?, ?, ?, ?, ?);";

		db.prepareStatement(sqlInsert);
		db.setString(1, user.getName());
		db.setString(2, user.getGender());
		db.setString(3, user.getEmail());
		db.setString(4, user.getCpf());
		db.setString(5, user.getPasswordHash());

		return db.executeUpdate() > 0;
	}

	@Override
	public boolean update(User user) throws ModelException {

		DBHandler db = new DBHandler();

		// A senha só é alterada se uma nova foi informada
		boolean changePassword = user.getPasswordHash() != null
				&& !user.getPasswordHash().isEmpty();

		String sqlUpdate = "UPDATE users "
				+ "SET nome = ?, "
				+ "sexo = ?, "
				+ "email = ?, "
				+ "cpf = ? "
				+ (changePassword ? ", password_hash = ? " : "")
				+ "WHERE id = ?";

		db.prepareStatement(sqlUpdate);

		db.setString(1, user.getName());
		db.setString(2, user.getGender());
		db.setString(3, user.getEmail());
		db.setString(4, user.getCpf());

		int next = 5;
		if (changePassword) {
			db.setString(next++, user.getPasswordHash());
		}
		db.setInt(next, user.getId());

		return db.executeUpdate() > 0;
	}

	@Override
	public boolean delete(User user) throws ModelException {

		DBHandler db = new DBHandler();

		String sqlDelete = " DELETE FROM users "
				+ " WHERE id = ?;";

		db.prepareStatement(sqlDelete);
		db.setInt(1, user.getId());

		try {

			return db.executeUpdate() > 0;

		} catch (ModelException e) {
			// usuário com empréstimos (chave estrangeira) não pode ser excluído
			if (e.getCause() instanceof SQLIntegrityConstraintViolationException) {
				return false;
			}

			throw e;
		}
	}

	@Override
	public List<User> listAll() throws ModelException {

		DBHandler db = new DBHandler();

		List<User> users = new ArrayList<User>();

		String sqlQuery = "SELECT * FROM users ORDER BY id";

		db.createStatement();
		db.executeQuery(sqlQuery);

		while (db.next()) {
			User u = createUser(db);
			users.add(u);
		}

		return users;
	}

	
		@Override
		public List<User> search(String term) throws ModelException {

			DBHandler db = new DBHandler();

			List<User> users = new ArrayList<User>();

			String sql = "SELECT * FROM users "
					+ "WHERE nome LIKE ? OR email LIKE ? OR cpf LIKE ? "
					+ "ORDER BY id";

			String like = "%" + term + "%";

			db.prepareStatement(sql);
			db.setString(1, like);
			db.setString(2, like);
			db.setString(3, like);
			db.executeQuery();

			while (db.next()) {
				users.add(createUser(db));
			}

			return users;
		
	}

	@Override
	public User findById(int id) throws ModelException {

		DBHandler db = new DBHandler();

		String sql = "SELECT * FROM users WHERE id = ?";

		db.prepareStatement(sql);
		db.setInt(1, id);
		db.executeQuery();

		User u = null;
		while (db.next()) {
			u = createUser(db);
			break;
		}

		return u;
	}

	// Usado pelo login: devolve o usuário se e-mail e senha conferem, senão null
	@Override
	public User authenticate(String email, String password) throws ModelException {

		DBHandler db = new DBHandler();

		String sql = "SELECT * FROM users WHERE email = ?";

		db.prepareStatement(sql);
		db.setString(1, email);
		db.executeQuery();

		User user = null;

		if (db.next()) {
			String storedHash = db.getString("password_hash");

			if (storedHash != null && PasswordUtil.verify(password, storedHash)) {
				user = createUser(db);
			}
		}

		return user;
	}

	// Não lê o password_hash: o hash nunca sai do DAO
	private User createUser(DBHandler db) throws ModelException {
		User u = new User(db.getInt("id"));
		u.setName(db.getString("nome"));
		u.setGender(db.getString("sexo"));
		u.setEmail(db.getString("email"));
		u.setCpf(db.getString("cpf"));

		return u;
	}
}
