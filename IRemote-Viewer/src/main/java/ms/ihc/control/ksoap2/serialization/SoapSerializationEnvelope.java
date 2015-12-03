/*
 * Copyright (c) 2003,2004, Stefan Haustein, Oberhausen, Rhld., Germany
 * 
 * Permission is hereby granted, free of charge, to any person obtaining a copy of this software and
 * associated documentation files (the "Software"), to deal in the Software without restriction, including
 * without limitation the rights to use, copy, modify, merge, publish, distribute, sublicense, and/or sell
 * copies of the Software, and to permit persons to whom the Software is furnished to do so, subject to the
 * following conditions:
 * 
 * The above copyright notice and this permission notice shall be included in all copies or substantial
 * portions of the Software.
 * 
 * THE SOFTWARE IS PROVIDED "AS IS", WITHOUT WARRANTY OF ANY KIND, EXPRESS OR IMPLIED, INCLUDING BUT NOT
 * LIMITED TO THE WARRANTIES OF MERCHANTABILITY, FITNESS FOR A PARTICULAR PURPOSE AND NONINFRINGEMENT. IN NO
 * EVENT SHALL THE AUTHORS OR COPYRIGHT HOLDERS BE LIABLE FOR ANY CLAIM, DAMAGES OR OTHER LIABILITY, WHETHER
 * IN AN ACTION OF CONTRACT, TORT OR OTHERWISE, ARISING FROM, OUT OF OR IN CONNECTION WITH THE SOFTWARE OR THE
 * USE OR OTHER DEALINGS IN THE SOFTWARE.
 */

package ms.ihc.control.ksoap2.serialization;

import org.xmlpull.v1.XmlPullParser;
import org.xmlpull.v1.XmlPullParserException;
import org.xmlpull.v1.XmlSerializer;

import java.io.IOException;
import java.util.Hashtable;
import java.util.Vector;

/**
 * @author Stefan Haustein
 * 
 *         This class extends the SoapEnvelope with Soap Serialization functionality.
 */
public class SoapSerializationEnvelope extends SoapEnvelope
{
	protected static final int QNAME_TYPE = 1;
	protected static final int QNAME_NAMESPACE = 0;
	protected static final int QNAME_MARSHAL = 3;
	private static final String ANY_TYPE_LABEL = "anyType";
	private static final String ARRAY_MAPPING_NAME = "Array";
	private static final String NULL_LABEL = "null";
	private static final String NIL_LABEL = "nil";
	private static final String HREF_LABEL = "href";
	private static final String ID_LABEL = "id";
	private static final String ROOT_LABEL = "root";
	private static final String TYPE_LABEL = "type";
	private static final String ITEM_LABEL = "item";
	private static final String ARRAY_TYPE_LABEL = "arrayType";
	static final ms.ihc.control.ksoap2.serialization.Marshal DEFAULT_MARSHAL = new DM();
	public Hashtable properties = new Hashtable();

	Hashtable idMap = new Hashtable();
	Vector multiRef; // = new Vector();

	public boolean implicitTypes;

	/**
	 * Set this variable to true for compatibility with what seems to be the default encoding for
	 * .Net-Services. This feature is an extremely ugly hack. A much better option is to change the
	 * configuration of the .Net-Server to standard Soap Serialization!
	 */

	public boolean dotNet;

	/**
	 * Map from XML qualified names to Java classes
	 */

	protected Hashtable qNameToClass = new Hashtable();

	/**
	 * Map from Java class names to XML name and namespace pairs
	 */

	protected Hashtable classToQName = new Hashtable();

	/**
	 * Set to true to add and ID and ROOT label to the envelope. Change to false for compatibility with WSDL.
	 */
	protected boolean addAdornments = true;

	public SoapSerializationEnvelope(int version)
	{
		super(version);
		addMapping(enc, ARRAY_MAPPING_NAME, ms.ihc.control.ksoap2.serialization.PropertyInfo.VECTOR_CLASS);
		DEFAULT_MARSHAL.register(this);
	}

	/**
	 * @return the addAdornments
	 */
	public boolean isAddAdornments()
	{
		return addAdornments;
	}

