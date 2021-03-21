package testiis.jdbc;

import org.apache.ibatis.jdbc.ScriptRunner;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import testiis.model.Department;

import java.io.*;
import java.sql.*;
import java.util.ArrayList;
import java.util.List;
import java.util.Objects;
import java.util.Properties;


/**
 * A class that uses jdbc to write data to a database
 *
 * @author Ilnur Yakhin
 */
public class JdbcDepartment {

    private static final String SELECT = "SELECT * FROM department";
    private static final String INSERT = "INSERT INTO department(DepCode, DepJob, Description) VALUES(?, ?, ?)";
    private static final String UPDATE = "UPDATE department SET Description = ? WHERE DepCode = ? AND DepJob = ?";
    private static final String DELETE = "DELETE FROM department WHERE DepCode = ? AND DepJob = ?";

    private static final Logger logger = LoggerFactory.getLogger(JdbcDepartment.class);

    private String url;

    /**
     * A constructor that connects to the database by url
     *
     * @see #init()
     */
    public JdbcDepartment() {
        init();
    }

    /**
     * configures the database
     *
     * @throws IOException If any IO errors occur.
     */
    public void init() {
        try (FileInputStream fileInputStream = new FileInputStream("application.properties")) {
            Properties properties = new Properties();
            properties.load(fileInputStream);
            url = (String) properties.get("URL");
            boolean isScript = Boolean.parseBoolean(properties.getProperty("script"));

            logger.info("Configuring the database");

            if (isScript)
                scriptRun(properties.getProperty("dbSql"));

        } catch (IOException e) {
            logger.error(e.toString());
            e.printStackTrace();
        }
    }


    /**
     * @return list filled in from the database
     * @throws SQLException if a database access error occurs
     *                      or this method is called on a closed connection
     */
    public List<Department> getAll() {
        List<Department> users = new ArrayList<>();
        try (PreparedStatement statement = getConnection().prepareStatement(SELECT)) {
            ResultSet rs = statement.executeQuery();
            while (rs.next()) {
                String depCode = rs.getString(2);
                String depJob = rs.getString(3);
                String description = rs.getString(4);
                users.add(new Department(depCode, depJob, description));
            }
            rs.close();
            logger.info("Select request");
        } catch (SQLException e) {
            logger.error(e.toString());
            e.printStackTrace();
        }
        return users;
    }

    /**
     * inserting into the database
     *
     * @param list stores POJO class
     * @throws SQLException if a database access error occurs
     *                      or this method is called on a closed connection
     * @see #rollback(Connection)
     */
    public void insertAll(List<Department> list) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = connection.prepareStatement(INSERT)) {
                for (Department department : list) {
                    statement.setString(1, department.getDepCode());
                    statement.setString(2, department.getDepJob());
                    statement.setString(3, department.getDescription());
                    statement.addBatch();
                }
                statement.executeBatch();
                connection.commit();
                logger.info("Insert in database");
            } catch (SQLException e) {
                rollback(connection);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * updating into the database
     *
     * @param list stores POJO class
     * @throws SQLException if a database access error occurs
     *                      or this method is called on a closed connection
     * @see #rollback(Connection)
     */
    public void updateAll(List<Department> list) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = getConnection().prepareStatement(UPDATE)) {
                for (Department department : list) {
                    statement.setString(2, department.getDepCode());
                    statement.setString(3, department.getDepJob());
                    statement.setString(1, department.getDescription());
                    statement.addBatch();
                }
                statement.executeBatch();
                connection.commit();
                logger.info("Update in database");
            } catch (SQLException e) {
                rollback(connection);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * deleting from the database
     *
     * @param list stores POJO class
     * @throws SQLException if a database access error occurs
     *                      or this method is called on a closed connection
     * @see #rollback(Connection)
     */
    public void deleteAll(List<Department> list) {
        try (Connection connection = getConnection()) {
            connection.setAutoCommit(false);
            try (PreparedStatement statement = getConnection().prepareStatement(DELETE)) {
                for (Department department : list) {
                    statement.setString(1, department.getDepCode());
                    statement.setString(2, department.getDepJob());
                    statement.addBatch();
                }
                statement.executeBatch();
                connection.commit();
                logger.info("Delete from database");
            } catch (SQLException e) {
                rollback(connection);
            }
        } catch (SQLException throwables) {
            throwables.printStackTrace();
        }
    }

    /**
     * @return connection
     * @throws SQLException if a database access error occurs
     *                      or this method is called on a closed connection
     */
    private Connection getConnection() throws SQLException {
        return DriverManager.getConnection(url);
    }

    /**
     * @param file name file for run script SQL
     * @throws FileNotFoundException if the named file does not exist,
     *                               is a directory rather than a regular file,
     *                               or for some other reason cannot be opened for
     *                               reading.
     */
    private void scriptRun(String file) throws FileNotFoundException {
        ScriptRunner runner = null;
        try {
            runner = new ScriptRunner(getConnection());
        } catch (SQLException throwables) {
            logger.error(throwables.toString());
            throwables.printStackTrace();
        }
        Objects.requireNonNull(runner).runScript(new BufferedReader(new FileReader(file)));
        logger.info("script run SQL");
    }

    /**
     * to throw an exception, rollback to old data
     *
     * @param connection
     * @throws SQLException if a database access error occurs
     *                      or this method is called on a closed connection
     */
    private void rollback(Connection connection) {
        try {
            connection.rollback();
            logger.info("rollback to old data");
        } catch (SQLException e1) {
            logger.error(e1.toString());
            e1.printStackTrace();
        }
    }

}
