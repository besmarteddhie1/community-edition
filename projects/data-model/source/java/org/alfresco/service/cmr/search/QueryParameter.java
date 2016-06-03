package org.alfresco.service.cmr.search;

import java.io.Serializable;

import org.alfresco.api.AlfrescoPublicApi;
import org.alfresco.service.namespace.QName;

/**
 * Encapsulates a query parameter
 * 
 * @author andyh
 * 
 */
@AlfrescoPublicApi
public class QueryParameter
{
    private QName qName;

    private Serializable value;

    public QueryParameter(QName qName, Serializable value)
    {
        this.qName = qName;
        this.value = value;
    }

    public QName getQName()
    {
        return qName;
    }
    

    public Serializable getValue()
    {
        return value;
    }
    
    
    
    
}
