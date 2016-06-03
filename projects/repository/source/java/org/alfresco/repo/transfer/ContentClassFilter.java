
package org.alfresco.repo.transfer;

import java.util.Arrays;
import java.util.Collection;
import java.util.HashSet;
import java.util.Set;

import org.alfresco.service.cmr.dictionary.ClassDefinition;
import org.alfresco.service.cmr.dictionary.DictionaryService;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.repository.NodeService;
import org.alfresco.service.namespace.QName;

/**
 * @author brian
 * 
 */
public class ContentClassFilter extends AbstractNodeFilter
{
    private Set<QName> contentClasses = new HashSet<QName>();
    private Set<QName> aspects = new HashSet<QName>();
    private Set<QName> types = new HashSet<QName>();
    private boolean initialised = false;
    private boolean exclude = false;
    private boolean directOnly = false;

    public ContentClassFilter()
    {
    }

    public ContentClassFilter(QName... contentClasses)
    {
        setContentClasses(contentClasses);
    }

    /*
     * (non-Javadoc)
     * 
     * @see org.alfresco.service.cmr.transfer.NodeFilter#accept(org.alfresco.service.cmr.repository.NodeRef,
     * org.alfresco.service.ServiceRegistry)
     */
    public boolean accept(NodeRef thisNode)
    {
        if (!initialised)
        {
            init();
        }
        NodeService nodeService = serviceRegistry.getNodeService();

        Set<QName> nodesAspects = nodeService.getAspects(thisNode);
        QName type = nodeService.getType(thisNode);

        boolean typeIsInSet = types.contains(type);
        boolean aspectIsInSet = false;
        for (QName aspect : nodesAspects)
        {
            if (aspects.contains(aspect))
            {
                aspectIsInSet = true;
                break;
            }
        }
        return (!exclude && (typeIsInSet || aspectIsInSet)) || (exclude && (!typeIsInSet && !aspectIsInSet));
    }

    public void init()
    {
        super.init();
        DictionaryService dictionaryService = serviceRegistry.getDictionaryService();
        aspects.clear();
        types.clear();
        for (QName contentClass : contentClasses)
        {
            ClassDefinition classDef = dictionaryService.getClass(contentClass);
            if (classDef == null)
            {
                continue;
            }
            if (classDef.isAspect())
            {
                aspects.add(contentClass);
                if (!directOnly)
                {
                    aspects.addAll(dictionaryService.getSubAspects(contentClass, true));
                }
            }
            else
            {
                types.add(contentClass);
                if (!directOnly)
                {
                    types.addAll(dictionaryService.getSubTypes(contentClass, true));
                }
            }

        }
        initialised = true;
    }

    /**
     * Set the classes of content (types and aspects) to filter by.
     * 
     * @param contentClasses
     *            the contentClasses to set
     */
    public void setContentClasses(Collection<QName> contentClasses)
    {
        this.contentClasses = new HashSet<QName>(contentClasses);
        initialised = false;
    }

    /**
     * Set the classes of content (types and aspects) to filter by.
     * 
     * @param contentClasses
     *            the contentClasses to set
     */
    public void setContentClasses(QName... contentClasses)
    {
        this.contentClasses = new HashSet<QName>(Arrays.asList(contentClasses));
        initialised = false;
    }

    /**
     * Specify whether the filter should exclude the specified classes of content.
     * 
     * @param exclude
     *            If true then this filter will not accept content that is of any of the filtered classes of content. If
     *            false then this filter will only accept content that has one or more of the filtered classes of
     *            content. Defaults to false.
     */
    public void setExclude(boolean exclude)
    {
        this.exclude = exclude;
    }

    /**
     * Specify whether the filter should only test against the content classes that have been supplied, or if it should
     * also test against all subclasses of those classes. For example, if "directOnly" is true and "cm:content" is in
     * the set of content classes then a node of type "cm:thumnail" will not be filtered.
     * 
     * @param directOnly
     *            If true then the filter only filters specifically the specified content classes. Defaults to false.
     */
    public void setDirectOnly(boolean directOnly)
    {
        this.directOnly = directOnly;
        initialised = false;
    }

}
