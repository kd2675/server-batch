package com.example.batch.service.webhook.api.vo;

import lombok.AllArgsConstructor;
import lombok.Getter;

@Getter
@AllArgsConstructor
public enum MemberEnum {
    YONG(1L, "용", "ewtmwoy8ridump183qucqgtb5y", "kimdo"),
    WOO(2L, "우", "i5xo5tnf67diidscisppku3u3h", "klmd0"),
    HO(3L, "호", "3rgampx9pbr5fjjizq97mjyb9y", "kimd0."),
    KIM(4L, "김", "irzi4hmjb781ppfd1nrpfm8ruw", "kimd0"),
    JOO(5L, "주", "46h1ubq517dff8x6ifr1bhwcie", "kimd0young"),
    GAP(6L, "갑", "hy4ynqft37fmudw1pjdx14uu9y", "kappa");

    private Long id;
    private String target;
    private String channel;
    private String channelId;
}
