package com.example.batch.utils;

import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.openqa.selenium.chrome.ChromeOptions;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;
@ActiveProfiles("test")
@ExtendWith(MockitoExtension.class)
class ChromeDriverConnUtilImplTest {
    @Mock
    ChromeDriverConnUtil chromeDriverConnUtil;
    @Test
    void conn() {
        //given
        String url = "test";
        Document doc = new Document(url);
        doc.appendElement("div");
        ChromeOptions chromeOptions = new ChromeOptions();
        given(chromeDriverConnUtil.conn(url)).willReturn(doc);
        given(chromeDriverConnUtil.conn(url, chromeOptions)).willReturn(doc);

        //when
        Document conn = chromeDriverConnUtil.conn(url);
        Document connWithChromeOptions = chromeDriverConnUtil.conn(url, chromeOptions);

        //then
        assertThat(conn.body())
                .isEqualTo(doc.body());
        assertThat(connWithChromeOptions.body())
                .isEqualTo(doc.body());

        System.out.println("doc = " + doc);
        System.out.println("conn = " + conn);
        System.out.println("connWithChromeOptions = " + connWithChromeOptions);
    }
}