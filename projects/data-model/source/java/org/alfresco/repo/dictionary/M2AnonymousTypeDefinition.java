package org.alfresco.repo.dictionary;

import java.io.Serializable;
import java.util.Collection;
import java.util.Collections;
import java.util.HashMap;
import java.util.List;
import java.util.Map;
import java.util.Set;

import org.alfresco.repo.i18n.StaticMessageLookup;
import org.alfresco.service.cmr.dictionary.AspectDefinition;
import org.alfresco.service.cmr.dictionary.AssociationDefinition;
import org.alfresco.service.cmr.dictionary.ChildAssociationDefinition;
import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.ModelDefinition;
import org.alfresco.service.cmr.dictionary.PropertyDefinition;
import org.alfresco.service.cmr.dictionary.TypeDefinition;
import org.alfresco.service.cmr.i18n.MessageLookup;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;


/**
 * Compiled anonymous type definition.
 * 
 * @author David Caruana
 *
 */
/*package*/ class M2AnonymousTypeDefinition implements TypeDefinition
{
    private TypeDefinition type;
    private Map<QName,PropertyDefinition> properties = new HashMap<QName,PropertyDefinition>();
    private Map<QName,AssociationDefinition> associations = new HashMap<QName,AssociationDefinition>();
    private Map<QName,ChildAssociationDefinition> childassociations = new HashMap<QName,ChildAssociationDefinition>();
    private transient MessageLookup staticMessageLookup = new StaticMessageLookup();

    /**
     * Construct
     * 
     * @param type  the primary type
     * @param aspects  the aspects to combine with the type
     */
    /*package*/ M2AnonymousTypeDefinition(TypeDefinition type, Collection<AspectDefinition> aspects)
    {
        this.type = type;
        
        // Combine features of type and aspects
        properties.putAll(type.getProperties());
        associations.putAll(type.getAssociations());
        childassociations.putAll(type.getChildAssociations());
        for (AspectDefinition aspect : aspects)
        {
            properties.putAll(aspect.getProperties());
            associations.putAll(aspect.getAssociations());
            childassociations.putAll(aspect.getChildAssociations());
        }
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.dictionary.ClassDefinition#getModel()
     */
    public ModelDefinition getModel()
    {
        return type.getModel();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.TypeDefinition#getDefaultAspects()
     */
    public List<AspectDefinition> getDefaultAspects()
    {
        return type.getDefaultAspects();
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.dictionary.ClassDefinition#getDefaultAspects(boolean)
     */
    public List<AspectDefinition> getDefaultAspects(boolean inherited)
    {
        return type.getDefaultAspects(inherited);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.dictionary.ClassDefinition#getDefaultAspectNames()
     */
    public Set<QName> getDefaultAspectNames()
    {
        return type.getDefaultAspectNames();
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.ClassDefinition#getName()
     */
    public QName getName()
    {
        return QName.createQName(NamespaceService.DICTIONARY_MODEL_1_0_URI, "anonymous#" + type.getName().getLocalName());
    }

    @Override
    public String getDescription()
    {
        return getDescription(staticMessageLookup);
    }
    
    @Override
    public String getTitle()
    {
        return getTitle(staticMessageLookup);
    }
    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.ClassDefinition#getTitle()
     */
    public String getTitle(MessageLookup messageLookup)
    {
        return type.getTitle(messageLookup);
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.ClassDefinition#getDescription()
     */
    public String getDescription(MessageLookup messageLookup)
    {
        return type.getDescription(messageLookup);
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.ClassDefinition#getParentName()
     */
    public QName getParentName()
    {
        return type.getParentName();
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.ClassDefinition#isAspect()
     */
    public boolean isAspect()
    {
        return type.isAspect();
    }

    public Boolean getArchive()
    {
        return type.getArchive();
    }
    
    public Boolean getIncludedInSuperTypeQuery()
    {
        return type.getIncludedInSuperTypeQuery();
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.ClassDefinition#getProperties()
     */
    public Map<QName, PropertyDefinition> getProperties()
    {
        return Collections.unmodifiableMap(properties);
    }
    
    /**
     * @see org.alfresco.service.cmr.dictionary.ClassDefinition#getDefaultValues()
     */
    public Map<QName, Serializable> getDefaultValues()
    {
        Map<QName, Serializable> result = new HashMap<QName, Serializable>(5);
        
        for(Map.Entry<QName, PropertyDefinition> entry : properties.entrySet())
        {
            PropertyDefinition propertyDefinition = entry.getValue();
            String defaultValue = propertyDefinition.getDefaultValue();
            if (defaultValue != null)
            {
                result.put(entry.getKey(), defaultValue);
            }
        }
        
        return Collections.unmodifiableMap(result);
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.ClassDefinition#getAssociations()
     */
    public Map<QName, AssociationDefinition> getAssociations()
    {
        return Collections.unmodifiableMap(associations);
    }


    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.dictionary.ClassDefinition#isContainer()
     */
    public boolean isContainer()
    {
        return !childassociations.isEmpty();
    }

    
    /* (non-Javadoc)
     * @see org.alfresco.repo.dictionary.ClassDefinition#getChildAssociations()
     */
    public Map<QName, ChildAssociationDefinition> getChildAssociations()
    {
        return Collections.unmodifiableMap(childassociations);
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.dictionary.ClassDefinition#getAnalyserResourceBundleName()
     */
    @Override
    public String getAnalyserResourceBundleName()
    {
       return type.getAnalyserResourceBundleName();
    }

    /* (non-Javadoc)
     * @see org.alfresco.service.cmr.dictionary.ClassDefinition#getParentClassDefinition()
     */
    @Override
    public ClassDefinition getParentClassDefinition()
    {
        return type.getParentClassDefinition();
    }

}