	/**
	 * @param addAdornments
	 *            the addAdornments to set
	 */
	public void setAddAdornments(boolean addAdornments)
	{
		this.addAdornments = addAdornments;
	}

	public void parseBody(XmlPullParser parser) throws IOException, XmlPullParserException
	{
		bodyIn = null;
		parser.nextTag();
		if (parser.getEventType() == XmlPullParser.START_TAG && parser.getNamespace().equals(env)
				&& parser.getName().equals("Fault"))
		{
			SoapFault fault = new SoapFault();
			fault.parse(parser);
			bodyIn = fault;
		}
		else
		{
			while (parser.getEventType() == XmlPullParser.START_TAG)
			{
				String rootAttr = parser.getAttributeValue(enc, ROOT_LABEL);
				Object o = read(parser, null, -1, parser.getNamespace(), parser.getName(),
						ms.ihc.control.ksoap2.serialization.PropertyInfo.OBJECT_TYPE);
				if ("1".equals(rootAttr) || bodyIn == null)
					bodyIn = o;
				parser.nextTag();
			}
		}
	}

	/** Read a SoapObject. This extracts any attributes and then reads the object as a KvmSerializable. */
	protected void readSerializable(XmlPullParser parser, ms.ihc.control.ksoap2.serialization.SoapObject obj) throws IOException,
			XmlPullParserException
	{
		for (int counter = 0; counter < parser.getAttributeCount(); counter++)
		{
			String attributeName = parser.getAttributeName(counter);
			String value = parser.getAttributeValue(counter);
			((ms.ihc.control.ksoap2.serialization.SoapObject) obj).addAttribute(attributeName, value);
		}
		readSerializable(parser, (ms.ihc.control.ksoap2.serialization.KvmSerializable) obj);
	}

	/** Read a KvmSerializable. */
	protected void readSerializable(XmlPullParser parser, ms.ihc.control.ksoap2.serialization.KvmSerializable obj) throws IOException,
			XmlPullParserException
	{
		int testIndex = -1; // inc at beg. of loop for perf. reasons
		int propertyCount = obj.getPropertyCount();
		ms.ihc.control.ksoap2.serialization.PropertyInfo info = new ms.ihc.control.ksoap2.serialization.PropertyInfo();
		while (parser.nextTag() != XmlPullParser.END_TAG)
		{
			String name = parser.getName();
			int countdown = propertyCount;
			// I don't really understand what's going on in this "while(true)"
			// clause. The structure surely is wrong "while(true)" with a break is
			// pretty much always because the person who wrote it couldn't figure out what
			// it was really supposed to be doing.
			// So, here's a little CYA since I think the code is only broken for
			// implicitTypes
			if (!implicitTypes || !(obj instanceof ms.ihc.control.ksoap2.serialization.SoapObject))
			{
				while (true)
				{
					if (countdown-- == 0)
					{
						throw new RuntimeException("Unknown Property: " + name);
					}
					if (++testIndex >= propertyCount)
					{
						testIndex = 0;
					}
					obj.getPropertyInfo(testIndex, properties, info);
					if (info.namespace == null && name.equals(info.name) || info.name == null
							&& testIndex == 0 || name.equals(info.name)
							&& parser.getNamespace().equals(info.namespace))
					{
						break;
					}
				}
				obj.setProperty(testIndex, read(parser, obj, testIndex, null, null, info));
			}
			else
			{
				// I can only make this work for SoapObjects - hence the check above
				// I don't understand namespaces well enough to know whether it is correct in the next line...
				((ms.ihc.control.ksoap2.serialization.SoapObject) obj).addProperty(parser.getName(), read(parser, obj, obj.getPropertyCount(),
						((ms.ihc.control.ksoap2.serialization.SoapObject) obj).getNamespace(), name, ms.ihc.control.ksoap2.serialization.PropertyInfo.OBJECT_TYPE));
			}
		}
		parser.require(XmlPullParser.END_TAG, null, null);
	}

	/**
	 * If the type of the object cannot be determined, and thus no Marshal class can handle the object, this
	 * method is called. It will build either a SoapPrimitive or a SoapObject
	 * 
	 * @param parser
	 * @param typeNamespace
	 * @param typeName
	 * @return unknownObject wrapped as a SoapPrimitive or SoapObject
	 * @throws IOException
	 * @throws XmlPullParserException
	 */

