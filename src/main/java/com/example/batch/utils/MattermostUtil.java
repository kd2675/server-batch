package com.example.batch.utils;

import com.example.batch.utils.vo.MattermostChannelVO;
import com.example.batch.utils.vo.MattermostPostVO;
import org.springframework.http.ResponseEntity;

public interface MattermostUtil {
    ResponseEntity<MattermostPostVO> send(String message, String channelId);

    ResponseEntity<MattermostPostVO> sendCoinChannel(String message);
    ResponseEntity<MattermostPostVO> sendNewsChannel(String message);
    ResponseEntity<MattermostPostVO> sendNewsFlashChannel(String message);
    ResponseEntity<MattermostPostVO> sendNewsMarketingChannel(String message);

    ResponseEntity<MattermostChannelVO> selectAllChannel(String channelId);
    ResponseEntity delete(String sentId);
}
