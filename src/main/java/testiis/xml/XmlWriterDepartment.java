package testiis.xml;

import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import testiis.jdbc.JdbcDepartment;
import testiis.model.Department;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import java.io.FileOutputStream;
import java.io.IOException;
import java.util.List;


/**
 * CLass for writing xml file from a database
 *
 * @author Ilnur Yakhin
 */
public class XmlWriterDepartment {
    private static final Logger logger = LoggerFactory.getLogger(XmlWriterDepartment.class);

    /**
     * creates the xml
     *
     * @param file writes the file
     * @throws ParserConfigurationException if a DocumentBuilder
     *                                      cannot be created which satisfies the configuration requested.
     * @see #writeDocument(Document, String)
     */
    public void createXML(String file) {
        JdbcDepartment jdbcDepartment = new JdbcDepartment();
        List<Department> departmentList = jdbcDepartment.getAll();
        Document document = null;
        try {
            DocumentBuilderFactory dFact = DocumentBuilderFactory.newInstance();
            DocumentBuilder build = dFact.newDocumentBuilder();
            document = build.newDocument();

            Element root = document.createElement("Department");

            for (Department dep : departmentList) {
                Element tag = document.createElement("tag");

                Element depCode = document.createElement("DepCode");
                depCode.setTextContent(dep.getDepCode());

                Element depJob = document.createElement("DepJob");
                depJob.setTextContent(dep.getDepJob());

                Element description = document.createElement("Description");
                description.setTextContent(dep.getDescription());

                tag.appendChild(depCode);
                tag.appendChild(depJob);
                tag.appendChild(description);

                root.appendChild(tag);
            }
            document.appendChild(root);
        } catch (ParserConfigurationException e) {
            logger.error(e.toString());
            e.printStackTrace();
        } finally {
            if (document != null)
                writeDocument(document, file);
        }
    }

    /**
     * writes in xml file
     *
     * @param document
     * @param file     name file
     * @throws TransformerException If an unrecoverable error occurs
     *                              during the course of the transformation.
     * @throws IOException          If any IO errors occur.
     */
    private void writeDocument(Document document, String file) {
        try {
            Transformer tr = TransformerFactory.newInstance().newTransformer();
            DOMSource source = new DOMSource(document);
            FileOutputStream fos = new FileOutputStream(file);
            StreamResult result = new StreamResult(fos);
            tr.transform(source, result);
            logger.info("data has written in xml file success");
        } catch (TransformerException | IOException e) {
            logger.error(e.toString());
            e.printStackTrace(System.out);
        }
    }

}