	protected Object readUnknown(XmlPullParser parser, String typeNamespace, String typeName)
			throws IOException, XmlPullParserException
	{
		String name = parser.getName();
		String namespace = parser.getNamespace();

        // cache the attribute info list from the current element before we move on
        Vector attributeInfoVector = new Vector();
        for (int attributeCount = 0; attributeCount < parser.getAttributeCount(); attributeCount ++)
        {
            ms.ihc.control.ksoap2.serialization.AttributeInfo attributeInfo = new ms.ihc.control.ksoap2.serialization.AttributeInfo();
            attributeInfo.setName(parser.getAttributeName(attributeCount));
            attributeInfo.setValue(parser.getAttributeValue(attributeCount));
            attributeInfo.setNamespace(parser.getAttributeNamespace(attributeCount));
            attributeInfo.setType(parser.getAttributeType(attributeCount));
            attributeInfoVector.addElement(attributeInfo);
        }

		parser.next(); // move to text, inner start tag or end tag
		Object result = null;
		String text = null;
		if (parser.getEventType() == XmlPullParser.TEXT)
		{
			text = parser.getText();
            ms.ihc.control.ksoap2.serialization.SoapPrimitive sp = new ms.ihc.control.ksoap2.serialization.SoapPrimitive(typeNamespace, typeName, text);
			result = sp;
              // apply all the cached attribute info list before we add the property and descend further for parsing
            for (int i = 0; i < attributeInfoVector.size(); i++) {
                sp.addAttribute((ms.ihc.control.ksoap2.serialization.AttributeInfo) attributeInfoVector.elementAt(i));
            }
			parser.next();
		}
		else if (parser.getEventType() == XmlPullParser.END_TAG)
		{
            ms.ihc.control.ksoap2.serialization.SoapObject so = new ms.ihc.control.ksoap2.serialization.SoapObject(typeNamespace, typeName);
            // apply all the cached attribute info list before we add the property and descend further for parsing
            for (int i = 0; i < attributeInfoVector.size(); i++) {
                so.addAttribute((ms.ihc.control.ksoap2.serialization.AttributeInfo) attributeInfoVector.elementAt(i));
            }
			result = so;
		}

		if (parser.getEventType() == XmlPullParser.START_TAG)
		{
			if (text != null && text.trim().length() != 0)
			{
				throw new RuntimeException("Malformed input: Mixed content");
			}
			ms.ihc.control.ksoap2.serialization.SoapObject so = new ms.ihc.control.ksoap2.serialization.SoapObject(typeNamespace, typeName);
            // apply all the cached attribute info list before we add the property and descend further for parsing
            for (int i = 0; i < attributeInfoVector.size(); i++) {
                so.addAttribute((ms.ihc.control.ksoap2.serialization.AttributeInfo) attributeInfoVector.elementAt(i));
            }

			while (parser.getEventType() != XmlPullParser.END_TAG)
			{
				so.addProperty(parser.getName(), read(parser, so, so.getPropertyCount(), null, null,
						ms.ihc.control.ksoap2.serialization.PropertyInfo.OBJECT_TYPE));
				parser.nextTag();
			}
			result = so;
		}
		parser.require(XmlPullParser.END_TAG, namespace, name);
		return result;
	}

	private int getIndex(String value, int start, int dflt)
	{
		if (value == null)
			return dflt;
		return value.length() - start < 3 ? dflt : Integer.parseInt(value.substring(start + 1,
				value.length() - 1));
	}

