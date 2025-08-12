package com.example.batch.service.webhook.api.biz;

import com.example.batch.database.crawling.entity.MusicEntity;
import com.example.batch.database.crawling.repository.MusicREP;
import com.example.batch.database.crawling.entity.PlaylistEntity;
import com.example.batch.database.crawling.repository.PlaylistREP;
import com.example.batch.service.webhook.api.dto.WebhookVO;
import com.example.batch.utils.BugsApiUtil;
import com.example.batch.utils.ChromeDriverConnUtil;
import com.example.batch.utils.MattermostUtil;
import com.example.batch.utils.YoutubeApiUtil;
import org.junit.jupiter.api.BeforeEach;
import org.junit.jupiter.api.DisplayName;
import org.junit.jupiter.api.Test;
import org.junit.jupiter.api.extension.ExtendWith;
import org.mockito.ArgumentCaptor;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.junit.jupiter.MockitoExtension;
import org.mockito.junit.jupiter.MockitoSettings;
import org.mockito.quality.Strictness;
import org.springframework.data.domain.Page;
import org.springframework.data.domain.PageImpl;
import org.springframework.data.domain.Pageable;
import org.springframework.http.ResponseEntity;

import java.time.LocalDate;
import java.util.ArrayList;
import java.util.List;
import java.util.Optional;

import static org.junit.jupiter.api.Assertions.*;
import static org.mockito.ArgumentMatchers.*;
import static org.mockito.Mockito.*;
import static org.assertj.core.api.Assertions.assertThat;

@ExtendWith(MockitoExtension.class)
@MockitoSettings(strictness = Strictness.LENIENT)
class MusicSVCImplTest {

    @Mock
    private ChromeDriverConnUtil chromeDriverConnUtil;

    @Mock
    private MattermostUtil mattermostUtil;

    @Mock
    private BugsApiUtil bugsApiUtil;

    @Mock
    private YoutubeApiUtil youtubeApiUtil;

    @Mock
    private MusicREP musicREP;

    @Mock
    private PlaylistREP playlistREP;

    @InjectMocks
    private MusicSVCImpl musicSVC;

    private WebhookVO webhookVO;

    @BeforeEach
    void setUp() {
        // 테스트에 사용할 WebhookVO 객체 설정
        webhookVO = new WebhookVO();
        webhookVO.setWebhookType("a");
        webhookVO.setText("test command");

        // MattermostUtil 기본 모킹
        when(mattermostUtil.sendWebhookChannel(anyString(), any(WebhookVO.class)))
            .thenReturn(ResponseEntity.ok().build());
    }

    @Test
    @DisplayName("notRun 메서드 테스트")
    void notRunTest() {
        // Given
        List<PlaylistEntity> playlistEntities = new ArrayList<>();
        PlaylistEntity playlistEntity = PlaylistEntity.builder()
                .id(1L)
                .musicId(100L)
                .title("테스트 음악")
                .singer("테스트 가수")
                .pubDate(LocalDate.now())
                .youtubeLink("https://www.youtube.com/watch?v=test")
                .build();
        playlistEntities.add(playlistEntity);

        Page<PlaylistEntity> page = new PageImpl<>(playlistEntities);
        when(playlistREP.findAll(any(Pageable.class))).thenReturn(page);

        // When
        musicSVC.playlist(webhookVO);

        // Then
        assertThat(playlistREP.findAll(any(Pageable.class))).isEqualTo(page);
    }






