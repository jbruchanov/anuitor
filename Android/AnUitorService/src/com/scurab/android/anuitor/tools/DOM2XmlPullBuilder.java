/* -*-             c-basic-offset: 4; indent-tabs-mode: nil; -*-  //------100-columns-wide------>|*/
// for license please see accompanying LICENSE.txt file (available also at http://www.xmlpull.org/)

package com.scurab.android.anuitor.tools;

import com.scurab.android.anuitor.hierarchy.IdsHelper;

import org.w3c.dom.DOMException;
import org.w3c.dom.DOMImplementation;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Text;
import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlPullParserFactory;

import java.io.IOException;
import java.io.Reader;
import java.io.StringWriter;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.FactoryConfigurationError;
import javax.xml.parsers.ParserConfigurationException;
import javax.xml.transform.OutputKeys;
import javax.xml.transform.Transformer;
import javax.xml.transform.TransformerException;
import javax.xml.transform.TransformerFactory;
import javax.xml.transform.dom.DOMSource;
import javax.xml.transform.stream.StreamResult;

//TOOD add parse methodthat will usenextToken() to reconstruct complete XML infoset

/**
 * <strong>Simplistic</strong> DOM2 builder that should be enough to do support most cases.
 * Requires JAXP DOMBuilder to provide DOM2 implementation.
 * <p>NOTE:this class is stateless factory and it is safe to share between multiple threads.
 * </p>
 *
 * @author <a href="http://www.extreme.indiana.edu/~aslom/">Aleksander Slominski</a>
 */

//this is copy from web and removed few calling which crashes with Android XmlPullParser jBr

public class DOM2XmlPullBuilder {

    protected Document newDoc() throws XmlPullParserException {
        try {
            // if you see dreaded "java.lang.NoClassDefFoundError: org/w3c/dom/ranges/DocumentRange"
            // add the newest xercesImpl and apis JAR file to JRE/lib/endorsed
            // for more deatils see: http://java.sun.com/j2se/1.4.2/docs/guide/standards/index.html
            DocumentBuilderFactory domFactory = DocumentBuilderFactory.newInstance();
            DocumentBuilder builder = domFactory.newDocumentBuilder();
            DOMImplementation impl = builder.getDOMImplementation();
            return builder.newDocument();
        } catch (FactoryConfigurationError ex) {
            throw new XmlPullParserException(
                    "could not configure factory JAXP DocumentBuilderFactory: " + ex, null, ex);
        } catch (ParserConfigurationException ex) {
            throw new XmlPullParserException(
                    "could not configure parser JAXP DocumentBuilderFactory: " + ex, null, ex);
        }
    }

    protected XmlPullParser newParser() throws XmlPullParserException {
        XmlPullParserFactory factory = XmlPullParserFactory.newInstance();
        return factory.newPullParser();
    }

    public Element parse(Reader reader) throws XmlPullParserException, IOException {
        Document docFactory = newDoc();
        return parse(reader, docFactory);
    }

    public Element parse(Reader reader, Document docFactory)
            throws XmlPullParserException, IOException {
        XmlPullParser pp = newParser();
        pp.setFeature(XmlPullParser.FEATURE_PROCESS_NAMESPACES, true);
        pp.setInput(reader);
        pp.next();
        return parse(pp, docFactory);
    }

    public Element parse(XmlPullParser pp, Document docFactory)
            throws XmlPullParserException, IOException {
        Element root = parseSubTree(pp, docFactory);
        return root;
    }

    public Element parseSubTree(XmlPullParser pp) throws XmlPullParserException, IOException {
        Document doc = newDoc();
        Element root = parseSubTree(pp, doc);
        return root;
    }

    public Element parseSubTree(XmlPullParser pp, Document docFactory)
            throws XmlPullParserException, IOException {
        BuildProcess process = new BuildProcess();
        return process.parseSubTree(pp, docFactory);
    }

    static class BuildProcess {
        private XmlPullParser pp;
        private Document docFactory;
        private boolean scanNamespaces = false;//not supported on android

        private BuildProcess() {
        }

        public Element parseSubTree(XmlPullParser pp, Document docFactory)
                throws XmlPullParserException, IOException {
            this.pp = pp;
            this.docFactory = docFactory;
            return parseSubTree();
        }