	protected void readVector(XmlPullParser parser, Vector v, ms.ihc.control.ksoap2.serialization.PropertyInfo elementType) throws IOException,
			XmlPullParserException
	{
		String namespace = null;
		String name = null;
		int size = v.size();
		boolean dynamic = true;
		String type = parser.getAttributeValue(enc, ARRAY_TYPE_LABEL);
		if (type != null)
		{
			int cut0 = type.indexOf(':');
			int cut1 = type.indexOf("[", cut0);
			name = type.substring(cut0 + 1, cut1);
			String prefix = cut0 == -1 ? "" : type.substring(0, cut0);
			namespace = parser.getNamespace(prefix);
			size = getIndex(type, cut1, -1);
			if (size != -1)
			{
				v.setSize(size);
				dynamic = false;
			}
		}
		if (elementType == null)
			elementType = ms.ihc.control.ksoap2.serialization.PropertyInfo.OBJECT_TYPE;
		parser.nextTag();
		int position = getIndex(parser.getAttributeValue(enc, "offset"), 0, 0);
		while (parser.getEventType() != XmlPullParser.END_TAG)
		{
			// handle position
			position = getIndex(parser.getAttributeValue(enc, "position"), 0, position);
			if (dynamic && position >= size)
			{
				size = position + 1;
				v.setSize(size);
			}
			// implicit handling of position exceeding specified size
			v.setElementAt(read(parser, v, position, namespace, name, elementType), position);
			position++;
			parser.nextTag();
		}
		parser.require(XmlPullParser.END_TAG, null, null);
	}

	/**
	 * Builds an object from the XML stream. This method is public for usage in conjuction with Marshal
	 * subclasses. Precondition: On the start tag of the object or property, so href can be read.
	 */

	public Object read(XmlPullParser parser, Object owner, int index, String namespace, String name,
			ms.ihc.control.ksoap2.serialization.PropertyInfo expected) throws IOException, XmlPullParserException
	{
		String elementName = parser.getName();
		String href = parser.getAttributeValue(null, HREF_LABEL);
		Object obj;
		if (href != null)
		{
			if (owner == null)
				throw new RuntimeException("href at root level?!?");
			href = href.substring(1);
			obj = idMap.get(href);
			if (obj == null || obj instanceof ms.ihc.control.ksoap2.serialization.FwdRef)
			{
				ms.ihc.control.ksoap2.serialization.FwdRef f = new ms.ihc.control.ksoap2.serialization.FwdRef();
				f.next = (ms.ihc.control.ksoap2.serialization.FwdRef) obj;
				f.obj = owner;
				f.index = index;
				idMap.put(href, f);
				obj = null;
			}
			parser.nextTag(); // start tag
			parser.require(XmlPullParser.END_TAG, null, elementName);
		}
		else
		{
			String nullAttr = parser.getAttributeValue(xsi, NIL_LABEL);
			String id = parser.getAttributeValue(null, ID_LABEL);
			if (nullAttr == null)
				nullAttr = parser.getAttributeValue(xsi, NULL_LABEL);
			if (nullAttr != null && SoapEnvelope.stringToBoolean(nullAttr))
			{
				obj = null;
				parser.nextTag();
				parser.require(XmlPullParser.END_TAG, null, elementName);
			}
			else
			{
				String type = parser.getAttributeValue(xsi, TYPE_LABEL);
				if (type != null)
				{
					int cut = type.indexOf(':');
					name = type.substring(cut + 1);
					String prefix = cut == -1 ? "" : type.substring(0, cut);
					namespace = parser.getNamespace(prefix);
				}
				else if (name == null && namespace == null)
				{
					if (parser.getAttributeValue(enc, ARRAY_TYPE_LABEL) != null)
					{
						namespace = enc;
						name = ARRAY_MAPPING_NAME;
					}
					else
					{
						Object[] names = getInfo(expected.type, null);
						namespace = (String) names[0];
						name = (String) names[1];
					}
				}
				// be sure to set this flag if we don't know the types.
				if (type == null)
				{
					implicitTypes = true;
				}
				obj = readInstance(parser, namespace, name, expected);
				if (obj == null)
					obj = readUnknown(parser, namespace, name);
			}
			// finally, care about the id....
			if (id != null)
			{
				Object hlp = idMap.get(id);
				if (hlp instanceof ms.ihc.control.ksoap2.serialization.FwdRef)
				{
					ms.ihc.control.ksoap2.serialization.FwdRef f = (ms.ihc.control.ksoap2.serialization.FwdRef) hlp;
					do
					{
						if (f.obj instanceof ms.ihc.control.ksoap2.serialization.KvmSerializable)
							((ms.ihc.control.ksoap2.serialization.KvmSerializable) f.obj).setProperty(f.index, obj);
						else
							((Vector) f.obj).setElementAt(obj, f.index);
						f = f.next;
					}
					while (f != null);
				}
				else if (hlp != null)
					throw new RuntimeException("double ID");
				idMap.put(id, obj);
			}
		}

		parser.require(XmlPullParser.END_TAG, null, elementName);
		return obj;
	}

