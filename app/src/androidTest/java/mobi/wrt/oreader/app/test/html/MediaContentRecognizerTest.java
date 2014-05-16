package mobi.wrt.oreader.app.test.html;

import junit.framework.TestCase;

import mobi.wrt.oreader.app.html.MediaContentRecognizer;

public class MediaContentRecognizerTest extends TestCase {

    private static final String SOURCE1 = "<p>В конце апреля компания <strong>Samsung</strong> официально представила камерофон, " +
            "который стал первым устройством, вошедшим в линейку «К». Несмотря на недавний анонс, мы уже смогли познакомиться с новинкой поближе. " +
            "О нашем первом впечатлении от <strong>Galaxy K Zoom</strong> можно узнать из видеоролика.</p> <p><iframe height=\"360\" width=\"640\" src=\"http://www.youtube.com/embed/dlAa0Fwx-4I\"></iframe></p> " +
            "<p>В первую очередь, новый камерофон отличается от предшественника более компактным корпусом. Еще до официального релиза эксперты утверждали, что Samsung всеми силами старается сделать устройство " +
            "похожим на обычный смартфон. И, по большей части, корейским инженерам это удалось. <strong>Galaxy K Zoom</strong> действительно похож на телефон. Хотя гаджет получился достаточно тяжелым – около 200 грамм.<br> " +
            "<br><a href=\"http://droider.ru/post/videoobzor-galaxy-k-zoom-novyiy-koreyskiy-kamerofon-12-05-2014/\">Читать дальше →</a></p> <hr> <p><small>2014 © <a href=\"http://droider.ru\">Droider.ru</a> </small></p>";

    private static final String SOURCE2 = "<p>Всем привет!</p> <p>Начинаем свеженький <strong>Droider Show</strong>. " +
            "На этот раз неделя выдалась интересная: представили темную лошадку смартфон <strong>OnePlus One</strong>, " +
            "<strong>Йота</strong> объявила, что станет оператором сотовой связи, а <strong>Facebook</strong> опять что-то купил. " +
            "Все подробности как всегда в ролике:</p> <p><iframe height=\"360\" width=\"640\" src=\"http://www.youtube.com/embed/kY57y0DQdW4\"></iframe></p> " +
            "<p>Кроме этого не пропустите горячую подборку инди-гаджетов: платежный терминал со сканером вен, новая Lytro камера и музыкальные квадрокоптеры. " +
            "Приятного просмотра и хорошей недели!</p> <img alt=\"\" src=\"http://droider.ru/?ak_action=api_record_view&amp;id=46385&amp;type=feed\"><hr> " +
            "<p><small>2014 © <a href=\"http://droider.ru\">Droider.ru</a> </small></p>";


    public void testRecognizer() throws Exception {
        /*String allImages = MediaContentRecognizer.findAllImages(true, SOURCE1);
        assertEquals("<img src=\"http://img.youtube.com/vi/"+"dlAa0Fwx-4I"+"/0.jpg\"></img>", allImages);
        allImages = MediaContentRecognizer.findAllImages(true, SOURCE2);
        assertEquals("<img src=\"http://img.youtube.com/vi/"+"kY57y0DQdW4"+"/0.jpg\"></img>", allImages);*/

        MediaContentRecognizer.recognize(SOURCE1);
    }

}
