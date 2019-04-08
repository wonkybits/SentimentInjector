import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.ArrayList;

/**
 * Created by pstene on 4/3/16.
 */
public class XMLToSWArray {
    public ArrayList<ScaledWord> parseXMLToSWaArray(String filename) {
        ArrayList<ScaledWord> output = new ArrayList<ScaledWord>();
        try {
            ClassLoader classLoader = getClass().getClassLoader();
            File inputFile = new File(classLoader.getResource(filename).getFile());
            DocumentBuilderFactory dbFactory
                    = DocumentBuilderFactory.newInstance();
            DocumentBuilder dBuilder = dbFactory.newDocumentBuilder();
            Document doc = dBuilder.parse(inputFile);
            doc.getDocumentElement().normalize();
            NodeList nList = doc.getElementsByTagName("word");
            for (int i = 0; i < nList.getLength(); i++) {
                Node nNode = nList.item(i);
                if (nNode.getNodeType() == Node.ELEMENT_NODE) {
                    Element eElement = (Element) nNode;
                    output.add(new ScaledWord(eElement.getTextContent(), Double.parseDouble(eElement.getAttribute("scale"))));
                }
            }
            return output;
        }  catch (Exception e) {
            e.printStackTrace();
        }

        return output;
    }
}
