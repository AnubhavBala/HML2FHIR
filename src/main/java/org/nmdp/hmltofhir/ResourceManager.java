package org.nmdp.hmltofhir;

import java.text.DateFormat;
import java.text.SimpleDateFormat;
import java.util.Date;
import java.util.UUID;
import java.io.BufferedReader;
import java.io.File;
import java.io.FileNotFoundException;
import java.io.FileReader;
import java.io.IOException;
import java.io.StringReader;
import java.net.URL;

import ca.uhn.fhir.context.FhirContext;


import org.hl7.fhir.dstu3.model.*;
import org.hl7.fhir.dstu3.model.Enumerations.*;
import org.hl7.fhir.dstu3.model.Sequence.*;
import org.hl7.fhir.dstu3.model.Specimen.*;
import ca.uhn.fhir.rest.client.IGenericClient;


import javax.xml.parsers.DocumentBuilder;
import javax.xml.parsers.DocumentBuilderFactory;
import javax.xml.parsers.SAXParser;
import javax.xml.parsers.SAXParserFactory;

import org.w3c.dom.Document;
import org.w3c.dom.NamedNodeMap;
import org.w3c.dom.Node;
import org.w3c.dom.NodeList;
import org.xml.sax.Attributes;
import org.xml.sax.InputSource;
import org.xml.sax.SAXException;
import org.xml.sax.helpers.DefaultHandler;

public class ResourceManager {
    public static Specimen specimen;
    public static Sequence sequence;
    public static Observation observation = new Observation();
    public static DiagnosticReport diagnosticReport= new DiagnosticReport();
    private static XMLParse parse;
    private static String xml;
    private static Document resourceTemplate;
    public static String [] seq= new String[20];
    public static String [] spec = new String[20];
    public static String [] obv = new String [20];
    public static String [] diag = new String [20];
    public ResourceManager(String xml)
    {
        parse= new XMLParse(xml,this);
        this.xml=xml;
        parse.grab();
    }
	
