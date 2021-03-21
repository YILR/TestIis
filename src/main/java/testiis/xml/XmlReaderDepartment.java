package testiis.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import testiis.jdbc.JdbcDepartment;
import testiis.model.Department;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import java.io.IOException;
import java.util.*;


/**
 * Class for reading an xml file and writing to a database
 *
 * @author Ilnur Yakhin
 */
public class XmlReaderDepartment {
    private static final Logger logger = LoggerFactory.getLogger(XmlReaderDepartment.class);
    private Set<Department> departmentSet = new HashSet<>();


    /**
     *
     * @param file parses the file
     * @throws ParserConfigurationException if a DocumentBuilder
     *                                      cannot be created which satisfies the configuration requested.
     * @throws SAXException                 If any parse errors occur.
     * @throws IOException                  If any IO errors occur.
     * @see #sqlWrite()
     * @see #add(Department)
     */
    public void readXml(String file) {
        DocumentBuilder builder;
        Document document = null;
        try {
            builder = DocumentBuilderFactory.newInstance().newDocumentBuilder();
            document = builder.parse(file);
        } catch (ParserConfigurationException | SAXException | IOException e) {
            logger.error(e.toString());
            e.printStackTrace();
        }
        NodeList list = Objects.requireNonNull(document).getElementsByTagName("Department");

        Element element = (Element) list.item(0);
        String depCode;
        String depJob;
        String description;
        for (int j = 0; j < element.getElementsByTagName("tag").getLength(); j++) {
            depCode = element.getElementsByTagName("DepCode").item(j).getTextContent();
            depJob = element.getElementsByTagName("DepJob").item(j).getTextContent();
            description = element.getElementsByTagName("Description").item(j).getTextContent();
            add(new Department(depCode, depJob, description));
        }

        sqlWrite();
    }

    /**
     * Sends data to jdbс class
     */
    public void sqlWrite() {

        JdbcDepartment jdbcDepartment = new JdbcDepartment();
        List<Department> insert = new ArrayList<>();
        List<Department> update = new ArrayList<>();
        List<Department> delete = new ArrayList<>();

        for (Department depSql : jdbcDepartment.getAll()) {
            Department depXml = departmentSet.stream()
                    .filter(department -> Objects.equals(department, depSql))
                    .findFirst().orElse(null);
            if (depXml != null) {
                if (!depSql.getDescription().equals(depXml.getDescription())) {
                    update.add(depXml);
                }
                departmentSet.remove(depXml);
            } else {
                delete.add(depSql);
            }
        }

        if (!departmentSet.isEmpty())
            insert.addAll(departmentSet);
        if (update.size() > 0) {
            jdbcDepartment.updateAll(update);
        }
        if (insert.size() > 0) {
            jdbcDepartment.insertAll(insert);
        }
        if (delete.size() > 0) {
            jdbcDepartment.deleteAll(delete);
        }
    }

    /**
     * @param department POJO class
     * @throws RuntimeException if it finds two identical keys
     */
    private void add(Department department) {
        if (departmentSet.contains(department)) {
            logger.error("found two identical keys");
            throw new RuntimeException("found two identical keys");
        }
        departmentSet.add(department);
    }
}
