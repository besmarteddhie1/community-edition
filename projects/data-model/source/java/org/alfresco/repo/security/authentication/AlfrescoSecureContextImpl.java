package org.alfresco.repo.security.authentication;

import net.sf.acegisecurity.Authentication;
import net.sf.acegisecurity.context.ContextInvalidException;

/**
 * Hold an Alfresco extended security context
 * 
 * @author andyh
 *
 */
public class AlfrescoSecureContextImpl implements AlfrescoSecureContext
{
    private static final long serialVersionUID = -8893133731693272549L;

    private Authentication realAuthentication;

    private Authentication effectiveAuthentication;

    /**
     * ACEGI
     */
    public Authentication getAuthentication()
    {
        return getEffectiveAuthentication();
    }

    /**
     * ACEGI
     */
    public void setAuthentication(Authentication newAuthentication)
    {
        setEffectiveAuthentication(newAuthentication);
    }

    /**
     * ACEGI
     */
    public void validate() throws ContextInvalidException
    {
        if (effectiveAuthentication == null)
        {
            throw new ContextInvalidException("Effective authentication not set");
        }
    }

    public Authentication getEffectiveAuthentication()
    {
        return effectiveAuthentication;
    }

    public Authentication getRealAuthentication()
    {
        return realAuthentication;
    }

    public void setEffectiveAuthentication(Authentication effictiveAuthentication)
    {
        this.effectiveAuthentication = effictiveAuthentication;
    }

    public void setRealAuthentication(Authentication realAuthentication)
    {
        this.realAuthentication = realAuthentication;
    }

    @Override
    public int hashCode()
    {
        final int PRIME = 31;
        int result = 1;
        result = PRIME * result + ((effectiveAuthentication == null) ? 0 : effectiveAuthentication.hashCode());
        result = PRIME * result + ((realAuthentication == null) ? 0 : realAuthentication.hashCode());
        return result;
    }

    @Override
    public boolean equals(Object obj)
    {
        if (this == obj)
            return true;
        if (obj == null)
            return false;
        if (getClass() != obj.getClass())
            return false;
        final AlfrescoSecureContextImpl other = (AlfrescoSecureContextImpl) obj;
        if (effectiveAuthentication == null)
        {
            if (other.effectiveAuthentication != null)
                return false;
        }
        else if (!effectiveAuthentication.equals(other.effectiveAuthentication))
            return false;
        if (realAuthentication == null)
        {
            if (other.realAuthentication != null)
                return false;
        }
        else if (!realAuthentication.equals(other.realAuthentication))
            return false;
        return true;
    }

    @Override
    public String toString()
    {
        StringBuilder builder = new StringBuilder();

        if (realAuthentication == null)
        {
            builder.append("Real authenticaion = null");
        }
        else
        {
            builder.append("Real authenticaion = " + realAuthentication.toString());
        }
        builder.append(", ");
        
        if (effectiveAuthentication == null)
        {
            builder.append("Effective authenticaion = null");
        }
        else
        {
            builder.append("Effective authenticaion = " + effectiveAuthentication.toString());
        }
        builder.append(", ");
        
        return builder.toString();
    }

}
