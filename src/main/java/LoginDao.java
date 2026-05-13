import java.io.IOException;
import java.io.InputStream;
import java.sql.Connection;
import java.sql.DriverManager;
import java.sql.PreparedStatement;
import java.sql.ResultSet;
import java.sql.SQLException;
import java.sql.Statement;
import java.util.ArrayList;
import java.util.Properties;
import java.util.logging.Level;
import java.util.logging.Logger;

public class LoginDao {

	private static final Logger LOG = Logger.getLogger(LoginDao.class.getName());

	private static Connection connection = null;

	private static final Properties DB = loadDatabaseProperties();

	private static Properties loadDatabaseProperties() {
		Properties p = new Properties();
		try (InputStream in = LoginDao.class.getClassLoader().getResourceAsStream("database.properties")) {
			if (in == null) {
				throw new IllegalStateException("classpath resource database.properties is missing");
			}
			p.load(in);
		} catch (IOException e) {
			throw new IllegalStateException("Could not load database.properties", e);
		}
		return p;
	}

	private static String jdbcDriver() {
		return DB.getProperty("jdbc.driver");
	}

	private static String jdbcUrl() {
		String fromEnv = System.getenv("JDBC_URL");
		if (fromEnv != null && !fromEnv.trim().isEmpty()) {
			return fromEnv.trim();
		}
		return DB.getProperty("jdbc.url");
	}

	private static String jdbcUsername() {
		return DB.getProperty("jdbc.username");
	}

	private static String jdbcPassword() {
		return DB.getProperty("jdbc.password");
	}

	public static boolean validate(String name, String pass) {
        boolean status = false;
        Connection conn = null;
        PreparedStatement pst = null;
        ResultSet rs = null;

        try {
            Class.forName(jdbcDriver());
            String url = jdbcUrl();
            conn = DriverManager.getConnection(url, jdbcUsername(), jdbcPassword());

            pst = conn.prepareStatement("SELECT * FROM EMPLOYEES WHERE USERNAME=? and PASSWORD=?");
            pst.setString(1, name);
            pst.setString(2, pass);

            rs = pst.executeQuery();
            status = rs.next();
            if (!status && name != null && !name.isEmpty()) {
				LOG.warning("LoginDao: no EMPLOYEES row matching username (wrong user/password or empty DB). jdbcUrl=" + url);
            }
        }

        catch (Exception e) {
        	LOG.log(Level.SEVERE, "LoginDao: database error during validate (check JDBC_URL / MySQL grants / network). jdbcUrl=" + jdbcUrl(), e);
        }

        finally {
        	if (rs != null) {
                try {
                    rs.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (pst != null) {
                try {
                    pst.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }

            if (conn != null) {
                try {
                    conn.close();
                } catch (SQLException e) {
                    e.printStackTrace();
                }
            }
        }

        return status;
    }

	// Create permanent connection
	public static Connection getConnection() {
        if (connection != null)
            return connection;
        else {
            try {
                Class.forName(jdbcDriver());
                connection = DriverManager.getConnection(jdbcUrl(), jdbcUsername(), jdbcPassword());

            } catch (ClassNotFoundException e) {
                e.printStackTrace();
            } catch (SQLException e) {
                e.printStackTrace();
            }
            return connection;
        }
    }

	public static ArrayList<Employee> getAllEmployees() {

		 connection = LoginDao.getConnection();
	     ArrayList<Employee> employeeList = new ArrayList<Employee>();
	     Statement statement = null;
	     ResultSet rs = null;

	        try {
	            statement = connection.createStatement();
	            rs = statement.executeQuery("SELECT * FROM EMPLOYEES LIMIT 100");

	            while(rs.next()) {
	            	Employee empl = new Employee();
	                empl.setEmployeeID(rs.getInt("EMPLOYEE_ID"));
	                empl.setName(rs.getString("NAME"));
	                empl.setPhone(rs.getString("PHONE_NUMBER"));
	                empl.setSupervisor(rs.getString("SUPERVISORS"));
	                empl.setUsername(rs.getString("USERNAME"));
	                empl.setPassword(rs.getString("PASSWORD"));
	                employeeList.add(empl);
	            }
	        }

	        catch (SQLException e) {
	            e.printStackTrace();
	        }

	        finally {
	        	if (rs != null) {
	                try {
	                    rs.close();
	                } catch (SQLException e) {
	                    e.printStackTrace();
	                }
	            }

	            if (statement != null) {
	                try {
	                	statement.close();
	                } catch (SQLException e) {
	                	e.printStackTrace();
	                }
	            }

	        }

	        return employeeList;
	    }
	public static void closeConnection() {
		if (connection != null) {
            try {
            	connection.close();
            } catch (SQLException e) {
                e.printStackTrace();
            }
        }
	}
}
