package org.alfresco.repo.management.subsystems.test;

import java.io.IOException;
import java.net.InetAddress;
import java.net.ServerSocket;
import java.net.SocketTimeoutException;
import java.util.HashMap;
import java.util.Map;

import org.alfresco.repo.management.subsystems.ApplicationContextFactory;
import org.alfresco.repo.management.subsystems.ChildApplicationContextFactory;
import org.alfresco.repo.management.subsystems.InvalidPropertyValueException;
import org.alfresco.repo.management.subsystems.SubsystemEarlyPropertyChecker;
import org.alfresco.test_category.OwnJVMTestsCategory;
import org.alfresco.util.ApplicationContextHelper;
import org.alfresco.util.BaseSpringTest;
import org.apache.commons.logging.LogFactory;
import org.junit.experimental.categories.Category;
import org.springframework.context.ConfigurableApplicationContext;

/**
 * Ensures the following features of subsystems are working:
 * <ul>
 * <li>Subsystem default properties
 * <li>Global property overrides (via alfresco-global.properties)
 * <li>Subsystem instance specific property overrides (via extension classpath)
 * <li>Subsystem instance specific Spring overrides (via extension classpath)
 * <li>Composite property defaults
 * <li>Composite property instance overrides
 * </ul>
 * 
 * @see ChildApplicationContextFactory
 * @author dward
 */
@Category(OwnJVMTestsCategory.class)
public class SubsystemsTest extends BaseSpringTest
{
    volatile boolean shouldBlockPort;
    
    /*
     * (non-Javadoc)
     * @see org.alfresco.util.BaseSpringTest#getConfigLocations()
     */
    @Override
    protected String[] getConfigLocations()
    {
        return new String[]
        {
            ApplicationContextHelper.CONFIG_LOCATIONS[0], "classpath:subsystem-test-context.xml"
        };
    }

    /**
     * Test subsystems.
     * 
     * @throws Exception
     *             the exception
     */
    public void testSubsystems() throws Exception
    {
        ApplicationContextFactory subsystem = (ApplicationContextFactory) getApplicationContext().getBean(
                "testsubsystem");
        ConfigurableApplicationContext childContext = (ConfigurableApplicationContext) subsystem
                .getApplicationContext();
        assertTrue("Subsystem not started", childContext.isActive());
        TestService testService = (TestService) childContext.getBean("testService");
        // Make sure subsystem defaults work
        assertEquals("Subsystem Default1", testService.getSimpleProp1());
        // Make sure global property overrides work
        assertEquals(true, testService.getSimpleProp2().booleanValue());
        // Make sure extension classpath property overrides work
        assertEquals("Instance Override3", testService.getSimpleProp3());
        // Make sure extension classpath Spring overrides work
        assertEquals("An extra bean I changed", childContext.getBean("anotherBean"));
        // Make sure composite properties and their defaults work
        TestBean[] testBeans = testService.getTestBeans();
        assertNotNull("Composite property not set", testBeans);
        assertEquals(3, testBeans.length);
        assertEquals("inst1", testBeans[0].getId());
        assertEquals(false, testBeans[0].isBoolProperty());
        assertEquals(123456789123456789L, testBeans[0].getLongProperty());
        assertEquals("Global Default", testBeans[0].getAnotherStringProperty());
        assertEquals("inst2", testBeans[1].getId());
        assertEquals(true, testBeans[1].isBoolProperty());
        assertEquals(123456789123456789L, testBeans[1].getLongProperty());
        assertEquals("Global Default", testBeans[1].getAnotherStringProperty());
        assertEquals("inst3", testBeans[2].getId());
        assertEquals(false, testBeans[2].isBoolProperty());
        assertEquals(123456789123456789L, testBeans[2].getLongProperty());
        assertEquals("Global Instance Default", testBeans[2].getAnotherStringProperty());
    }
    
    private void blockPort(final String host, final int portNumber)
    {
        shouldBlockPort = true;
        
        new Thread()
        {
            public void run()
            {
                ServerSocket serverSocket = null;

                try
                {
                    if (host != null && !host.equals("0.0.0.0"))
                    {
                        serverSocket = new ServerSocket(portNumber, 0, InetAddress.getByName(host));
                    }
                    else
                    {
                        serverSocket = new ServerSocket(portNumber);
                    }
                    
                    serverSocket.setSoTimeout(200);
                    
                    do
                    {
                        try
                        {
                            serverSocket.accept();
                        }
                        catch (SocketTimeoutException ste)
                        {
                            //We're expecting this.
                        }
                    }
                    while (shouldBlockPort);
                }
                catch (IOException ex)
                {
                    LogFactory.getLog(SubsystemsTest.class).error(ex.getMessage());
                }
                finally
                {
                    if (serverSocket != null)
                    {
                        try
                        {
                            serverSocket.close();
                        }
                        catch (Exception ex)
                        {
                            LogFactory.getLog(SubsystemsTest.class).error(ex.getMessage());
                        }
                    }
                }
            }
        }.start();
    }

