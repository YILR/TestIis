package testiis;

import testiis.xml.XmlReader;
import testiis.xml.XmlWriter;


public class Main {

    public static void main(String[] args) {
        if(args.length == 1) {
            XmlWriter xmlWriter = new XmlWriter();
            xmlWriter.createXML(args[0]);
        }else if (args.length == 2){
            if(args[0].equals("sync")) {
                XmlReader xmlReader = new XmlReader();
                xmlReader.readXml(args[1]);
            }
        }
    }
}
