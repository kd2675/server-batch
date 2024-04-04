package com.example.batch.api.biz;

import com.example.batch.utils.ChromeDriverConnUtil;
import org.jsoup.nodes.Document;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;

import static org.assertj.core.api.Assertions.assertThat;
import static org.mockito.BDDMockito.given;

@ExtendWith(MockitoExtension.class)
class SendKospiSvcTest {
    @Mock
    ChromeDriverConnUtil chromeDriverConnUtil;

    @Test
    void getKospi() {
        //given
        String url = "test";
        Document doc = new Document(url);
        doc.appendElement("div");

        given(chromeDriverConnUtil.conn(url)).willReturn(doc);

        //whenjkldsjfkldsjfklsdf
        Document conn = chromeDriverConnUtil.conn(url);
        System.out.println("conn = " + conn);

        //then
        assertThat(conn.body())
                .isEqualTo(doc.body());
    }
}