package mobi.wrt.oreader.app.html;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.nodes.Node;
import org.jsoup.nodes.TextNode;
import org.jsoup.select.Elements;
import org.jsoup.select.NodeTraversor;
import org.jsoup.select.NodeVisitor;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import by.istin.android.xcore.utils.Holder;
import by.istin.android.xcore.utils.StringUtil;

public class MediaContentRecognizer {

    public static final String YOUTUBE_REGEX = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";
    public static final String YOUTUBE_VIDEO_ID_REGEX = "^.*((youtu.be" + "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*";

    public static final Pattern YOUTUBE_PATTERN = Pattern.compile(YOUTUBE_REGEX, Pattern.CASE_INSENSITIVE);
    public static final Pattern YOUTUBE_VIDEO_ID_PATTERN = Pattern.compile(YOUTUBE_VIDEO_ID_REGEX, Pattern.CASE_INSENSITIVE);

    public static final String URL_REGEX = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
    public static final Pattern URL_PATTERN = Pattern.compile(URL_REGEX, Pattern.CASE_INSENSITIVE);

    public static List<HtmlElement> recognize(String html) {
        Document document = Jsoup.parseBodyFragment(html);
        Element body = document.body();
        final Elements objs = document.select("[height][width][src]");
        final List<HtmlElement> list = new ArrayList<HtmlElement>();
        final StringBuilder builder = new StringBuilder();
        final Holder<Integer> objPositionHolder = new Holder<Integer>(0);
        final Holder<Element> objHolder = new Holder<Element>(objs.get(0));

        new NodeTraversor(new NodeVisitor() {

            private boolean isSkipNext = false;

            @Override
            public void head(Node node, int i) {
                if (node instanceof TextNode) {
                    if (isSkipNext) {
                        return;
                    }
                    if (builder.length() > 0) {
                        builder.append(" ");
                    }
                    TextNode textNode = (TextNode) node;
                    String wholeText = textNode.getWholeText();
                    builder.append(wholeText.trim());
                } else if (node instanceof Element) {
                    Element element = (Element)node;
                    String tagName = element.tag().getName();
                    if (builder.length() > 0) {
                        if (element.isBlock() || tagName.equals("br")) {
                            builder.append("\n");
                        };
                    }
                    if (element.equals(objHolder.get())) {
                        if (builder.length() > 0) {
                            list.add(new HtmlElement(null, builder.toString()));
                            builder.setLength(0);
                        }
                        list.add(new HtmlElement(element, null));
                        Integer position = objPositionHolder.get();
                        position = position + 1;
                        if (position < objs.size()) {
                            objHolder.set(objs.get(position));
                            objPositionHolder.set(position);
                        }
                    } else if (isSpannedCanHandleTag(tagName)) {
                        appendAndSkip(element);
                    }
                }
            }

            public void appendAndSkip(Element element) {
                builder.append(element.toString());
                isSkipNext = true;
            }

            @Override
            public void tail(Node node, int i) {
                isSkipNext = false;
            }
        }).traverse(body);
        if (builder.length() > 0) {
            list.add(new HtmlElement(null, builder.toString()));
        }
        return list;
    }

    public static boolean isSpannedCanHandleTag(String tagName) {
        return tagName.equalsIgnoreCase("a") ||
               tagName.equalsIgnoreCase("strong") ||
               tagName.equalsIgnoreCase("b") ||
               tagName.equalsIgnoreCase("em") ||
               tagName.equalsIgnoreCase("cite") ||
               tagName.equalsIgnoreCase("dfn") ||
               tagName.equalsIgnoreCase("i") ||
               tagName.equalsIgnoreCase("big") ||
               tagName.equalsIgnoreCase("small") ||
               tagName.equalsIgnoreCase("font") ||
               tagName.equalsIgnoreCase("blockquote") ||
               tagName.equalsIgnoreCase("tt") ||
               tagName.equalsIgnoreCase("u") ||
               tagName.equalsIgnoreCase("sup") ||
               tagName.equalsIgnoreCase("sub") ||
               (tagName.length() == 2 &&
                        Character.toLowerCase(tagName.charAt(0)) == 'h' &&
                        tagName.charAt(1) >= '1' && tagName.charAt(1) <= '6')
        ;
    }

    public static String findAllImages(boolean withVideo, String... sources) {
        StringBuilder stringBuilder = new StringBuilder();
        for (String source : sources) {
            if (!StringUtil.isEmpty(source)) {
                Document summaryDocument = Jsoup.parse(source);
                Elements imgs = summaryDocument.select("img[height]");
                if (imgs != null && imgs.size() > 0) {
                    stringBuilder.append(imgs.toString());
                }
                if (withVideo) {
                    List<String> urls = extractUrls(source);
                    if (urls != null) {
                        for (String url : urls) {
                            stringBuilder.append(findYouTubeImage(url));
                        }
                    }
                }
            }
        }
        return stringBuilder.toString();
    }

    public static List<String> extractUrls(String value) {
        if (StringUtil.isEmpty(value)) return null;
        List<String> result = new ArrayList<String>();
        Matcher m = URL_PATTERN.matcher(value);
        while (m.find()) {
            result.add(value.substring(m.start(0), m.end(0)));
        }
        return result;
    }


    private static String findYouTubeImage(String youtubeUrl) {
        List<String> urls = new ArrayList<String>();
        Matcher matcher = YOUTUBE_PATTERN.matcher(youtubeUrl);
        while (matcher.find()) {
            urls.add(matcher.group());
        }
        if (urls != null && urls.size() > 0) {
            StringBuilder stringBuilder = new StringBuilder();
            for (String url : urls) {
                String v = getYoutubeVideoId(url);
                if (!StringUtil.isEmpty(v)) {
                    stringBuilder.append("<img src=\"http://img.youtube.com/vi/" + v + "/0.jpg\"></img>");
                }
            }
            return stringBuilder.toString();
        }
        return StringUtil.EMPTY;
    }

    public static String getYoutubeVideoId(String youtubeUrl) {
        String videoId = StringUtil.EMPTY;
        if (youtubeUrl != null && youtubeUrl.trim().length() > 0 && youtubeUrl.startsWith("http")) {
            CharSequence input = youtubeUrl;
            Matcher matcher = YOUTUBE_VIDEO_ID_PATTERN.matcher(input);
            if (matcher.matches()) {
                String groupIndex1 = matcher.group(7);
                if (groupIndex1 != null && groupIndex1.length() == 11)
                    videoId = groupIndex1;
            }
        }
        return videoId;
    }
}
