package org.alfresco.repo.tenant;

import org.alfresco.api.AlfrescoPublicApi;
import org.alfresco.service.cmr.repository.AssociationRef;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.cmr.repository.StoreRef;
import org.alfresco.service.cmr.search.SearchService;
import org.alfresco.service.namespace.NamespaceService;
import org.alfresco.service.namespace.QName;


/**
 * Tenant Service interface.
 * <p>
 * This interface provides methods to support either ST or MT implementations.
 *
 */
@AlfrescoPublicApi
public interface TenantService extends TenantUserService
{
    public static final String SEPARATOR = "@";
    
    public static final String DEFAULT_DOMAIN = "";
    
    /**
     * @return          the reference <b>with</b> the tenant-specific ID attached
     */
    public NodeRef getName(NodeRef nodeRef);
    
    /**
     * @return          the reference <b>with</b> the tenant-specific ID attached
     */
    public NodeRef getName(NodeRef inNodeRef, NodeRef nodeRef);
    
    /**
     * @return          the reference <b>with</b> the tenant-specific ID attached
     */
    public StoreRef getName(StoreRef storeRef);
    
    /**
     * @return          the reference <b>with</b> the tenant-specific ID attached
     */
    public ChildAssociationRef getName(ChildAssociationRef childAssocRef);
    
    /**
     * @return          the reference <b>with</b> the tenant-specific ID attached
     */
    public AssociationRef getName(AssociationRef assocRef);
    
    /**
     * @return          the reference <b>with</b> the tenant-specific ID attached
     */
    public StoreRef getName(String username, StoreRef storeRef);
    
    /**
     * @return          the reference <b>with</b> the tenant-specific ID attached
     */
    public QName getName(QName name);
    
    /**
     * @return          the reference <b>with</b> the tenant-specific ID attached
     */
    public QName getName(NodeRef inNodeRef, QName name);
    
    /**
     * @return          the reference <b>with</b> the tenant-specific ID attached
     */
    public String getName(String name);
    
    /**
     * @return          the reference <b>without</b> the tenant-specific ID attached
     */
    public QName getBaseName(QName name, boolean forceIfNonTenant);
    
    /**
     * @return          the reference <b>without</b> the tenant-specific ID attached
     */
    public NodeRef getBaseName(NodeRef nodeRef);
    
    /**
     * @return          the reference <b>without</b> the tenant-specific ID attached
     */
    public NodeRef getBaseName(NodeRef nodeRef, boolean forceForNonTenant);
    
    /**
     * @return          the reference <b>without</b> the tenant-specific ID attached
     */
    public StoreRef getBaseName(StoreRef storeRef);
    
    /**
     * @return          the reference <b>without</b> the tenant-specific ID attached
     */
    public ChildAssociationRef getBaseName(ChildAssociationRef childAssocRef);
    
    /**
     * @return          the reference <b>without</b> the tenant-specific ID attached
     */
    public ChildAssociationRef getBaseName(ChildAssociationRef childAssocRef, boolean forceIfNonTenant);
    
    /**
     * @return          the reference <b>without</b> the tenant-specific ID attached
     */
    public AssociationRef getBaseName(AssociationRef assocRef);
    
    /**
     * @return          the reference <b>without</b> the tenant-specific ID attached
     */
    public String getBaseName(String name);
    
    /**
     * @return          the reference <b>without</b> the tenant-specific ID attached
     */
    public String getBaseName(String name, boolean forceIfNonTenant);
    
    public void checkDomainUser(String username);
    
    public void checkDomain(String name);
    
    public NodeRef getRootNode(NodeService nodeService, SearchService searchService, NamespaceService namespaceService, String rootPath, NodeRef rootNodeRef);
    
    public boolean isTenantUser();
    
    public boolean isTenantUser(String username);
    
    public boolean isTenantName(String name);
    
    public String getUserDomain(String username);
    
    public Tenant getTenant(String tenantDomain);
    
    /**
     * @return          the tenant-specific ID for specified identifier
     */
    public String getDomain(String name);
    
    /**
     * @return          the tenant-specific ID for specified identifier
     */
    public String getDomain(String name, boolean checkCurrentDomain);

    /**
     * Get the primary domain for user, if one exists.
     *  
     * @param user The user whose primary domain is to be returned 
     * @return The primary domain of user, or null if the domain does not exist in the system.
     */
	public String getPrimaryDomain(String user);
}
