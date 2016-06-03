package org.alfresco.repo.content.caching.test;

import static org.junit.Assert.assertEquals;
import static org.junit.Assert.assertTrue;

import org.alfresco.repo.content.ContentContext;
import org.alfresco.repo.content.caching.CachingContentStore;
import org.alfresco.repo.content.caching.ContentCacheImpl;
import org.alfresco.util.ApplicationContextHelper;
import org.apache.commons.logging.Log;
import org.apache.commons.logging.LogFactory;
import org.junit.Before;
import org.junit.BeforeClass;
import org.junit.Test;
import org.springframework.context.support.ClassPathXmlApplicationContext;

/**
 * Tests that exercise the CachingContentStore in conjunction with a backing store
 * that runs deliberately slowly.
 * 
 * @author Matt Ward
 */
public class SlowContentStoreTest
{
    private static ClassPathXmlApplicationContext ctx;
    private CachingContentStore cachingStore;
    private static final Log logger = LogFactory.getLog(SlowContentStoreTest.class);


    @BeforeClass
    public static void setUpClass()
    {        
        String slowconf = "classpath:cachingstore/test-slow-context.xml";
        ctx = (ClassPathXmlApplicationContext) ApplicationContextHelper.getApplicationContext(new String[] { slowconf });    
    }
    
    @Before
    public void setUp()
    {
        cachingStore = (CachingContentStore) ctx.getBean("cachingContentStore");
        cachingStore.setCacheOnInbound(false);
        
        // Clear the cache before each test case.
        ContentCacheImpl cache = (ContentCacheImpl) ctx.getBean("contentCache");
        cache.removeAll();
    }


    @Test
    public void readsAreFasterFromCache()
    {
        // First read will hit the SLOW backing store
        TimedStoreReader storeReader = new TimedStoreReader();
        storeReader.execute();
        assertTrue("First read should take a while", storeReader.timeTakenMillis() > 1000);
        logger.debug(String.format("First read took %ds", storeReader.timeTakenMillis()));
        // The content came from the slow backing store...
        assertEquals("This is the content for my slow ReadableByteChannel", storeReader.content);
        
        // Subsequent reads will hit the cache
        for (int i = 0; i < 5; i++)
        {
            storeReader = new TimedStoreReader();
            storeReader.execute();
            assertTrue("Subsequent reads should be fast", storeReader.timeTakenMillis() < 100);
            logger.debug(String.format("Cache read took %ds", storeReader.timeTakenMillis()));
            // The content came from the slow backing store, but was cached...
            assertEquals("This is the content for my slow ReadableByteChannel", storeReader.content);
        }
    }
    
    
    @Test
    public void writeThroughCacheResultsInFastReadFirstTime()
    {
        cachingStore.setCacheOnInbound(true);

        // This content will be cached on the way in
        cachingStore.getWriter(new ContentContext(null, "any-url")).
            putContent("Content written from " + getClass().getSimpleName());
        
        // First read will hit cache
        TimedStoreReader storeReader = new TimedStoreReader();
        storeReader.execute();
        assertTrue("First read should be fast", storeReader.timeTakenMillis() < 100);
        logger.debug(String.format("First read took %ds", storeReader.timeTakenMillis()));
        assertEquals("Content written from " + getClass().getSimpleName(), storeReader.content);
        
        // Subsequent reads will also hit the cache
        for (int i = 0; i < 5; i++)
        {
            storeReader = new TimedStoreReader();
            storeReader.execute();
            assertTrue("Subsequent reads should be fast", storeReader.timeTakenMillis() < 100);
            logger.debug(String.format("Cache read took %ds", storeReader.timeTakenMillis()));
            // The original cached content, still cached...
            assertEquals("Content written from " + getClass().getSimpleName(), storeReader.content);
        }
    }
    
    
    private class TimedStoreReader extends TimedExecutor
    {
        String content;
        
        @Override
        protected void doExecute()
        {
            content = cachingStore.getReader("any-url").getContentString();
            logger.debug("Read content: " + content);
        }   
    }
    
    private abstract class TimedExecutor
    {
        private long start;
        private long finish;
        
        public void execute()
        {
            start = System.currentTimeMillis();
            doExecute();
            finish = System.currentTimeMillis();
        }
        
        public long timeTakenMillis()
        {
            return finish - start;
        }
        
        protected abstract void doExecute();
    }
}
