package org.alfresco.repo.content.encoding;

import java.io.BufferedInputStream;
import java.io.ByteArrayInputStream;
import java.io.InputStream;
import java.nio.charset.Charset;

import junit.framework.TestCase;

import org.alfresco.encoding.CharactersetFinder;
import org.alfresco.repo.content.MimetypeMap;
import org.alfresco.util.DataModelTestApplicationContextHelper;
import org.springframework.context.ApplicationContext;

/**
 * @see CharsetFinderTest
 * @see CharactersetFinder
 * 
 * @author Derek Hulley
 */
public class CharsetFinderTest extends TestCase
{
    private static ApplicationContext ctx = DataModelTestApplicationContextHelper.getApplicationContext();
    
    private ContentCharsetFinder charsetFinder;
    
    @Override
    public void setUp() throws Exception
    {
        charsetFinder = (ContentCharsetFinder) ctx.getBean("charset.finder");
    }
    
    public void testPlainText() throws Exception
    {
        String test = "The quick brown fox jumps over the lazy dog" +
                      "\n\nLe renard brun rapide saute par-dessus le chien paresseux" +
                      "\n\nDer schnelle braune Fuchs springt über den faulen Hund\n\n" +
                      "براون وكس السريع يقفز فوق الكلب كسالي";
        
        // As UTF-8
        InputStream is = new BufferedInputStream(new ByteArrayInputStream(test.getBytes("UTF-8")));
        Charset charset = charsetFinder.getCharset(is, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        assertNotNull(charset);
        assertEquals("UTF-8", charset.displayName());
        
        // Now try with longer encodings
        
        // UTF-16 with byte order mark 
        is = new BufferedInputStream(new ByteArrayInputStream(test.getBytes("UTF-16")));
        charset = charsetFinder.getCharset(is, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        assertNotNull(charset);
        assertEquals("UTF-16BE", charset.displayName());
        
        is = new BufferedInputStream(new ByteArrayInputStream(test.getBytes("UnicodeBig")));
        charset = charsetFinder.getCharset(is, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        assertNotNull(charset);
        assertEquals("UTF-16BE", charset.displayName());
        
        is = new BufferedInputStream(new ByteArrayInputStream(test.getBytes("UnicodeLittle")));
        charset = charsetFinder.getCharset(is, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        assertNotNull(charset);
        assertEquals("UTF-16LE", charset.displayName());
        
        // UTF-32 with byte order mark
        is = new BufferedInputStream(new ByteArrayInputStream(test.getBytes("UTF-32")));
        charset = charsetFinder.getCharset(is, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        assertNotNull(charset);
        assertEquals("UTF-32BE", charset.displayName());
    }
    
    /**
     * Tries with various 8 bit (non-unicode) encodings, such as
     *  ISO-8859-1
     */
    public void test8BitText() throws Exception
    {
        String for_iso_8859_1 = "En français où les choses sont accentués. En español, así";
        String for_iso_8859_7 = "Αυτό είναι στην ελληνική γλώσσα";
        String for_cp1251 = "Это в русском языке, который является кириллица";
        
        InputStream is = new BufferedInputStream(new ByteArrayInputStream(for_iso_8859_1.getBytes("ISO-8859-1")));
        Charset charset = charsetFinder.getCharset(is, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        assertNotNull(charset);
        assertEquals("ISO-8859-1", charset.displayName());
        
        is = new BufferedInputStream(new ByteArrayInputStream(for_iso_8859_7.getBytes("ISO-8859-7")));
        charset = charsetFinder.getCharset(is, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        assertNotNull(charset);
        assertEquals("ISO-8859-7", charset.displayName());
        
        is = new BufferedInputStream(new ByteArrayInputStream(for_cp1251.getBytes("CP1251")));
        charset = charsetFinder.getCharset(is, MimetypeMap.MIMETYPE_TEXT_PLAIN);
        assertNotNull(charset);
        assertEquals("windows-1251", charset.displayName()); // AKA CP1251
    }
}
