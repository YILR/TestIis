package testiis;

import testiis.xml.XmlReaderDepartment;
import testiis.xml.XmlWriterDepartment;


public class Main {

    public static void main(String[] args) {
        if(args.length == 1) {
            XmlWriterDepartment writeDepartmentXml = new XmlWriterDepartment();
            writeDepartmentXml.createXML(args[0]);
        }else if (args.length == 2){
            XmlReaderDepartment xmlReaderDepartment = new XmlReaderDepartment();
            xmlReaderDepartment.readXml(args[1]);
        }
    }
}
