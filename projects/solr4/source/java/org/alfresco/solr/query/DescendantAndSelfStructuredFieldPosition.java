package org.alfresco.solr.query;

public class DescendantAndSelfStructuredFieldPosition extends AnyStructuredFieldPosition
{
    public DescendantAndSelfStructuredFieldPosition()
    {
        super();
    }
    
    public String getDescription()
    {
        return "Descendant and Self Axis";
    }

    public boolean allowsLinkingBySelf()
    {
        return true;
    }

    public boolean isDescendant()
    {
        return true;
    }
    
    

}
