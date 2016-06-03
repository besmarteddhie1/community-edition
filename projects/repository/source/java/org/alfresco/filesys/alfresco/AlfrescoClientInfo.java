package org.alfresco.filesys.alfresco;

import org.alfresco.jlan.server.auth.ClientInfo;
import org.alfresco.service.cmr.repository.NodeRef;


/**
 * Alfresco Client Information Class
 * 
 * <p>Contains additional fields used by the Alfresco filesystem drivers.
 */
public class AlfrescoClientInfo extends ClientInfo {

  // Authentication ticket, used for web access without having to re-authenticate
  
  private String m_authTicket;
  
  // Home folder node
  
  private NodeRef m_homeNode;

  /**
   * Default constructor
   */
  public AlfrescoClientInfo()
  {
      super("", null);
  }
  
  /**
   * Class constructor
   * 
   * @param user User name
   * @param pwd Password
   */
  public AlfrescoClientInfo(String user, byte[] pwd)
  {
    super(user, pwd);
  }

  /**
   * Check if the client has an authentication ticket
   * 
   * @return boolean
   */
  public final boolean hasAuthenticationTicket()
  {
    return m_authTicket != null ? true : false;
  }
  
  /**
   * Return the authentication ticket
   * 
   * @return String
   */
  public final String getAuthenticationTicket()
  {
    return m_authTicket;
  }
  
  /**
   * Check if the client has a home folder node
   * 
   * @return boolean
   */
  public final boolean hasHomeFolder()
  {
      return m_homeNode != null ? true : false;
  }
  
  /**
   * Return the home folder node
   * 
   * @return NodeRef
   */
  public final NodeRef getHomeFolder()
  {
      return m_homeNode;
  }

  /**
   * Set the authentication ticket
   * 
   * @param ticket String
   */
  public final void setAuthenticationTicket(String ticket)
  {
    m_authTicket = ticket;
  }
  
  /**
   * Set the home folder node
   * 
   * @param homeNode NodeRef
   */
  public final void setHomeFolder(NodeRef homeNode)
  {
      m_homeNode = homeNode;
  }
  
}
