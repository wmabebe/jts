package ch.bfh.ti.jts.importer;

import java.awt.Shape;
import java.awt.geom.Path2D;
import java.util.Collection;
import java.util.LinkedHashMap;
import java.util.LinkedList;
import java.util.Map;
import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import org.w3c.dom.Document;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import ch.bfh.ti.jts.data.Edge;
import ch.bfh.ti.jts.data.Junction;
import ch.bfh.ti.jts.data.Lane;
import ch.bfh.ti.jts.data.Net;

public class Importer
{
   private DocumentBuilderFactory documentBuilderFactory;
   private DocumentBuilder        documentBuilder;

   private Net                    net             = new Net();

   private Collection<Node>       edgesNodes      = new LinkedList<Node>();
   private Collection<Node>       connectionNodes = new LinkedList<Node>();

   private Map<String, Junction>  junctions       = new LinkedHashMap<String, Junction>();
   private Map<String, Edge>      edges           = new LinkedHashMap<String, Edge>();
   private Map<String, Lane>      lanes           = new LinkedHashMap<String, Lane>();

   public Importer()
   {
      try
      {
         documentBuilderFactory = DocumentBuilderFactory.newInstance();
         documentBuilder = documentBuilderFactory.newDocumentBuilder();
      }
      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }
   }

   public Net importData(String path)
   {
      try
      {
         Document document = documentBuilder.parse(path);
         extractData(document, net);
      }
      catch (Exception ex)
      {
         throw new RuntimeException(ex);
      }

      return net;
   }

   private void extractData(Document document, Net net)
   {
      Node root = document.getDocumentElement();
      NodeList nodes = root.getChildNodes();

      for (int i = 1; i < nodes.getLength(); i++)
      {
         Node node = nodes.item(i);

         if (node.getNodeName().equals("location"))
         {
            extractLocation(node);
         }
         else if (node.getNodeName().equals("edge"))
         {
            // add to dictionary, extract later
            edgesNodes.add(node);
         }
         else if (node.getNodeName().equals("junction"))
         {
            extractJunction(node);
         }
         else if (node.getNodeName().equals("connection"))
         {
            // add to dictionary, extract later
            connectionNodes.add(node);
         }
      }

      // now we have the junctions. extract the edges
      for (Node node : edgesNodes)
      {
         extractEdge(node);
      }

      // now we have the edges. extract the connections
      for (Node node : connectionNodes)
      {
         extractConnection(node);
      }
   }

   private void extractLocation(Node node)
   {}

   private void extractJunction(Node node)
   {
      if (node == null) throw new IllegalArgumentException("node is null");
      if (!node.hasAttributes()) throw new IllegalArgumentException("node has no attributes");

      String id = node.getAttributes().getNamedItem("id").getNodeValue();
      double x = Double.valueOf(node.getAttributes().getNamedItem("x").getNodeValue());
      double y = Double.valueOf(node.getAttributes().getNamedItem("y").getNodeValue());
      Shape shape = getShape(node.getAttributes().getNamedItem("shape").getNodeValue());

      Junction junction = new Junction(x, y, shape);
      junctions.put(id, junction);
      net.getJunctions().add(junction);
   }

   private void extractEdge(Node node)
   {
      if (node == null) throw new IllegalArgumentException("node is null");
      if (!node.hasAttributes()) throw new IllegalArgumentException("node has no attributes");

      String id = node.getAttributes().getNamedItem("id").getNodeValue();
      String from = node.getAttributes().getNamedItem("from").getNodeValue();
      String to = node.getAttributes().getNamedItem("to").getNodeValue();
      int priority = Integer.valueOf(node.getAttributes().getNamedItem("priority").getNodeValue());

      Junction start = junctions.get(from);
      Junction end = junctions.get(to);

      Edge edge = new Edge(start, end, priority);
      edges.put(id, edge);
      net.getEdges().add(edge);

      NodeList nodes = node.getChildNodes();
      for (int i = 1; i < nodes.getLength(); i++)
      {
         Node child = nodes.item(i);
         if (child.getNodeName().equals("lane"))
         {
            extractLane(child, edge);
         }
      }
   }

   private void extractLane(Node node, Edge edge)
   {
      if (node == null) throw new IllegalArgumentException("node is null");
      if (!node.hasAttributes()) throw new IllegalArgumentException("node has no attributes");

      String id = node.getAttributes().getNamedItem("id").getNodeValue();
      int index = Integer.valueOf(node.getAttributes().getNamedItem("index").getNodeValue());
      double speed = Double.valueOf(node.getAttributes().getNamedItem("speed").getNodeValue());
      double length = Double.valueOf(node.getAttributes().getNamedItem("speed").getNodeValue());
      Shape shape = getShape(node.getAttributes().getNamedItem("shape").getNodeValue());

      Lane lane = new Lane(edge, index, speed, length, shape);
      edge.getLanes().add(lane);
      net.getLanes().add(lane);
      lanes.put(id, lane);
   }

   private Shape getShape(String shapeString)
   {
      if (shapeString == null) throw new IllegalArgumentException("shape is null");
      if (!shapeString.contains(" ")) throw new IllegalArgumentException("shape has wrong format");

      Path2D path = new Path2D.Double();
      String[] points = shapeString.split(" ");
      if (points.length == 0) throw new IllegalArgumentException("shape is no path");

      for (int i = 0; i < points.length; i++)
      {
         String point = points[i];
         String[] coordinates = point.split(",");
         if (coordinates.length != 2) throw new IllegalArgumentException("invalid coordinates");

         if (i == 0)
         {
            path.moveTo(Double.valueOf(coordinates[0]), Double.valueOf(coordinates[1]));
         }
         else
         {
            path.lineTo(Double.valueOf(coordinates[0]), Double.valueOf(coordinates[1]));
         }
      }

      path.closePath();
      return path;
   }

   private void extractConnection(Node node)
   {
      if (node == null) throw new IllegalArgumentException("node is null");
      if (!node.hasAttributes()) throw new IllegalArgumentException("node has no attributes");

      String from = node.getAttributes().getNamedItem("from").getNodeValue();
      String to = node.getAttributes().getNamedItem("to").getNodeValue();
      String fromLane = node.getAttributes().getNamedItem("fromLane").getNodeValue();
      String toLane = node.getAttributes().getNamedItem("toLane").getNodeValue();

      Lane laneFrom = lanes.get(String.format("%s_%s", from, fromLane));
      Lane laneTo = lanes.get(String.format("%s_%s", to, toLane));

      laneFrom.getLanes().add(laneTo);
   }
}
