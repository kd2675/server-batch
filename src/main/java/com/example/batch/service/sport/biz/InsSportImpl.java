package com.example.batch.service.sport.biz;

import com.example.batch.service.kospi.database.rep.jpa.kospi.KospiREP;
import com.example.batch.utils.ChromeDriverConnUtil;
import com.example.batch.utils.MattermostUtil;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.springframework.stereotype.Service;

@Slf4j
@Service
@RequiredArgsConstructor
public class InsSportImpl implements InsSportSVC {
    String URL = "https://www.gwangjusportsinfo.org/reservation/reservation_view/1/";

    private final ChromeDriverConnUtil chromeDriverConnUtil;
    private final MattermostUtil mattermostUtil;

    @Override
    public void saveSport() {
        for (int num = 2; num < 5; num++) {
            String url = URL + String.valueOf(num) + "?agree=1";
            Document doc = chromeDriverConnUtil.conn(url);

            for (Element element : doc.getElementsByClass("sat btn_pop")) {
                String s1 = null;
                String s2 = null;
                try {
                    s1 = element.getElementsByTag("li").get(0).className();
                    String s1Str = element.getElementsByTag("li").get(0).getElementsByTag("b").get(0).ownText();
                    s2 = element.getElementsByTag("li").get(1).className();
                    String s2Str = element.getElementsByTag("li").get(1).getElementsByTag("b").get(0).ownText();

                    log.warn("{}", s1);
                    log.warn("{}", s1Str);
                    log.warn("{}", s2);
                    log.warn("{}", s2Str);
                } catch (Exception e) {
                }

                if ((s1 != null && s1.equals("child")) || (s2 != null && s2.equals("child"))) {
                    if (s1 != null && s1.equals("child")) {
                        mattermostUtil.send("테니스장 예약 확인 요망 " + num + "코트 " + "7-9", "35cpu84icbr6xch7ju61k4da6w");
                    }else {
                        mattermostUtil.send("테니스장 예약 확인 요망 " + num + "코트 " + "9-11", "35cpu84icbr6xch7ju61k4da6w");
                    }
                }
            }
        }
    }
}