	/**
	 * Returns a new object read from the given parser. If no mapping is found, null is returned. This method
	 * is used by the SoapParser in order to convert the XML code to Java objects.
	 */
	public Object readInstance(XmlPullParser parser, String namespace, String name, ms.ihc.control.ksoap2.serialization.PropertyInfo expected)
			throws IOException, XmlPullParserException
	{
		Object obj = qNameToClass.get(new ms.ihc.control.ksoap2.serialization.SoapPrimitive(namespace, name, null));
		if (obj == null)
			return null;
		if (obj instanceof ms.ihc.control.ksoap2.serialization.Marshal)
			return ((ms.ihc.control.ksoap2.serialization.Marshal) obj).readInstance(parser, namespace, name, expected);
		else if (obj instanceof ms.ihc.control.ksoap2.serialization.SoapObject)
		{
			obj = ((ms.ihc.control.ksoap2.serialization.SoapObject) obj).newInstance();
		}
		else if (obj == ms.ihc.control.ksoap2.serialization.SoapObject.class)
		{
			obj = new ms.ihc.control.ksoap2.serialization.SoapObject(namespace, name);
		}
		else
		{
			try
			{
				obj = ((Class) obj).newInstance();
			}
			catch (Exception e)
			{
				throw new RuntimeException(e.toString());
			}
		}
		// ok, obj is now the instance, fill it....
		if (obj instanceof ms.ihc.control.ksoap2.serialization.SoapObject)
			readSerializable(parser, (ms.ihc.control.ksoap2.serialization.SoapObject) obj);
		else if (obj instanceof ms.ihc.control.ksoap2.serialization.KvmSerializable)
			readSerializable(parser, (ms.ihc.control.ksoap2.serialization.KvmSerializable) obj);
		else if (obj instanceof Vector)
			readVector(parser, (Vector) obj, expected.elementType);
		else
			throw new RuntimeException("no deserializer for " + obj.getClass());
		return obj;
	}

	/**
	 * Returns a string array containing the namespace, name, id and Marshal object for the given java object.
	 * This method is used by the SoapWriter in order to map Java objects to the corresponding SOAP section
	 * five XML code.
	 */
	public Object[] getInfo(Object type, Object instance)
	{
		if (type == null)
		{
			if (instance instanceof ms.ihc.control.ksoap2.serialization.SoapObject || instance instanceof ms.ihc.control.ksoap2.serialization.SoapPrimitive)
				type = instance;
			else
				type = instance.getClass();
		}
		if (type instanceof ms.ihc.control.ksoap2.serialization.SoapObject)
		{
			ms.ihc.control.ksoap2.serialization.SoapObject so = (ms.ihc.control.ksoap2.serialization.SoapObject) type;
			return new Object[] { so.getNamespace(), so.getName(), null, null };
		}
		if (type instanceof ms.ihc.control.ksoap2.serialization.SoapPrimitive)
		{
			ms.ihc.control.ksoap2.serialization.SoapPrimitive sp = (ms.ihc.control.ksoap2.serialization.SoapPrimitive) type;
			return new Object[] { sp.getNamespace(), sp.getName(), null, DEFAULT_MARSHAL };
		}
		if ((type instanceof Class) && type != ms.ihc.control.ksoap2.serialization.PropertyInfo.OBJECT_CLASS)
		{
			Object[] tmp = (Object[]) classToQName.get(((Class) type).getName());
			if (tmp != null)
				return tmp;
		}
		return new Object[] { xsd, ANY_TYPE_LABEL, null, null };
	}

	/**
	 * Defines a direct mapping from a namespace and name to a java class (and vice versa), using the given
	 * marshal mechanism
	 */
	public void addMapping(String namespace, String name, Class clazz, ms.ihc.control.ksoap2.serialization.Marshal marshal)
	{
		qNameToClass
				.put(new ms.ihc.control.ksoap2.serialization.SoapPrimitive(namespace, name, null), marshal == null ? (Object) clazz : marshal);
		classToQName.put(clazz.getName(), new Object[] { namespace, name, null, marshal });
	}

