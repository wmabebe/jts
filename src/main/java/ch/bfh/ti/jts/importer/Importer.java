package ch.bfh.ti.jts.importer;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;

import org.w3c.dom.Document;
import org.w3c.dom.Node;

/**
 * Abstract class for XML file importers.
 *
 * @author Mathias
 * @param <T>
 *            type of the imported class file.
 */
public abstract class Importer<T> {

    private DocumentBuilderFactory documentBuilderFactory;
    private DocumentBuilder        documentBuilder;
    private Document               document;
    private T                      data;

    public Importer() {
        try {
            documentBuilderFactory = DocumentBuilderFactory.newInstance();
            documentBuilder = documentBuilderFactory.newDocumentBuilder();
        } catch (final Exception ex) {
            throw new RuntimeException(ex);
        }
    }

    /**
     * Converts a class into another.
     *
     * @param input
     *            input class
     * @param outputClass
     *            output type
     * @return converted object
     * @throws Exception
     */
    private <I, O> O convert(final I input, final Class<O> outputClass) throws Exception {
        return input == null ? null : outputClass.getConstructor(String.class).newInstance(input.toString());
    }

    /**
     * Abstract method that handles the data extraction.
     *
     * @param document
     *            XML document
     * @return object representation of the imported file
     */
    abstract T extractData(final Document document);

    /**
     * Get the attribute value of a node with the specified type.
     *
     * @param node
     *            document node
     * @param attributeName
     *            name of the attribute to convert
     * @param outputClass
     *            output type of the attribute
     * @return attribute value
     */
    protected <O> O getAttribute(final Node node, final String attributeName, final Class<O> outputClass) {
        O output = null;
        try {
            if (node.hasAttributes()) {
                final Node attribute = node.getAttributes().getNamedItem(attributeName);
                if (attribute != null) {
                    final String value = attribute.getNodeValue();
                    output = convert(value, outputClass);
                }
            }
        } catch (final Exception ex) {
            throw new RuntimeException("conversion failed", ex);
        }
        return output;
    }

    /**
     * Imports data from a file into an object representation.
     *
     * @param path
     *            path to the file to import
     * @return object representation of the imported file
     */
    public T importData(final String path) {
        try {
            document = documentBuilder.parse(path);
            data = extractData(document);
        } catch (final Exception ex) {
            throw new RuntimeException("document parsing failed", ex);
        }
        return data;
    }
}
