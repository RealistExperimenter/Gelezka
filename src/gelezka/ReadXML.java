package gelezka;
/**
 * Created by Coder on 21-Sep-15.
 */

import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.NodeList;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import java.io.File;
import java.util.LinkedList;
import java.util.List;

public class ReadXML {

    String userName,userPassword;
    List<String> listOfLinks = new LinkedList<String>();

    public String getUserName(){
        return userName;
    }

    public String getUserPassword(){
        return userPassword;
    }

    public List<String> getListOfLinks(){
        return listOfLinks;
    }

     ReadXML() {

        File fXml = new File("my_settings.xml");


        try {
            DocumentBuilderFactory dbf = DocumentBuilderFactory.newInstance();
            DocumentBuilder db = dbf.newDocumentBuilder();
            Document doc = db.parse(fXml);

            doc.getDocumentElement().normalize();

            Element user = doc.getDocumentElement();
            NodeList uName = user.getElementsByTagName("user_name");
            NodeList uPass = user.getElementsByTagName("user_password");
            NodeList postsLinks = user.getElementsByTagName("posts_links");

            userName = uName.item(0).getTextContent();
            userPassword = uPass.item(0).getTextContent();

            for (int i = 0; i != postsLinks.getLength();i++) listOfLinks.add(postsLinks.item(i).getTextContent());

        } catch (Exception ei) {
            System.out.println("Error with the settings file");
        }
    }
}
