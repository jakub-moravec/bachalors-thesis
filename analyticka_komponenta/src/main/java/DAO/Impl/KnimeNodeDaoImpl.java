package DAO.Impl;

import DAO.KnimeNodeDao;
import com.sun.org.apache.xpath.internal.NodeSet;
import model.KnimeNode;
import org.slf4j.Logger;
import org.slf4j.LoggerFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.SAXException;
import services.ServiceFactory;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.*;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;
import javax.xml.xpath.*;
import java.io.File;
import java.io.IOException;
import java.util.HashMap;

/**
 * Implementuje {@link KnimeNodeDao}
 *
 * @author moravja8@fel.cvut.cz
 */
public class KnimeNodeDaoImpl implements KnimeNodeDao {
    private static Logger log = LoggerFactory.getLogger(KnimeNodeDaoImpl.class);

    private DocumentBuilderFactory factory;
    private DocumentBuilder builder;
    private XPathFactory xPathfactory;

    private Transformer transformer;

    public KnimeNodeDaoImpl() {
        factory = DocumentBuilderFactory.newInstance();
        factory.setValidating(false);
        factory.setNamespaceAware(true);
        try {
            factory.setFeature("http://xml.org/sax/features/namespaces", false);
            factory.setFeature("http://xml.org/sax/features/validation", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-dtd-grammar", false);
            factory.setFeature("http://apache.org/xml/features/nonvalidating/load-external-dtd", false);
        } catch (ParserConfigurationException e) {
            log.info("Could not set features to DocumentBuilderFactory. Resulting in document builder might not work well.", e);
        }
        try {
            builder = factory.newDocumentBuilder();
        } catch (ParserConfigurationException e) {
            log.error("Could not parse configuration.", e);
        }
        xPathfactory = XPathFactory.newInstance();
        try {
            transformer = TransformerFactory.newInstance().newTransformer();
        } catch (TransformerConfigurationException e) {
            log.error("Could not create transformer.", e);
        }
    }

    public Document createDOM(File xmlFile){
        Document doc = null;
        try {
            doc = builder.parse(xmlFile);
        } catch (SAXException e) {
            log.error("Unable to build xml dom.", e);
        } catch (IOException e) {
            log.error("Unable to build document.", e);
        }
        return doc;
    }

    public Node readParameter(Document doc, String xpathString){
        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = null;
        try {
            expr = xpath.compile(xpathString);
        } catch (XPathExpressionException e) {
            log.error("Could not compile given xpath.", e);
        }

        if (expr != null){
            try {
                return (Node) expr.evaluate(doc, XPathConstants.NODE);
            } catch (XPathExpressionException e) {
                log.error("Given xpath does not work for this document.", e);
            }
        }
        return null;
    }

    public void update(KnimeNode node){
        String newNodeSettingsPath =  node.getNodeSettings().getAbsolutePath();

        ServiceFactory.getKnimeNodeService().backup(node);

        Result result = new StreamResult(newNodeSettingsPath);
        Source source = new DOMSource(node.getXmlSettings());

        try {
            transformer.setOutputProperty(OutputKeys.INDENT, "yes");
            transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
            transformer.transform(source, result);
            log.info("Changed DOM document saved to file.");
        } catch (TransformerException e) {
            log.error("Could not transform DOM document to XML file.", e);
            ServiceFactory.getKnimeNodeService().restore(node);
        }
    }



    public HashMap<String, String> readParameters(Document doc){
        NodeList nodeSet = new NodeSet();
        HashMap<String, String> parameters = new HashMap<String, String>();

        XPath xpath = xPathfactory.newXPath();
        XPathExpression expr = null;
        try {
            expr = xpath.compile("//entry");
        } catch (XPathExpressionException e) {
            log.error("Could not compile given xpath.", e);
        }

        if (expr != null){
            try {
                nodeSet = (NodeList) expr.evaluate(doc, XPathConstants.NODESET);
            } catch (XPathExpressionException e) {
                log.error("Given xpath does not work for this document.", e);
                return parameters;
            }
        }

        for (int i = 0; i < nodeSet.getLength(); i++) {
            Node node = nodeSet.item(i);
            String key = node.getAttributes().getNamedItem("key").getNodeValue();
            String value = node.getAttributes().getNamedItem("value").getNodeValue();
            parameters.put(key, value);
        }
        return parameters;
    }
}
