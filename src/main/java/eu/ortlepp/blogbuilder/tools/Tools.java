package eu.ortlepp.blogbuilder.tools;

import java.util.regex.Matcher;
import java.util.regex.Pattern;

/**
 * An utility class with some useful (static) methods.
 *
 * @author Thorsten Ortlepp
 */
public final class Tools {

    /**
     * Change all non-absolute links in an HTML formatted string into relative links.
     * All links in href and src attributes are prepended with the relative path to the base
     * directory (e.g. ../../path/file.html).
     *
     * @param content The (HTML) text in which the links should be changed
     * @param relative The relative path to add to the links
     * @return The content with changed links
     */
    public static String makeLinksRelative(String content, String relative) {
        String replaced = replaceLinks(content, relative, "href");
        replaced = replaceLinks(replaced, relative, "src");
        return replaced;
    }


    /**
     * Change all non-absolute links in an HTML formatted string into absolute links.
     * All links in href and src attributes are prepended with the base URL.
     *
     * @param content The (HTML) text in which the links should be changed
     * @return The content with changed links
     */
    public static String makeLinksAbsolute(String content) {
        String baseurl = Config.getInstance().getBaseUrl();
        if (!baseurl.endsWith("/")) {
            baseurl += "/";
        }

        String replaced = replaceLinks(content, baseurl, "href");
        replaced = replaceLinks(replaced, baseurl, "src");
        return replaced;
    }


    /**
     * Adds a prefix to all relative links (links that do not start with  http / https).
     *
     * @param content The (HTML) text in which the links should be changed
     * @param prefix The prefix to add to all relative links
     * @param attribute The attribute whose links are changed
     * @return The content with changed links
     */
    private static String replaceLinks(String content, String prefix, String attribute) {
        Matcher matcher = Pattern.compile(attribute + "=\".*?\"").matcher(content);
        StringBuffer strBuffer = new StringBuffer(content.length());

        while (matcher.find()) {
            String found = matcher.group();

            /* Change all links that do not start with http / https */
            if (!found.startsWith(attribute + "=\"http:") && !found.startsWith(attribute + "=\"https:")) {
                found = found.replace(attribute + "=\"", attribute + "=\"" + prefix);
                matcher.appendReplacement(strBuffer, Matcher.quoteReplacement(found));
            }
        }

        matcher.appendTail(strBuffer);
        return strBuffer.toString();
    }


    /**
     * Private constructor for tool class - without any functionality.
     */
    private Tools() {
        /* Nothing happens here... */
    }

}