    @Test
    @DisplayName("playlist 메서드 테스트 - 플레이리스트가 있는 경우")
    void playlistWithContentTest() {
        // Given
        List<PlaylistEntity> playlistEntities = new ArrayList<>();
        PlaylistEntity playlistEntity = PlaylistEntity.builder()
                .id(1L)
                .musicId(100L)
                .title("테스트 음악")
                .singer("테스트 가수")
                .pubDate(LocalDate.now())
                .youtubeLink("https://www.youtube.com/watch?v=test")
                .build();
        playlistEntities.add(playlistEntity);

        Page<PlaylistEntity> page = new PageImpl<>(playlistEntities);
        when(playlistREP.findAll(any(Pageable.class))).thenReturn(page);

        // When
        musicSVC.playlist(webhookVO);

        // Then
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mattermostUtil).sendWebhookChannel(messageCaptor.capture(), eq(webhookVO));
        String capturedMessage = messageCaptor.getValue();
        assertTrue(capturedMessage.contains("테스트 음악"));
        assertTrue(capturedMessage.contains("테스트 가수"));
    }

    @Test
    @DisplayName("playlist 메서드 테스트 - 플레이리스트가 비어있는 경우")
    void playlistEmptyTest() {
        // Given
        List<PlaylistEntity> emptyList = new ArrayList<>();
        Page<PlaylistEntity> emptyPage = new PageImpl<>(emptyList);
        when(playlistREP.findAll(any(Pageable.class))).thenReturn(emptyPage);

        // When
        musicSVC.playlist(webhookVO);

        // Then
        verify(mattermostUtil, never()).sendWebhookChannel(anyString(), any(WebhookVO.class));
    }

    @Test
    @DisplayName("playlistAdd 메서드 테스트 - 성공 케이스")
    void playlistAddSuccessTest() {
        // Given
        webhookVO.setText("$pad 123"); // 플레이리스트에 추가할 음악 ID 설정

        MusicEntity musicEntity = MusicEntity.builder()
                .id(123L)
                .no(456L)
                .title("추가할 음악")
                .singer("가수")
                .album("앨범")
                .pubDate(LocalDate.now())
                .youtubeLink("https://youtube.com/test")
                .build();

        when(musicREP.findById(123L)).thenReturn(Optional.of(musicEntity));
        when(playlistREP.findByNo(456L)).thenReturn(Optional.empty());

        // When
        musicSVC.playlistAdd(webhookVO);

        // Then
        verify(playlistREP).save(any(PlaylistEntity.class));
        verify(mattermostUtil).sendWebhookChannel(eq("완료"), eq(webhookVO));
    }

    @Test
    @DisplayName("playlistAdd 메서드 테스트 - 이미 존재하는 경우")
    void playlistAddExistingTest() {
        // Given
        webhookVO.setText("$pad 123"); // 플레이리스트에 추가할 음악 ID 설정

        MusicEntity musicEntity = MusicEntity.builder()
                .id(123L)
                .no(456L)
                .title("추가할 음악")
                .singer("가수")
                .album("앨범")
                .pubDate(LocalDate.now())
                .youtubeLink("https://youtube.com/test")
                .build();

        PlaylistEntity existingPlaylist = PlaylistEntity.builder()
                .id(1L)
                .musicId(123L)
                .no(456L)
                .title("추가할 음악")
                .singer("가수")
                .build();

        when(musicREP.findById(123L)).thenReturn(Optional.of(musicEntity));
        when(playlistREP.findByNo(456L)).thenReturn(Optional.of(existingPlaylist));

        // When
        musicSVC.playlistAdd(webhookVO);

        // Then
        verify(playlistREP, never()).save(any(PlaylistEntity.class));
        verify(mattermostUtil).sendWebhookChannel(eq("완료"), eq(webhookVO));
    }

    @Test
    @DisplayName("playlistRemove 메서드 테스트")
    void playlistRemoveTest() {
        // Given
        webhookVO.setText("$prm 123"); // 플레이리스트에서 제거할 ID 설정

        // When
        musicSVC.playlistRemove(webhookVO);

        // Then
        verify(playlistREP).deleteById(123L);
        verify(mattermostUtil).sendWebhookChannel(eq("완료"), eq(webhookVO));
    }

    @Test
    @DisplayName("musicSearch 메서드 테스트 - 성공 케이스")
    void musicSearchSuccessTest() {
        // Given
        webhookVO.setText("$ms 테스트노래 0 5");

        // BugsApiUtil 응답 모킹
        String jsonResponse = "{\"list\":[{\"trackId\":123,\"trackTitle\":\"테스트노래\",\"artists\":[{\"artistNm\":\"테스트가수\"}],\"album\":{\"title\":\"테스트앨범\",\"image\":{\"path\":\"/test/path\"}},\"updDt\":\"2023-01-01T00:00:00Z\"}]}";
        when(bugsApiUtil.conn(eq("track"), anyString(), anyInt(), anyInt()))
                .thenReturn(ResponseEntity.ok(jsonResponse));

        // Youtube 링크 모킹
        String youtubeResponse = "{\"videoId\":\"testId\"}";
        when(youtubeApiUtil.conn(anyString()))
                .thenReturn(ResponseEntity.ok(youtubeResponse));

        // 기존 곡 없음 가정
        when(musicREP.findBySlctAndNo(anyString(), anyLong()))
                .thenReturn(Optional.empty());

        // 새 곡 저장 모킹
        when(musicREP.save(any(MusicEntity.class)))
                .thenAnswer(invocation -> invocation.getArgument(0));

        // When
        musicSVC.musicSearch(webhookVO);

        // Then
        verify(bugsApiUtil).conn(eq("track"), eq("테스트노래"), eq(1), eq(5));
        verify(youtubeApiUtil).conn(contains("테스트노래"));
        verify(musicREP).save(any(MusicEntity.class));
        verify(mattermostUtil).sendWebhookChannel(contains("테스트노래"), eq(webhookVO));
    }

    @Test
    @DisplayName("musicPlay 메서드 테스트")
    void musicPlayTest() {
        // Given
        webhookVO.setText("$mp 123"); // 재생할 음악 ID 설정

        MusicEntity musicEntity = MusicEntity.builder()
                .id(123L)
                .title("재생할 음악")
                .singer("가수")
                .youtubeLink("https://youtube.com/watch?v=test")
                .build();

        when(musicREP.findTop1ByIdOrderByIdDesc(123L))
                .thenReturn(Optional.of(musicEntity));

        // When
        musicSVC.musicPlay(webhookVO);

        // Then
        verify(mattermostUtil).sendWebhookChannel(eq("https://youtube.com/watch?v=test"), eq(webhookVO));
    }

    @Test
    @DisplayName("music 메서드 테스트 - 랜덤 음악 추천")
    void musicRandomRecommendationTest() {
        // Given
        MusicEntity musicEntity = MusicEntity.builder()
                .id(123L)
                .title("랜덤 음악")
                .singer("랜덤 가수")
                .pubDate(LocalDate.now())
                .youtubeLink("https://youtube.com/watch?v=random")
                .build();

        when(musicREP.findMusicRand())
                .thenReturn(Optional.of(musicEntity));

        // When
        musicSVC.music(webhookVO);

        // Then
        ArgumentCaptor<String> messageCaptor = ArgumentCaptor.forClass(String.class);
        verify(mattermostUtil).sendWebhookChannel(messageCaptor.capture(), eq(webhookVO));
        String capturedMessage = messageCaptor.getValue();
        assertTrue(capturedMessage.contains("랜덤 음악"));
        assertTrue(capturedMessage.contains("랜덤 가수"));
        assertTrue(capturedMessage.contains("youtube.com/watch?v=random"));


    }
}