	/**
	 * Defines a direct mapping from a namespace and name to a java class (and vice versa)
	 */
	public void addMapping(String namespace, String name, Class clazz)
	{
		addMapping(namespace, name, clazz, null);
	}

	/**
	 * Adds a SoapObject to the class map. During parsing, objects of the given type (namespace/name) will be
	 * mapped to corresponding copies of the given SoapObject, maintaining the structure of the template.
	 */
	public void addTemplate(ms.ihc.control.ksoap2.serialization.SoapObject so)
	{
		qNameToClass.put(new ms.ihc.control.ksoap2.serialization.SoapPrimitive(so.namespace, so.name, null), so);
	}

	/**
	 * Response from the soap call. Pulls the object from the wrapper object and returns it.
	 * 
	 * @since 2.0.3
	 * @return response from the soap call.
	 * @throws SoapFault
	 */
	public Object getResponse() throws SoapFault
	{
		if (bodyIn instanceof SoapFault)
		{
			throw (SoapFault) bodyIn;
		}
		ms.ihc.control.ksoap2.serialization.KvmSerializable ks = (ms.ihc.control.ksoap2.serialization.KvmSerializable) bodyIn;
		return ks.getPropertyCount() == 0 ? null : ks.getProperty(0);
	}

	/**
	 * @deprecated Please use the getResponse going forward
	 * @see #getResponse()
	 */
	public Object getResult()
	{
		ms.ihc.control.ksoap2.serialization.KvmSerializable ks = (ms.ihc.control.ksoap2.serialization.KvmSerializable) bodyIn;
		return ks.getPropertyCount() == 0 ? null : ks.getProperty(0);
	}

	/**
	 * Serializes the request object to the given XmlSerliazer object
	 * 
	 * @param writer
	 *            XmlSerializer object to write the body into.
	 */
	public void writeBody(XmlSerializer writer) throws IOException
	{
		if(bodyOut == null)
			return;
		
		multiRef = new Vector();
		multiRef.addElement(bodyOut);
		Object[] qName = getInfo(null, bodyOut);
		if(qName[QNAME_TYPE] != "")
			writer.startTag((dotNet) ? "" : (String) qName[QNAME_NAMESPACE], (String) qName[QNAME_TYPE]);
		if (dotNet)
		{
			writer.attribute(null, "xmlns", (String) qName[QNAME_NAMESPACE]);
		}
		if (addAdornments)
		{
			writer.attribute(null, ID_LABEL, qName[2] == null ? ("o" + 0) : (String) qName[2]);
			writer.attribute(enc, ROOT_LABEL, "1");
		}
		writeElement(writer, bodyOut, null, qName[QNAME_MARSHAL]);
		if(qName[QNAME_TYPE] != "")
			writer.endTag((dotNet) ? "" : (String) qName[QNAME_NAMESPACE], (String) qName[QNAME_TYPE]);

	}

	/**
	 * Writes the body of an SoapObject. This method write the attributes and then calls
	 * "writeObjectBody (writer, (KvmSerializable)obj);"
	 */
	public void writeObjectBody(XmlSerializer writer, ms.ihc.control.ksoap2.serialization.SoapObject obj) throws IOException
	{
		ms.ihc.control.ksoap2.serialization.SoapObject soapObject = (ms.ihc.control.ksoap2.serialization.SoapObject) obj;
		for (int counter = 0; counter < soapObject.getAttributeCount(); counter++)
		{
			ms.ihc.control.ksoap2.serialization.AttributeInfo attributeInfo = new ms.ihc.control.ksoap2.serialization.AttributeInfo();
			soapObject.getAttributeInfo(counter, attributeInfo);
			writer.attribute(attributeInfo.getNamespace(), attributeInfo.getName(), attributeInfo.getValue()
					.toString());
		}
		writeObjectBody(writer, (ms.ihc.control.ksoap2.serialization.KvmSerializable) obj);
	}

