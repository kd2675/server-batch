package com.example.batch.service.batch.reader;

import com.example.batch.service.hotdeal.database.rep.jpa.HotdealDTO;
import com.example.batch.service.hotdeal.database.rep.jpa.HotdealEntity;
import com.example.batch.service.hotdeal.database.rep.jpa.HotdealEntityREP;
import jakarta.persistence.EntityManagerFactory;
import lombok.RequiredArgsConstructor;
import lombok.extern.slf4j.Slf4j;
import org.apache.commons.lang3.StringUtils;
import org.jsoup.Jsoup;
import org.jsoup.nodes.Document;
import org.jsoup.nodes.Element;
import org.jsoup.select.Elements;
import org.springframework.batch.core.configuration.annotation.StepScope;
import org.springframework.batch.item.support.ListItemReader;
import org.springframework.beans.factory.annotation.Qualifier;
import org.springframework.context.annotation.Bean;
import org.springframework.context.annotation.Configuration;
import org.springframework.web.client.RestTemplate;
import org.springframework.web.util.UriComponentsBuilder;

import java.net.URI;
import java.util.ArrayList;
import java.util.Collections;
import java.util.List;

@Slf4j
@RequiredArgsConstructor
@Configuration
public class HotdealReader {
    private static final int PAGE_SIZE = 100;
    public static final String FIND_HOTDEAL = "findHotdeal";
    public static final String FIND_HOTDEAL_TOP5_SEND_YN_N = "findHotdealTop15SendYnN";

    private final RestTemplate restTemplate;

    private final HotdealEntityREP hotdealEntityREP;

    @Bean(name = FIND_HOTDEAL, destroyMethod = "")
    @StepScope
    public ListItemReader<HotdealDTO> findHotdeal() {
        return new ListItemReader<HotdealDTO>(this.getHotdeal());
    }

    @Bean(name = FIND_HOTDEAL_TOP5_SEND_YN_N, destroyMethod = "")
    @StepScope
    public ListItemReader<HotdealEntity> findTop5News(@Qualifier("hotdealEntityManagerFactory") EntityManagerFactory entityManagerFactory) {
        return new ListItemReader<>(
                hotdealEntityREP.findTop5BySendYnOrderByIdDesc("n")
        );
    }

    private List<HotdealDTO> getHotdeal() {
        List<HotdealDTO> result = new ArrayList<>();

        HotdealEntity hotdealEntity = hotdealEntityREP.findTop1ByOrderByProductIdDesc().stream().findFirst().orElse(null);

        if (hotdealEntity != null) {
            for (int i = 0; i < 5; i++) {
                List<HotdealDTO> hotdeal = this.getHotdeal(i);

                Long productId = hotdealEntity.getProductId();

                boolean limit1 = hotdeal.stream()
                        .anyMatch(v -> v.getProductId().compareTo(productId) == 0);

                boolean limit2 = hotdeal.stream()
                        .anyMatch(v -> v.getProductId().compareTo(productId) > 0);

                List<HotdealDTO> collect = hotdeal.stream()
                        .filter(v -> v.getProductId().compareTo(productId) > 0)
                        .toList();

                result.addAll(collect);

                if (limit1 || !limit2) {
                    break;
                }
            }
        } else {
            List<HotdealDTO> hotdeal = this.getHotdeal(0);
            result.addAll(hotdeal);
        }

        return result;
    }

    private List<HotdealDTO> getHotdeal(int num) {
        URI uri = UriComponentsBuilder
                .fromUriString("https://www.algumon.com")
                .path("/more/" + num)
                .encode()
                .build()
                .toUri();

        // JSON 데이터 가져오기
        String response = restTemplate.getForObject(uri, String.class);

        // JSON 데이터 출력
        try { // ObjectMapper 객체 생성
            Document doc = Jsoup.parse(response);
            Elements postElements = doc.select(".post-li");

            List<HotdealDTO> hotdealDTOS = new ArrayList<>();

            for (Element postElement : postElements) {
                String id = postElement.attr("data-post-id");
                String title = postElement.select(".item-name").text().trim();

                String originPrice = postElement.select(".product-price").text();

                int price;
                String priceSlct = null;
                String priceStr = null;
                try {
                    if (originPrice.contains("$")) {
                        String replace = originPrice.split("\\.")[0]
                                .replace("원", "")
                                .replace(",", "")
                                .replace("$", "")
                                .replace(".", "")
                                .replace("다양", "");
                        price = StringUtils.isNotEmpty(replace) ? Integer.parseInt(replace) : 0;
                    } else {
                        String replace = originPrice
                                .replace("원", "")
                                .replace(",", "")
                                .replace("$", "")
                                .replace(".", "")
                                .replace("다양", "")
                                .trim();
                        price = StringUtils.isNotEmpty(replace) ? Integer.parseInt(replace) : 0;
                    }
                    priceSlct = originPrice.contains("$") ? "d" : "w";
                    priceStr = originPrice.trim();
                } catch (Exception e) {
                    price = 0;
                    priceSlct = "w";
                    priceStr = "0";
                    log.error("price error -> {}", e);
                }

                String link = "https://www.algumon.com" + postElement.select(".product-link").attr("href").trim();
                String img = postElement.select(".product-img").select("img").attr("src").trim();
                if (img.contains("?")) {
                    img = img.substring(0, img.indexOf("?"));
                }
                String shop = postElement.select(".label.shop").text().trim();
                String site = postElement.select(".label.site:nth-of-type(1)").text().trim();

                HotdealDTO product = new HotdealDTO(
                        null,
                        Long.valueOf(id),
                        title,
                        price,
                        priceSlct,
                        priceStr,
                        link,
                        img,
                        shop,
                        site,
                        "n"
                );
                hotdealDTOS.add(product);
            }

            return hotdealDTOS;
        } catch (Exception e) {
            e.printStackTrace();
        }

        return Collections.emptyList();
    }
}
