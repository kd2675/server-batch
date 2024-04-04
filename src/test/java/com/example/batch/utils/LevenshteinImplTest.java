package com.example.batch.utils;

import org.junit.jupiter.api.Test;
import org.springframework.test.context.ActiveProfiles;

import static org.assertj.core.api.Assertions.assertThat;
@ActiveProfiles("test")
class LevenshteinImplTest {

    @Test
    void similarity() {
        Levenshtein levenshtein = new LevenshteinImpl();
        String s1 = "안녕! 내 이름은 김도영@";
        String s2 = "안녕! 내 이름은 김도영@";
        String s3 = "안녕! 내 이름은";
        String s4 = "이름테스트";
        String s5 = "";
        String s6 = "이름은 내 김도영@ 안녕!";

        double similarity1 = levenshtein.similarity(s1, s2);
        double similarity2 = levenshtein.similarity(s1, s3);
        double similarity3 = levenshtein.similarity(s1, s4);
        double similarity4 = levenshtein.similarity(s1, s5);
        double similarity5 = levenshtein.similarity(s1, s6);

        assertThat(similarity1)
                .isEqualTo(1D);
        assertThat(similarity2)
                .isGreaterThan(0.5D);
        assertThat(similarity3)
                .isLessThan(0.5D);
        assertThat(similarity4)
                .isEqualTo(0D);
        assertThat(similarity5)
                .isLessThan(0.5D);

        System.out.println("similarity1 = " + similarity1);
        System.out.println("similarity2 = " + similarity2);
        System.out.println("similarity3 = " + similarity3);
        System.out.println("similarity4 = " + similarity4);
        System.out.println("similarity5 = " + similarity5);
    }
}