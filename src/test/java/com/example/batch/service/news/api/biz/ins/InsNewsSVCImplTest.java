package com.example.batch.service.news.api.biz.ins;

import com.example.batch.service.news.batch.biz.ins.InsNewsSVC;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.Spy;
import org.mockito.junit.jupiter.MockitoExtension;

@ExtendWith(MockitoExtension.class)
class InsNewsSVCImplTest {

    @Spy
    InsNewsSVC insNewsSVC;

    @Test
    void test() {
        insNewsSVC.saveNews();
    }
}