//
// This file was generated by the JavaTM Architecture for XML Binding(JAXB) Reference Implementation, vJAXB 2.1.10 in JDK 6 
// See <a href="http://java.sun.com/xml/jaxb">http://java.sun.com/xml/jaxb</a> 
// Any modifications to this file will be lost upon recompilation of the source schema. 
// Generated on: 2010.10.06 at 03:01:28 PM CEST 
//


package org.eobjects.analyzer.job.jaxb;

import java.util.ArrayList;
import java.util.List;
import javax.xml.bind.annotation.XmlAccessType;
import javax.xml.bind.annotation.XmlAccessorType;
import javax.xml.bind.annotation.XmlAttribute;
import javax.xml.bind.annotation.XmlElement;
import javax.xml.bind.annotation.XmlSchemaType;
import javax.xml.bind.annotation.XmlType;
import javax.xml.bind.annotation.adapters.CollapsedStringAdapter;
import javax.xml.bind.annotation.adapters.XmlJavaTypeAdapter;


/**
 * <p>Java class for filterType complex type.
 * 
 * <p>The following schema fragment specifies the expected content contained within this class.
 * 
 * <pre>
 * &lt;complexType name="filterType">
 *   &lt;complexContent>
 *     &lt;restriction base="{http://www.w3.org/2001/XMLSchema}anyType">
 *       &lt;sequence>
 *         &lt;element name="descriptor" type="{http://eobjects.org/analyzerbeans/job/1.0}filterDescriptorType"/>
 *         &lt;element name="properties" type="{http://eobjects.org/analyzerbeans/job/1.0}configuredPropertiesType" minOccurs="0"/>
 *         &lt;element name="input" type="{http://eobjects.org/analyzerbeans/job/1.0}inputType" maxOccurs="unbounded"/>
 *         &lt;element name="outcome" type="{http://eobjects.org/analyzerbeans/job/1.0}outcomeType" maxOccurs="unbounded"/>
 *       &lt;/sequence>
 *       &lt;attribute name="requires" type="{http://www.w3.org/2001/XMLSchema}token" />
 *     &lt;/restriction>
 *   &lt;/complexContent>
 * &lt;/complexType>
 * </pre>
 * 
 * 
 */
@XmlAccessorType(XmlAccessType.FIELD)
@XmlType(name = "filterType", propOrder = {
    "descriptor",
    "properties",
    "input",
    "outcome"
})
public class FilterType {

    @XmlElement(required = true)
    protected FilterDescriptorType descriptor;
    protected ConfiguredPropertiesType properties;
    @XmlElement(required = true)
    protected List<InputType> input;
    @XmlElement(required = true)
    protected List<OutcomeType> outcome;
    @XmlAttribute
    @XmlJavaTypeAdapter(CollapsedStringAdapter.class)
    @XmlSchemaType(name = "token")
    protected String requires;

    /**
     * Gets the value of the descriptor property.
     * 
     * @return
     *     possible object is
     *     {@link FilterDescriptorType }
     *     
     */
    public FilterDescriptorType getDescriptor() {
        return descriptor;
    }

    /**
     * Sets the value of the descriptor property.
     * 
     * @param value
     *     allowed object is
     *     {@link FilterDescriptorType }
     *     
     */
    public void setDescriptor(FilterDescriptorType value) {
        this.descriptor = value;
    }

    /**
     * Gets the value of the properties property.
     * 
     * @return
     *     possible object is
     *     {@link ConfiguredPropertiesType }
     *     
     */
    public ConfiguredPropertiesType getProperties() {
        return properties;
    }

    /**
     * Sets the value of the properties property.
     * 
     * @param value
     *     allowed object is
     *     {@link ConfiguredPropertiesType }
     *     
     */
    public void setProperties(ConfiguredPropertiesType value) {
        this.properties = value;
    }

    /**
     * Gets the value of the input property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the input property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getInput().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link InputType }
     * 
     * 
     */
    public List<InputType> getInput() {
        if (input == null) {
            input = new ArrayList<InputType>();
        }
        return this.input;
    }

    /**
     * Gets the value of the outcome property.
     * 
     * <p>
     * This accessor method returns a reference to the live list,
     * not a snapshot. Therefore any modification you make to the
     * returned list will be present inside the JAXB object.
     * This is why there is not a <CODE>set</CODE> method for the outcome property.
     * 
     * <p>
     * For example, to add a new item, do as follows:
     * <pre>
     *    getOutcome().add(newItem);
     * </pre>
     * 
     * 
     * <p>
     * Objects of the following type(s) are allowed in the list
     * {@link OutcomeType }
     * 
     * 
     */
    public List<OutcomeType> getOutcome() {
        if (outcome == null) {
            outcome = new ArrayList<OutcomeType>();
        }
        return this.outcome;
    }

    /**
     * Gets the value of the requires property.
     * 
     * @return
     *     possible object is
     *     {@link String }
     *     
     */
    public String getRequires() {
        return requires;
    }

    /**
     * Sets the value of the requires property.
     * 
     * @param value
     *     allowed object is
     *     {@link String }
     *     
     */
    public void setRequires(String value) {
        this.requires = value;
    }

}