	public static void addResource(String resource,String structure, String value) {
        /*
         Change these arrays to arrays of linked lists
         This allows for multiple of any structure to beb allowed 
         */
        
        DocumentBuilderFactory factory = DocumentBuilderFactory.newInstance();
        DocumentBuilder builder;
        try {
            builder = factory.newDocumentBuilder();
            Document template = builder.parse(new InputSource(new StringReader(parse.xmlToString("/org/nmdp/ResourceName.xml"))));
            resourceTemplate = template;
            Document xmlDOM = builder.parse(new InputSource(new StringReader(xml)));
            
        } catch (Exception e) {
            System.out.println("Error in handle Managing " + e);
        }
        NodeList positionList = resourceTemplate.getElementsByTagName("resourceName");

        if(resource.equals("Sequence"))
        {
            System.out.println("Adding to Sequence");
            for (int i = 0; i < positionList.getLength(); i++) {
                NamedNodeMap positionAttribute = positionList.item(i).getAttributes();
                if(structure.equals(parse.getAttribute(positionAttribute,"structure"))&&resource.equals(parse.getAttribute(positionAttribute,"resource")))
                   {
                    seq[Integer.parseInt(parse.getAttribute(positionAttribute,"position"))]=isNotNull(value);
                    }
                
            }
                  
        }
        else if(resource.equals("Specimen"))
        {
            System.out.println("Adding to Specimen");

            for (int i = 0; i < positionList.getLength(); i++) {
                NamedNodeMap positionAttribute = positionList.item(i).getAttributes();
                if(structure.equals(parse.getAttribute(positionAttribute,"structure"))&&resource.equals(parse.getAttribute(positionAttribute,"resource")))
                   {
                    spec[Integer.parseInt(parse.getAttribute(positionAttribute,"position"))]=isNotNull(value);
                }
                   
                   }
            
            
        }
        else if(resource.equals("Observation"))
        {
            System.out.println("Adding to Observation");
   
            for (int i = 0; i < positionList.getLength(); i++) {
                NamedNodeMap positionAttribute = positionList.item(i).getAttributes();
                if(structure.equals(parse.getAttribute(positionAttribute,"structure"))&&resource.equals(parse.getAttribute(positionAttribute,"resource")))
                   {
                    obv[Integer.parseInt(parse.getAttribute(positionAttribute,"position"))]=isNotNull(value);
                }
                   
                   }
            
        }
        else if(resource.equals("DiagnosticReport"))
        {
            System.out.println("Adding to DR");
            for (int i = 0; i < positionList.getLength(); i++) {
                NamedNodeMap positionAttribute = positionList.item(i).getAttributes();
                if(structure.equals(parse.getAttribute(positionAttribute,"structure"))&&resource.equals(parse.getAttribute(positionAttribute,"resource")))
                   {
                    diag[Integer.parseInt(parse.getAttribute(positionAttribute,"position"))]=isNotNull(value);
                }
                   
                   }
        }
        else
        {
            System.out.println("Error: Couldnt find a resource");
        }
				
	}
    public static void createResources()
    {
        //Call all methods
        System.out.println("Making resources");
        newSpecimen();
        newSequence();
        
    }
    // Checks if string is null if so
	public static String isNotNull(String value)
	{
		return (value==null)|| (value.equals(""))? null: value;
	}
    public static void newSpecimen()
    {
                    //Specimen.addwhatever in a for loop through linked lists do a hard set at 10 but can change
                    //loop through linked list if it hits a null it doesnt matter'
        System.out.println("In Specimen");
        try{
            
        
            specimen=new Specimen();
            Reference[] ref = new Reference[]{new Reference()};
            ref[0].setReference("Patient/"+diag[0]);
            specimen.setCollection(SpecimenCollectionComponent.class.newInstance().setMethod(CodeableConcept.class.newInstance().setText(spec[0])));
            specimen.setSubject(ref[0]);
        }
        catch(Exception e)
        {
            
        }
    }
    public static void newSequence()
    {
        System.out.println("In Sequence");
        try{
            sequence=new Sequence();
            Reference [] ref = new Reference[]{new Reference(),new Reference()};
            //Cant find the method for some reason
            //Sequence.addReferenceSeq().setWindowStart(Integer.parseInt(seq[1])).setWindowEnd(Integer.parseInt(seq[2])).setReferenceSeqString(seq[3]);
            sequence.setReferenceSeq(SequenceReferenceSeqComponent.class.newInstance().setWindowStart(Integer.parseInt(seq[1])).setWindowEnd(Integer.parseInt(seq[2])).setReferenceSeqString(seq[3]));
            sequence.setObservedSeq(seq[4]);
            sequence.addVariant().setStart(Integer.parseInt(seq[5])).setEnd(Integer.parseInt(seq[6])).setObservedAllele(seq[7]).setReferenceAllele(seq[8]);
            sequence.addQuality().setStart(Integer.parseInt(seq[9])).setEnd(Integer.parseInt(seq[10])).setScore(Quantity.class.newInstance().setValue(Double.parseDouble(seq[11])));
            sequence.setType(SeqType(seq[0]));
            
            sequence.setSpecimen(ref[0].setReference("Specimen/"+diag[0]));
            sequence.setPatient(ref[0].setReference("Patient/"+diag[0]));
        }
        catch(Exception e)
        {
                            
        }
    }
    public static SequenceType SeqType(String type)
    {
                if(type.equals("DNA"))
                {
                    return SequenceType.DNA;
                }
                else if(type.equals("RNA"))
                {
                    return SequenceType.RNA;
                }
                else if(type.equals("AA"))
                {
                    return SequenceType.AA;
                }
                else{
                    return null;
                }
    }
    /* ToDo:
     If there are references they will be put at the end of the array
     String to primative data types methods*/
}