    private void unblockPort()
    {
        shouldBlockPort = false;
    }

    public void testAbstractPropertyBackedBean_performEarlyPropertyChecks_PortEarlyPropertyChecker()
    {
        int testPortNumber = (Integer) getApplicationContext().getBean("testPortNumber");

        ChildApplicationContextFactory testBean = (ChildApplicationContextFactory) getApplicationContext().getBean("testsubsystem");

        Map<String, String> testProperties = new HashMap<String, String>();

        // Check for empty port value error:
        testProperties.put("test1.port", "");

        // Check for "unable to parse" error:
        testProperties.put("test2.port", "123xy");

        // Check for "out-of-bounds" (less than) error:
        testProperties.put("test3.port", "0");

        // Check for "out of bounds" (greater than) error:
        testProperties.put("test4.port", "65536");

        // Check for "port in use" error:
        testProperties.put("test5.port", "" + testPortNumber);

        try
        {
            blockPort(null, testPortNumber);
            
            String errorMessage = testBean.performEarlyPropertyChecks(testProperties);
            
            assertTrue(errorMessage.contains("The value for TestSubsystem port property cannot be empty."));
            assertTrue(errorMessage.contains("Unable to parse value for TestSubsystem port property: 123xy."));
            assertTrue(errorMessage.contains("The port chosen for TestSubsystem is outside the required range (1, 65535): 0."));
            assertTrue(errorMessage.contains("The port chosen for TestSubsystem is outside the required range (1, 65535): 65536."));
            
            assertTrue(errorMessage.contains(
                    "The port chosen for TestSubsystem is already in use or you don't have permission to use it: " + testPortNumber + "."));
            
        }
        finally
        {
            unblockPort();
        }
        
        testProperties.clear();

        testProperties.put("test_with_host.port", "" + testPortNumber);
        // Check for unknown host:
        testProperties.put("test.subsystem.host", "The quick brown fox jumps over the lazy dog.");

        String errorMessage = testBean.performEarlyPropertyChecks(testProperties);
        
        assertTrue(errorMessage.contains(
                "The hostname chosen for TestSubsystem is unknown or misspelled: " + testProperties.get("test.subsystem.host") + "."));

    }

    public void testAbstractPropertyBackedBean_performEarlyPropertyChecks_CustomEarlyPropertyChecker()
    {
        ChildApplicationContextFactory testBean = new ChildApplicationContextFactory();

        SubsystemEarlyPropertyChecker testEarlyPropertyChecker = new SubsystemEarlyPropertyChecker()
        {
            @Override
            public void checkPropertyValue(String propertyName, String propertyValue, String pairedPropertyValue) throws InvalidPropertyValueException
            {
                if (propertyValue == null || propertyValue.isEmpty())
                {
                    throw new InvalidPropertyValueException("Property value cannot be empty.");
                }

                if (pairedPropertyValue == null)
                {
                    if (propertyValue.equals("Bad value"))
                    {
                        throw new InvalidPropertyValueException("Property value cannot be a 'Bad value'.");
                    }
                }
                else if ((propertyValue + pairedPropertyValue).contains("bad value"))
                {
                    throw new InvalidPropertyValueException("No 'bad value's allowed!");
                }
            }

            @Override
            public String getPairedPropertyName()
            {
                return "testPairedPropertyName";
            }
        };

        Map<String, SubsystemEarlyPropertyChecker> earlyPropertyCheckersMap = new HashMap<String, SubsystemEarlyPropertyChecker>();
        earlyPropertyCheckersMap.put("test1.property", testEarlyPropertyChecker);
        earlyPropertyCheckersMap.put("test2.property", testEarlyPropertyChecker);

        testBean.setEarlyPropertyCheckers(earlyPropertyCheckersMap);

        Map<String, String> testProperties = new HashMap<String, String>();
        
        // Test empty value error:
        testProperties.put("test1.property", "");
        
        // Test "Bad value" error:
        testProperties.put("test2.property", "Bad value");

        String errorMessage = testBean.performEarlyPropertyChecks(testProperties);

        assertTrue(errorMessage.contains("Property value cannot be empty."));
        assertTrue(errorMessage.contains("Property value cannot be a 'Bad value'."));
        
        earlyPropertyCheckersMap.clear();
        earlyPropertyCheckersMap.put("test3.property", testEarlyPropertyChecker);
        
        testProperties.clear();
        testProperties.put("testPairedPropertyName", "Test paired property bad value");
        testProperties.put("test3.property", "Test property value");
        
        errorMessage = testBean.performEarlyPropertyChecks(testProperties);
        assertTrue(errorMessage.contains("No 'bad value's allowed!"));
    }
}