	/**
	 * Writes the body of an KvmSerializable object. This method is public for access from Marshal subclasses.
	 */
	public void writeObjectBody(XmlSerializer writer, ms.ihc.control.ksoap2.serialization.KvmSerializable obj) throws IOException
	{
		ms.ihc.control.ksoap2.serialization.PropertyInfo info = new ms.ihc.control.ksoap2.serialization.PropertyInfo();
		int cnt = obj.getPropertyCount();
		for (int i = 0; i < cnt; i++)
		{
			obj.getPropertyInfo(i, properties, info);
			if ((info.flags & ms.ihc.control.ksoap2.serialization.PropertyInfo.TRANSIENT) == 0)
			{
				writer.startTag(info.namespace, info.name);
				writeProperty(writer, obj.getProperty(i), info);
				writer.endTag(info.namespace, info.name);
			}
		}
	}

	protected void writeProperty(XmlSerializer writer, Object obj, ms.ihc.control.ksoap2.serialization.PropertyInfo type) throws IOException
	{
		if (obj == null)
		{
			writer.attribute(xsi, version >= VER12 ? NIL_LABEL : NULL_LABEL, "true");
			return;
		}
		Object[] qName = getInfo(null, obj);
		if (type.multiRef || qName[2] != null)
		{
			int i = multiRef.indexOf(obj);
			if (i == -1)
			{
				i = multiRef.size();
				multiRef.addElement(obj);
			}
			writer.attribute(null, HREF_LABEL, qName[2] == null ? ("#o" + i) : "#" + qName[2]);
		}
		else
		{
			if (!implicitTypes || obj.getClass() != type.type)
			{
				String prefix = writer.getPrefix((String) qName[QNAME_NAMESPACE], true);
				writer.attribute(xsi, TYPE_LABEL, prefix + ":" + qName[QNAME_TYPE]);
			}
			writeElement(writer, obj, type, qName[QNAME_MARSHAL]);
		}
	}

	private void writeElement(XmlSerializer writer, Object element, ms.ihc.control.ksoap2.serialization.PropertyInfo type, Object marshal)
			throws IOException
	{
		if (marshal != null)
			((ms.ihc.control.ksoap2.serialization.Marshal) marshal).writeInstance(writer, element);
		else if (element instanceof ms.ihc.control.ksoap2.serialization.SoapObject)
			writeObjectBody(writer, (ms.ihc.control.ksoap2.serialization.SoapObject) element);
		else if (element instanceof ms.ihc.control.ksoap2.serialization.KvmSerializable)
			writeObjectBody(writer, (ms.ihc.control.ksoap2.serialization.KvmSerializable) element);
		else if (element instanceof Vector)
			writeVectorBody(writer, (Vector) element, type.elementType);
		else
			throw new RuntimeException("Cannot serialize: " + element);
	}

	protected void writeVectorBody(XmlSerializer writer, Vector vector, ms.ihc.control.ksoap2.serialization.PropertyInfo elementType)
			throws IOException
	{
		String itemsTagName = ITEM_LABEL;
		String itemsNamespace = null;
	
		if (elementType == null)
		{
			elementType = ms.ihc.control.ksoap2.serialization.PropertyInfo.OBJECT_TYPE;
		}
		else if (elementType instanceof ms.ihc.control.ksoap2.serialization.PropertyInfo)
		{
			if (elementType.name != null)
			{
				itemsTagName = elementType.name;
				itemsNamespace = elementType.namespace;
			}
		}
		
		int cnt = vector.size();
		Object[] arrType = getInfo(elementType.type, null);
		// I think that this needs an implicitTypes check, but don't have a failure case for that
		writer.attribute(enc, ARRAY_TYPE_LABEL, writer.getPrefix((String) arrType[0], false) + ":"
				+ arrType[1] + "[" + cnt + "]");
		boolean skipped = false;
		for (int i = 0; i < cnt; i++)
		{
			if (vector.elementAt(i) == null)
				skipped = true;
			else
			{
				writer.startTag(itemsNamespace, itemsTagName);
				if (skipped)
				{
					writer.attribute(enc, "position", "[" + i + "]");
					skipped = false;
				}
				writeProperty(writer, vector.elementAt(i), elementType);
				writer.endTag(itemsNamespace, itemsTagName);
			}
		}
	}

}
