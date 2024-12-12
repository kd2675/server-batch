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
    String URL68 = "https://www.gwangjusportsinfo.org/reservation/reservation_view/5/";
    String URL99 = "https://www.gwangjusportsinfo.org/reservation/reservation_view/2/";
    String URLJangSung = "https://www.jangseong.go.kr/home/ok/health/warabel_gym?step=two";

    private final ChromeDriverConnUtil chromeDriverConnUtil;
    private final MattermostUtil mattermostUtil;

    @Override
    public void saveSportJangSung911() {
        String url = URLJangSung + "&year=2024&month=11&day=9&start_time=9";
        Document doc = chromeDriverConnUtil.conn(url);

        for (Element element : doc.getElementsByClass("reserve_table")
                .get(0)
                .getElementsByTag("tbody")
                .get(0).getElementsByTag("tr")
        ) {
            log.warn("element : {}", element);
            if (!element.getElementsByTag("input").isEmpty()) {
                mattermostUtil.send("@kimd00 워라벨 돔 테니스장 예약 확인 요망", "35cpu84icbr6xch7ju61k4da6w");
            }

        }
    }

    @Override
    public void saveSport() {
        for (int num = 2; num < 5; num++) {
            String url = URL + String.valueOf(num) + "?agree=1";
            Document doc = chromeDriverConnUtil.conn(url);

            for (Element element : doc.getElementsByClass("sat btn_pop")) {
                String s1 = null;
                String s2 = null;
                String s3 = null;
                try {
                    s1 = element.getElementsByTag("li").get(0).className();
                    String s1Str = element.getElementsByTag("li").get(0).getElementsByTag("b").get(0).ownText();
                    s2 = element.getElementsByTag("li").get(1).className();
                    String s2Str = element.getElementsByTag("li").get(1).getElementsByTag("b").get(0).ownText();
//                    s3 = element.getElementsByTag("li").get(4).className();
//                    String s3Str = element.getElementsByTag("li").get(4).getElementsByTag("b").get(0).ownText();

                    log.warn("{}", s1);
                    log.warn("{}", s1Str);
                    log.warn("{}", s2);
                    log.warn("{}", s2Str);
//                    log.warn("{}", s3);
//                    log.warn("{}", s3Str);
                } catch (Exception e) {
                }

                if (s1 != null && s1.equals("child")) {
                    mattermostUtil.send("@kimd00 테니스장 예약 확인 요망 " + num + "코트 " + "7-9", "35cpu84icbr6xch7ju61k4da6w");
                }

                if (s2 != null && s2.equals("child")) {
                    mattermostUtil.send("@kimd00 테니스장 예약 확인 요망 " + num + "코트 " + "9-11", "35cpu84icbr6xch7ju61k4da6w");
                }

                if (s3 != null && s3.equals("child")) {
//                        mattermostUtil.send("@kimd00 테니스장 예약 확인 요망 " + num + "코트 " + "15-17", "35cpu84icbr6xch7ju61k4da6w");
                }
            }
        }
    }

    @Override
    public void saveSport68() {
        for (int num = 5; num < 17; num++) {
            String url = URL68 + String.valueOf(num) + "?agree=1";
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

                if ((s1 != null && s1.equals("child"))) {
//                    mattermostUtil.send("@kimd00 테니스장 예약 확인 요망 " + (num - 2) + "코트 " + "6-8", "35cpu84icbr6xch7ju61k4da6w");
                }

                if ((s2 != null && s2.equals("child"))) {
                    mattermostUtil.send("@kimd00 테니스장 예약 확인 요망 " + (num - 2) + "코트 " + "8-10", "35cpu84icbr6xch7ju61k4da6w");
                }
            }
        }
    }

    @Override
    public void saveSport68Cus() {
        for (int num = 5; num < 17; num++) {
            String url = URL68 + String.valueOf(num) + "?agree=1";
            Document doc = chromeDriverConnUtil.conn(url);

            for (Element element : doc.getElementById("2024-09-20").children()) {
                String s1 = null;
                try {
                    s1 = element.getElementsByTag("li").get(7).className();
                    String s1Str = element.getElementsByTag("li").get(7).getElementsByTag("b").get(0).ownText();

                    log.warn("{}", s1);
                    log.warn("{}", s1Str);
                } catch (Exception e) {
                }

                if ((s1 != null && s1.equals("child"))) {
                    mattermostUtil.send("@kimd00 테니스장 예약 확인 요망 " + (num - 2) + "코트 " + "20-22", "35cpu84icbr6xch7ju61k4da6w");
                }
            }
        }
    }

    @Override
    public void saveSport99() {
        for (int num = 1; num < 5; num++) {
            String url = URL99 + String.valueOf(num) + "?agree=1";
            Document doc = chromeDriverConnUtil.conn(url);

            for (Element element : doc.getElementsByClass("sat btn_pop")) {
                String s1 = null;
                try {
                    s1 = element.getElementsByTag("li").get(0).className();
                    String s1Str = element.getElementsByTag("li").get(0).getElementsByTag("b").get(0).ownText();

                    log.warn("{}", s1);
                    log.warn("{}", s1Str);
                } catch (Exception e) {
                }

                if ((s1 != null && s1.equals("child"))) {
                    mattermostUtil.send("@kimd00 테니스장 예약 확인 요망 " + num + "코트 " + "99", "35cpu84icbr6xch7ju61k4da6w");
                }
            }
        }


    }
}
