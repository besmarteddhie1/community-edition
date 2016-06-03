package org.alfresco.repo.site;

import java.io.Serializable;
import java.util.Map;

import org.alfresco.model.ContentModel;
import org.alfresco.repo.node.NodeServicePolicies;
import org.alfresco.repo.node.NodeServicePolicies.OnMoveNodePolicy;
import org.alfresco.repo.node.NodeServicePolicies.OnUpdatePropertiesPolicy;
import org.alfresco.repo.policy.Behaviour;
import org.alfresco.repo.policy.JavaBehaviour;
import org.alfresco.repo.policy.PolicyComponent;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.ChildAssociationRef;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;
import org.alfresco.util.EqualsHelper;

/**
 * Site aspect behaviour bean.
 * 
 * Renames are not allowed, because the relationship between a site and 
 * its authorities is based on a pattern using the name.
 * 
 * @author Nick Burch
 */
public class SiteAspect implements NodeServicePolicies.OnMoveNodePolicy, NodeServicePolicies.OnUpdatePropertiesPolicy
{
   /** Services */
   private DictionaryService dictionaryService;
   private PolicyComponent policyComponent;
   private NodeService nodeService;
   
   /**
    * Set the dictionary service
    * 
    * @param dictionaryService   dictionary service
    */
   public void setDictionaryService(DictionaryService dictionaryService)
   {
       this.dictionaryService = dictionaryService;
   }
   
   /**
    * Set the policy component
    * 
    * @param policyComponent   policy component
    */
   public void setPolicyComponent(PolicyComponent policyComponent)
   {
       this.policyComponent = policyComponent;
   }
   
   /**
    * Set the node service
    * 
    * @param nodeService   node service
    */
   public void setNodeService(NodeService nodeService)
   {
       this.nodeService = nodeService;
   }
   
   /**
    * Initialise method
    */
   public void init()
   {
       this.policyComponent.bindClassBehaviour(OnMoveNodePolicy.QNAME, 
               SiteModel.TYPE_SITE, 
               new JavaBehaviour(this, "onMoveNode", Behaviour.NotificationFrequency.EVERY_EVENT));
       
       this.policyComponent.bindClassBehaviour(OnMoveNodePolicy.QNAME,
               SiteModel.ASPECT_SITE_CONTAINER,
               new JavaBehaviour(this, "onMoveNode", Behaviour.NotificationFrequency.EVERY_EVENT));
       
       this.policyComponent.bindClassBehaviour(OnUpdatePropertiesPolicy.QNAME, 
               SiteModel.TYPE_SITE, 
               new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.EVERY_EVENT));
       
       this.policyComponent.bindClassBehaviour(OnUpdatePropertiesPolicy.QNAME,
               SiteModel.ASPECT_SITE_CONTAINER,
               new JavaBehaviour(this, "onUpdateProperties", Behaviour.NotificationFrequency.EVERY_EVENT));
   }

   public void onUpdateProperties(NodeRef nodeRef,
           Map<QName, Serializable> before,
           Map<QName, Serializable> after)
   {
	   String beforeName = (String)before.get(ContentModel.PROP_NAME);
	   String afterName = (String)after.get(ContentModel.PROP_NAME);

	   if(beforeName != null && !beforeName.equals(afterName))
	   {
		   // Deny renames
		   QName type = nodeService.getType(nodeRef);
		   if (dictionaryService.isSubClass(type, SiteModel.TYPE_SITE))
		   {
			   throw new SiteServiceException("Sites can not be renamed.");
		   }
		   else
		   {
			   throw new SiteServiceException("Site containers can not be renamed.");
		   }
	   }
   }

   /**
    * Deny renames.
    */
   public void onMoveNode(ChildAssociationRef oldChildAssocRef,
         ChildAssociationRef newChildAssocRef) 
   {
      NodeRef oldParent = oldChildAssocRef.getParentRef();
      NodeRef newParent = newChildAssocRef.getParentRef();
      
      // Deny renames
      if (oldParent.equals(newParent))
      {
          QName type = nodeService.getType((oldChildAssocRef.getChildRef()));
          if (dictionaryService.isSubClass(type, SiteModel.TYPE_SITE))
          {
              throw new SiteServiceException("Sites can not be renamed.");
          }
          else
          {
              throw new SiteServiceException("Site containers can not be renamed.");
          }
      }
   }
}
