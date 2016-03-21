package ca.ubc.salt.model.state;

import java.io.File;
import java.io.IOException;
import java.io.StringReader;
import java.util.HashMap;
import java.util.HashSet;
import java.util.LinkedList;
import java.util.List;
import java.util.Map;
import java.util.Set;

import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.ParserConfigurationException;

import org.eclipse.jdt.core.dom.SimpleName;
import org.w3c.dom.Document;
import org.w3c.dom.Element;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;

import ca.ubc.salt.model.utils.FileUtils;
import ca.ubc.salt.model.utils.Settings;
import ca.ubc.salt.model.utils.Utils;
import ca.ubc.salt.model.utils.XMLUtils;

public class StateCompatibilityChecker
{
    HashMap<List<String>, Set<String>> varStateSet = new HashMap<List<String>, Set<String>>();
    

    public static void main(String[] args) throws SAXException, IOException
    {
	StateCompatibilityChecker scc = new StateCompatibilityChecker();
	// scc.processState("testSubtract-17.xml");
	scc.populateVarStateSet();
	System.out.println(scc.varStateSet);
	 Map<String, Set<SimpleName>> readVars =
	 ReadVariableDetector.populateReadVarsForFile(
	 "/Users/Arash/Research/repos/commons-math/src/test/java/org/apache/commons/math4/fraction/FractionTest.java");
	 Map<String, Set<List<String>>> readValues = ReadVariableDetector.getReadValues(readVars);
	 Map<String, Set<String>> compatibleStates = getCompatibleStates(scc.varStateSet, readValues);
    }

    
    public static Map<String, Set<String>> getCompatibleStates(HashMap<List<String>, Set<String>> varStateSet, Map<String, Set<List<String>>> readValues)
    {
	return null;
    }
    
    
    public static Set<String> getAllStatesWithVariableValue(HashMap<List<String>, Set<String>> varStateSet, List<String> value)
    {
	return null;
    }
    
    private void populateVarStateSet()
    {
	File folder = new File(Settings.tracePaths);
	String[] traces = folder.list();

	for (String state : traces)
	{
	    try
	    {
		processState(state);
	    } catch (SAXException e)
	    {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    } catch (IOException e)
	    {
		// TODO Auto-generated catch block
		e.printStackTrace();
	    }
	}
    }

    public void processState(String stateName) throws SAXException, IOException
    {
	NodeList nodeList = XMLUtils.getNodeList(stateName);

	for (int i = 0; i < nodeList.getLength(); i++)
	{
	    Node object = nodeList.item(i);
	    if (object instanceof Element)
	    {
		processObject(object, this.varStateSet, new LinkedList<String>(), stateName);
	    }
	}
    }

    public static void processObjectIndex(String stateName, int i, HashMap<List<String>, Set<String>> varStateSet, NodeList nodeList) throws SAXException, IOException
    {
	Node object = nodeList.item(i);
	if (object instanceof Element)
	{
	    processObject(object, varStateSet, new LinkedList<String>(), stateName);
	}
    }

    

    public static void processObject(Node object, HashMap<List<String>, Set<String>> varStateSet,
	    LinkedList<String> key, String stateName)
    {

	key.add(object.getNodeName());

	NodeList children = object.getChildNodes();
	if (children.getLength() == 0)
	{
	    key.add(object.getNodeValue());
	    addToList(varStateSet, key, stateName);
	    key.removeLast();
	} else
	{
	    for (int i = 0; i < children.getLength(); i++)
	    {
		processObject(children.item(i), varStateSet, key, stateName);
	    }
	}

	key.removeLast();
    }

    private static void addToList(HashMap<List<String>, Set<String>> varStateSet, List<String> key, String stateName)
    {
	Set<String> states = varStateSet.get(key);
	if (states != null)
	{
	    states.add(stateName);
	} else
	{
	    states = new HashSet<String>();
	    states.add(stateName);
	    // TODO check if you need to clone key object!
	    varStateSet.put(new LinkedList<String>(key), states);
	}
    }

    private Set<String> getCompatibleStates(List<List<String>> vars)
    {
	List<Set> stateSets = new LinkedList<Set>();
	for (List<String> var : vars)
	{
	    Set states = varStateSet.get(var);
	    if (states != null)
		stateSets.add(states);
	}

	return Utils.intersection(stateSets);
    }
}