        private Element parseSubTree()
                throws XmlPullParserException, IOException {
            pp.require(XmlPullParser.START_TAG, null, null);
            String name = pp.getName();
            String ns = pp.getNamespace();
            String prefix = null;//pp.getPrefix(); no on android
            String qname = prefix != null ? prefix + ":" + name : name;
            Element parent = docFactory.createElementNS(ns, qname);

            //declare namespaces - quite painful and easy to fail process in DOM2
            declareNamespaces(pp, parent);

            // process attributes
            for (int i = 0; i < pp.getAttributeCount(); i++) {
                String attrNs = pp.getAttributeNamespace(i);
                String attrName = pp.getAttributeName(i);
                String attrValue = pp.getAttributeValue(i);
                //android stuff
                if (attrValue != null && attrValue.length() > 1
                        && attrValue.charAt(0) == '@' && Character.isDigit(attrValue.charAt(1))) {
                    try {
                        int iValue = Integer.parseInt(attrValue.substring(1));
                        String newValue = IdsHelper.getValueForId(iValue);
                        if(newValue != null){
                            attrValue = newValue;
                        }
                    } catch (Exception e) {
                        //ignore and keep old value
                    }
                }

                if (attrNs == null || attrNs.length() == 0) {
                    parent.setAttribute(attrName, attrValue);
                } else {
                    String attrPrefix = null;//pp.getAttributePrefix(i); not supported on android
                    String attrQname = attrPrefix != null ? attrPrefix + ":" + attrName : attrName;
                    parent.setAttributeNS(attrNs, attrQname, attrValue);
                }
            }

            // process children
            while (pp.next() != XmlPullParser.END_TAG) {
                if (pp.getEventType() == XmlPullParser.START_TAG) {
                    Element el = parseSubTree(pp, docFactory);
                    parent.appendChild(el);
                } else if (pp.getEventType() == XmlPullParser.TEXT) {
                    String text = pp.getText();
                    Text textEl = docFactory.createTextNode(text);
                    parent.appendChild(textEl);
                } else {
                    throw new XmlPullParserException(
                            "unexpected event " + XmlPullParser.TYPES[pp.getEventType()], pp, null);
                }
            }
            pp.require(XmlPullParser.END_TAG, ns, name);
            return parent;
        }

        private void declareNamespaces(XmlPullParser pp, Element parent)
                throws DOMException, XmlPullParserException {
            //not supported on android
        }

        private void declareOneNamespace(XmlPullParser pp, int i, Element parent)
                throws DOMException, XmlPullParserException {
            String xmlnsPrefix = pp.getNamespacePrefix(i);
            String xmlnsUri = pp.getNamespaceUri(i);
            String xmlnsDecl = (xmlnsPrefix != null) ? "xmlns:" + xmlnsPrefix : "xmlns";
            parent.setAttributeNS("http://www.w3.org/2000/xmlns/", xmlnsDecl, xmlnsUri);
        }
    }

    private static void assertEquals(String expected, String s) {
        if ((expected != null && !expected.equals(s)) || (expected == null && s == null)) {
            throw new RuntimeException("expected '" + expected + "' but got '" + s + "'");
        }
    }

    private static void assertNotNull(Object o) {
        if (o == null) {
            throw new RuntimeException("expected no null value");
        }
    }

    public static String transform(XmlPullParser xmlPullParser) throws IOException, XmlPullParserException, TransformerException {
        int type;
        while ((type = xmlPullParser.next()) != XmlPullParser.START_TAG &&
                type != XmlPullParser.END_DOCUMENT) {
            // Empty loop
        }

        if (type != XmlPullParser.START_TAG) {
            throw new XmlPullParserException("No start tag found");
        }

        DOM2XmlPullBuilder dom2XmlPullBuilder = new DOM2XmlPullBuilder();
        Element element = dom2XmlPullBuilder.parseSubTree(xmlPullParser);
        StringWriter buffer = new StringWriter();

        TransformerFactory transFactory = TransformerFactory.newInstance();
        Transformer transformer = transFactory.newTransformer();
        transformer.setOutputProperty(OutputKeys.INDENT, "yes");
        transformer.setOutputProperty("{http://xml.apache.org/xslt}indent-amount", "2");
        transformer.transform(new DOMSource(element), new StreamResult(buffer));

        return buffer.toString();
    }
}

