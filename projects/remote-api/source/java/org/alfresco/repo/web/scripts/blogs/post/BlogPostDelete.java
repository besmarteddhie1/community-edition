package org.alfresco.repo.web.scripts.blogs.post;

import java.text.MessageFormat;
import java.util.HashMap;
import java.util.Map;
import java.util.ResourceBundle;

import org.alfresco.repo.web.scripts.blogs.AbstractBlogWebScript;
import org.alfresco.service.cmr.blog.BlogPostInfo;
import org.alfresco.service.cmr.repository.NodeRef;
import org.alfresco.service.cmr.site.SiteInfo;
import org.json.simple.JSONObject;
import org.springframework.extensions.webscripts.Cache;
import org.springframework.extensions.webscripts.Status;
import org.springframework.extensions.webscripts.WebScriptException;
import org.springframework.extensions.webscripts.WebScriptRequest;

/**
 * This class is the controller for the blog-posts.get web script.
 * 
 * @author Neil Mc Erlean (based on existing JavaScript webscript controllers)
 * @since 4.0
 */
public class BlogPostDelete extends AbstractBlogWebScript
{
    protected static final String MSG_BLOG_DELETED = "blog-post.msg.deleted";
    
    @Override
    protected Map<String, Object> executeImpl(SiteInfo site, NodeRef nodeRef,
         BlogPostInfo blog, WebScriptRequest req, JSONObject json, Status status, Cache cache) 
    {
        final ResourceBundle rb = getResources();
        
        if (blog == null)
        {
           throw new WebScriptException(Status.STATUS_NOT_FOUND, "Blog Post Not Found");
        }
        
        // TODO Get this from the BlogPostInfo Object
        final boolean isDraftBlogPost = blogService.isDraftBlogPost(blog.getNodeRef());
        
        // Have it deleted
        blogService.deleteBlogPost(blog);
        
        // If we're in a site, and it isn't a draft, add an activity
        if (site != null && !isDraftBlogPost)
        {
            addActivityEntry("deleted", blog, site, req, json, nodeRef);
        }

        // Report it as deleted
        Map<String, Object> model = new HashMap<String, Object>();
        String message = rb.getString(MSG_BLOG_DELETED);
        model.put("message",MessageFormat.format(message, blog.getNodeRef()));
        return model;
    }
}
