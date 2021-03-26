package testiis.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import testiis.jdbc.DepartmentDao;
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
public class XmlReader {
    private static final Logger logger = LoggerFactory.getLogger(XmlReader.class);
    private Map<Department, Integer> departmentMap = new HashMap();


    /**
     * @param file parses the file
     * @throws ParserConfigurationException if a DocumentBuilder
     *                                      cannot be created which satisfies the configuration requested.
     * @throws SAXException                 If any parse errors occur.
     * @throws IOException                  If any IO errors occur.
     * @see #sqlWrite()
     * @see #validationKey(Department)
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
            validationKey(new Department(depCode, depJob, description));
        }
        sqlWrite();
    }


    /**
     * Sends data to jdbс class
     */
    public void sqlWrite() {

        DepartmentDao departmentDao = new DepartmentDao();
        List<Department> insertList = new ArrayList<>();
        List<Department> updateList = new ArrayList<>();
        List<Department> deleteList = new ArrayList<>();

        for (Department depSql : departmentDao.getAll()) {
            Department depXml = departmentMap.keySet().stream()
                    .filter(department -> Objects.equals(department, depSql))
                    .findFirst().orElse(null);
            if (depXml != null) {
                if (!depSql.getDescription().equals(depXml.getDescription())) {
                    updateList.add(depXml);
                }
                departmentMap.remove(depXml);
            } else {
                deleteList.add(depSql);
            }
        }

        if (!departmentMap.isEmpty())
            insertList.addAll(departmentMap.keySet());

        if (!updateList.isEmpty()) {
            departmentDao.updateAll(updateList);
        }
        if (!insertList.isEmpty()) {
            departmentDao.insertAll(insertList);
        }
        if (!deleteList.isEmpty()) {
            departmentDao.deleteAll(deleteList);
        }
        departmentDao.commitAndClose();
    }


    /**
     * @param department POJO class
     * @throws RuntimeException if it finds two identical keys
     */
    private void validationKey(Department department) {
        if (departmentMap.containsKey(department)) {
            logger.error("found two identical keys");
            throw new RuntimeException("found two identical keys");
        }
        departmentMap.put(department, 0);
    }
}
