
package org.alfresco.repo.webdav.auth;

import java.io.IOException;
import java.nio.ByteBuffer;
import java.nio.charset.CharacterCodingException;
import java.nio.charset.Charset;
import java.nio.charset.CharsetDecoder;
import java.nio.charset.CodingErrorAction;
import java.util.HashSet;
import java.util.LinkedHashMap;
import java.util.LinkedHashSet;
import java.util.Map;
import java.util.Set;

import javax.servlet.FilterChain;
import javax.servlet.ServletContext;
import javax.servlet.ServletException;
import javax.servlet.ServletRequest;
import javax.servlet.ServletResponse;
import javax.servlet.http.HttpServletRequest;
import javax.servlet.http.HttpServletResponse;

import org.alfresco.repo.SessionUser;
import org.alfresco.repo.security.authentication.AuthenticationException;
import org.alfresco.repo.security.authentication.AuthenticationUtil;
import org.alfresco.repo.security.authentication.Authorization;
import org.alfresco.repo.web.auth.BasicAuthCredentials;
import org.alfresco.repo.web.auth.TicketCredentials;
import org.alfresco.repo.web.filter.beans.DependencyInjectedFilter;
import org.alfresco.repo.webdav.WebDAV;
import org.alfresco.service.cmr.security.NoSuchPersonException;
import org.apache.commons.codec.binary.Base64;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;

/**
 * WebDAV Authentication Filter Class
 * 
 * @author GKSpencer
 */
public class AuthenticationFilter extends BaseAuthenticationFilter implements DependencyInjectedFilter
{
    // Debug logging
    
    private static Log logger = LogFactory.getLog(AuthenticationFilter.class);
    
    // Authenticated user session object name

    private static final String PPT_EXTN = ".ppt";
    
    /** The password encodings to try in priority order **/
    private static final String[] ENCODINGS;
    
    static
    {
        String[] encodings = new String[] {
                "UTF-8", 
                System.getProperty("file.encoding"),
                "ISO-8859-1"
            };
        
        Set<String> encodingsSet = new LinkedHashSet<String>();
        for (String encoding : encodings)
        {
            encodingsSet.add(encoding);
        }
        ENCODINGS = new String[encodingsSet.size()];
        encodingsSet.toArray(ENCODINGS);
    }
    
    // Various services required by NTLM authenticator
    
    /**
     * Run the authentication filter
     * 
     * @param context ServletContext
     * @param req ServletRequest
     * @param resp ServletResponse
     * @param chain FilterChain
     * @exception ServletException
     * @exception IOException
     */
    public void doFilter(ServletContext context, ServletRequest req, ServletResponse resp, FilterChain chain)
            throws IOException, ServletException
    {
        if (logger.isDebugEnabled())
            logger.debug("Entering AuthenticationFilter.");
        
        // Assume it's an HTTP request

        HttpServletRequest httpReq = (HttpServletRequest) req;
        HttpServletResponse httpResp = (HttpServletResponse) resp;

        // Get the user details object from the session
        SessionUser user = getSessionUser(context, httpReq, httpResp, false);

        if (user == null)
        {
            if (logger.isDebugEnabled())
                logger.debug("There is no user in the session.");
            // Get the authorization header
            
            String authHdr = httpReq.getHeader("Authorization");
            
            if ( authHdr != null && authHdr.length() > 5 && authHdr.substring(0,5).equalsIgnoreCase("BASIC"))
            {
                if (logger.isDebugEnabled())
                    logger.debug("Basic authentication details present in the header.");
                byte[] encodedString = Base64.decodeBase64(authHdr.substring(5).getBytes());
                
                // ALF-13621: Due to browser inconsistencies we have to try a fallback path of encodings
                Set<String> attemptedAuths = new HashSet<String>(ENCODINGS.length * 2);
                for (String encoding : ENCODINGS)
                {
                    CharsetDecoder decoder = Charset.forName(encoding).newDecoder()
                            .onMalformedInput(CodingErrorAction.REPORT);
                    try
                    {
                        // Attempt to decode using this charset 
                        String basicAuth = decoder.decode(ByteBuffer.wrap(encodedString)).toString();
                        
                        // It decoded OK but we may already have tried this string.
                        if (!attemptedAuths.add(basicAuth))
                        {
                            // Already tried - no need to try again
                            continue;
                        }
                        
                        String username = null;
                        String password = null;
    
                        // Split the username and password
                        int pos = basicAuth.indexOf(":");
                        if (pos != -1)
                        {
                            username = basicAuth.substring(0, pos);
                            password = basicAuth.substring(pos + 1);
                        }
                        else
                        {
                            username = basicAuth;
                            password = "";
                        }
    
                        // Go to the repo and authenticate
                        Authorization auth = new Authorization(username, password);
                        if (auth.isTicket())
                        {
                            authenticationService.validate(auth.getTicket());
                        }
                        else
                        {
                            authenticationService.authenticate(username, password.toCharArray());
                            authenticationListener.userAuthenticated(new BasicAuthCredentials(username, password));
                        }
                        
                        user = createUserEnvironment(httpReq.getSession(), authenticationService.getCurrentUserName(), authenticationService.getCurrentTicket(), false);
                        
                        // Success so break out
                        break;
                    }
                    catch (CharacterCodingException e)
                    {
                        if (logger.isDebugEnabled())
                            logger.debug("Didn't decode using " + decoder.getClass().getName(), e);
                    }
                    catch (AuthenticationException ex)
                    {
                        if (logger.isDebugEnabled())
                            logger.debug("Authentication error ", ex);
                    }
                    catch (NoSuchPersonException e)
                    {
                        if (logger.isDebugEnabled())
                            logger.debug("There is no such person error ", e);
                    }
                }
            }
            else
            {
                // Check if the request includes an authentication ticket

                String ticket = req.getParameter(ARG_TICKET);

                if (ticket != null && ticket.length() > 0)
                {
                    // PowerPoint bug fix
                    if (ticket.endsWith(PPT_EXTN))
                    {
                        ticket = ticket.substring(0, ticket.length() - PPT_EXTN.length());
                    }

                    // Debug

                    if (logger.isDebugEnabled())
                        logger.debug("Logon via ticket from " + req.getRemoteHost() + " (" + req.getRemoteAddr() + ":"
                                + req.getRemotePort() + ")" + " ticket=" + ticket);

                    // Validate the ticket

                    authenticationService.validate(ticket);
                    authenticationListener.userAuthenticated(new TicketCredentials(ticket));

                    // Need to create the User instance if not already available

                    String currentUsername = authenticationService.getCurrentUserName();

                    user = createUserEnvironment(httpReq.getSession(), currentUsername, ticket, false);
                }
            }

            // Check if the user is authenticated, if not then prompt again
            
            if (user == null)
            {
                if (logger.isDebugEnabled())
                    logger.debug("No user/ticket, force the client to prompt for logon details.");
    
                httpResp.setHeader("WWW-Authenticate", "BASIC realm=\"Alfresco DAV Server\"");
                httpResp.setStatus(HttpServletResponse.SC_UNAUTHORIZED);
    
                httpResp.flushBuffer();
                return;
            }
        }
        else
        {
            authenticationListener.userAuthenticated(new TicketCredentials(user.getTicket()));
        }

        // Chain other filters

        chain.doFilter(req, resp);
    }

    /**
     * Cleanup filter resources
     */
    public void destroy()
    {
        // Nothing to do
    }

    /* (non-Javadoc)
     * @see org.alfresco.repo.webdav.auth.BaseAuthenticationFilter#getLogger()
     */
    protected Log getLogger()
    {
        return logger;
    }
}
