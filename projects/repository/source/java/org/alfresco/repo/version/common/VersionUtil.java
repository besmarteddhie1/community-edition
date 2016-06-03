package org.alfresco.repo.version.common;

import java.io.Serializable;
import java.util.Collection;
import java.util.Date;
import java.util.HashSet;
import java.util.Map;
import java.util.Set;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.version.Version2Model;
import org.alfresco.repo.version.VersionBaseModel;
import org.alfresco.repo.version.VersionModel;
import org.alfresco.service.cmr.repository.InvalidNodeRefException;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.version.ReservedVersionNameException;
import org.alfresco.service.namespace.QName;

/**
 * Helper class containing helper methods for the versioning services.
 *
 * @author Roy Wetherall
 */
public class VersionUtil
{
    /**
     * Reserved property names
     */
    public static final String[] RESERVED_PROPERTY_NAMES = new String[]{
        VersionModel.PROP_FROZEN_NODE_ID,               // deprecated (since 3.1)
        VersionModel.PROP_FROZEN_NODE_STORE_ID,         // deprecated (since 3.1)
        VersionModel.PROP_FROZEN_NODE_STORE_PROTOCOL,   // deprecated (since 3.1)
        VersionModel.PROP_FROZEN_NODE_TYPE,             // deprecated (since 3.1)
        VersionModel.PROP_FROZEN_ASPECTS,               // deprecated (since 3.1)
        VersionBaseModel.PROP_CREATED_DATE,
        VersionBaseModel.PROP_VERSION_LABEL,
        VersionBaseModel.PROP_VERSION_NUMBER,           // deprecated (since 3.3)
        VersionBaseModel.PROP_VERSION_DESCRIPTION,
        Version2Model.PROP_FROZEN_NODE_DBID,
        Version2Model.PROP_FROZEN_CREATED,
        Version2Model.PROP_FROZEN_CREATOR,
        Version2Model.PROP_FROZEN_MODIFIED,
        Version2Model.PROP_FROZEN_MODIFIER,
        Version2Model.PROP_FROZEN_ACCESSED};

    /**
     * Checks that the names of the additional version properties are valid and that they do not clash
     * with the reserved properties.
     *
     * @param names  the property names
     * @throws                   ReservedVersionNameException
     */
    public static void checkVersionPropertyNames(Collection<String> names)
        throws ReservedVersionNameException
    {
        for (String name : RESERVED_PROPERTY_NAMES)
        {
            if (names.contains(name) == true)
            {
                throw new ReservedVersionNameException(name);
            }
        }
    }

    /**
     * Create Version Store Ref
     * 
     * @param  storeRef ref
     * @return  store ref for version store
     */
    public static StoreRef convertStoreRef(StoreRef storeRef)
    {
        return new StoreRef(StoreRef.PROTOCOL_WORKSPACE, storeRef.getIdentifier());
    }
    
    /**
     * Convert the incomming node ref (with the version store protocol specified)
     * to the internal representation with the workspace protocol.
     *
     * @param nodeRef   the incomming verison protocol node reference
     * @return          the internal version node reference
     */
    public static NodeRef convertNodeRef(NodeRef nodeRef)
    {
        return new NodeRef(convertStoreRef(nodeRef.getStoreRef()), nodeRef.getId());
    }
    
    
    public static void convertFrozenToOriginalProps(Map<QName, Serializable> props) throws InvalidNodeRefException
    {
        if (props != null)
        {
            props.remove(Version2Model.PROP_QNAME_VERSION_DESCRIPTION);
            props.remove(Version2Model.PROP_QNAME_VERSION_NUMBER);
            
            Set<QName> keys = new HashSet<QName>(props.keySet());
            for (QName key : keys)
            {
                String keyName = key.getLocalName();
                int idx = keyName.indexOf(Version2Model.PROP_METADATA_PREFIX);
                if (idx == 0)
                {
                    props.remove(key);
                }
            }
            
            String versionLabel = (String)props.get(Version2Model.PROP_QNAME_VERSION_LABEL);
            props.put(ContentModel.PROP_VERSION_LABEL, versionLabel);
            props.remove(Version2Model.PROP_QNAME_VERSION_LABEL);
            
            // Convert frozen sys:referenceable properties
            NodeRef nodeRef = (NodeRef)props.get(Version2Model.PROP_QNAME_FROZEN_NODE_REF);
            if (nodeRef != null)
            {
                props.put(ContentModel.PROP_STORE_PROTOCOL, nodeRef.getStoreRef().getProtocol());
                props.put(ContentModel.PROP_STORE_IDENTIFIER, nodeRef.getStoreRef().getIdentifier());
                props.put(ContentModel.PROP_NODE_UUID, nodeRef.getId());
            }
            props.remove(Version2Model.PROP_QNAME_FROZEN_NODE_REF);

            Long dbid = (Long)props.get(Version2Model.PROP_QNAME_FROZEN_NODE_DBID);
            props.put(ContentModel.PROP_NODE_DBID, dbid);
            props.remove(Version2Model.PROP_QNAME_FROZEN_NODE_DBID);
            
            // Convert frozen cm:auditable properties

            String creator = (String)props.get(Version2Model.PROP_QNAME_FROZEN_CREATOR);
            if (creator != null)
            {
                props.put(ContentModel.PROP_CREATOR, creator);
            }
            props.remove(Version2Model.PROP_QNAME_FROZEN_CREATOR);
            
            Date created = (Date)props.get(Version2Model.PROP_QNAME_FROZEN_CREATED);
            if (created != null)
            {
                props.put(ContentModel.PROP_CREATED, created);
            }
            props.remove(Version2Model.PROP_QNAME_FROZEN_CREATED);
            
            // TODO - check use-cases for get version, revert, restore ....
            String modifier = (String)props.get(Version2Model.PROP_QNAME_FROZEN_MODIFIER);
            if (modifier != null)
            {
                props.put(ContentModel.PROP_MODIFIER, modifier);
            }
            props.remove(Version2Model.PROP_QNAME_FROZEN_MODIFIER);
            
            Date modified = (Date)props.get(Version2Model.PROP_QNAME_FROZEN_MODIFIED);
            if (modified != null)
            {
                props.put(ContentModel.PROP_MODIFIED, modified);
            }
            props.remove(Version2Model.PROP_QNAME_FROZEN_MODIFIED);

            Date accessed = (Date)props.get(Version2Model.PROP_QNAME_FROZEN_ACCESSED);
            if (accessed != null)
            {
                props.put(ContentModel.PROP_ACCESSED, accessed);
            }
            props.remove(Version2Model.PROP_QNAME_FROZEN_ACCESSED);
        }
    }
}
