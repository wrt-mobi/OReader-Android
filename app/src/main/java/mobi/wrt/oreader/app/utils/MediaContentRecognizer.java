package mobi.wrt.oreader.app.utils;

import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.select.Elements;

import java.util.ArrayList;
import java.util.List;
import java.util.regex.Matcher;
import java.util.regex.Pattern;

import by.istin.android.xcore.utils.StringUtil;

public class MediaContentRecognizer {

    private static String pattern = "https?:\\/\\/(?:[0-9A-Z-]+\\.)?(?:youtu\\.be\\/|youtube\\.com\\S*[^\\w\\-\\s])([\\w\\-]{11})(?=[^\\w\\-]|$)(?![?=&+%\\w]*(?:['\"][^<>]*>|<\\/a>))[?=&+%\\w]*";
    public static final Pattern YOUTUBE_REGEX = Pattern.compile(pattern, Pattern.CASE_INSENSITIVE);

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
        String urlPattern = "((https?|ftp|gopher|telnet|file):((//)|(\\\\))+[\\w\\d:#@%/;$()~_?\\+-=\\\\\\.&]*)";
        Pattern p = Pattern.compile(urlPattern, Pattern.CASE_INSENSITIVE);
        Matcher m = p.matcher(value);
        while (m.find()) {
            result.add(value.substring(m.start(0), m.end(0)));
        }
        return result;
    }


    private static String findYouTubeImage(String youtubeUrl) {
        List<String> urls = new ArrayList<String>();
        Matcher matcher = YOUTUBE_REGEX.matcher(youtubeUrl);
        while(matcher.find()) {
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
        String videoUd = "";
        if (youtubeUrl != null && youtubeUrl.trim().length() > 0 && youtubeUrl.startsWith("http")) {
            String expression = "^.*((youtu.be" + "\\/)" + "|(v\\/)|(\\/u\\/w\\/)|(embed\\/)|(watch\\?))\\??v?=?([^#\\&\\?]*).*"; // var regExp = /^.*((youtu.be\/)|(v\/)|(\/u\/\w\/)|(embed\/)|(watch\?))\??v?=?([^#\&\?]*).*/;
            CharSequence input = youtubeUrl;
            Pattern pattern = Pattern.compile(expression, Pattern.CASE_INSENSITIVE);
            Matcher matcher = pattern.matcher(input);
            if (matcher.matches()) {
                String groupIndex1 = matcher.group(7);
                if (groupIndex1 != null && groupIndex1.length() == 11)
                    videoUd = groupIndex1;
            }
        }
        return videoUd;
    }
}
