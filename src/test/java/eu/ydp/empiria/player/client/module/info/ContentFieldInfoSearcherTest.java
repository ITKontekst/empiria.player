package eu.ydp.empiria.player.client.module.info;

import com.google.common.base.Optional;
import com.google.common.collect.Lists;
import org.junit.Test;
import org.junit.runner.RunWith;
import org.mockito.InjectMocks;
import org.mockito.Mock;
import org.mockito.runners.MockitoJUnitRunner;

import java.util.List;

import static org.junit.Assert.*;
import static org.mockito.Mockito.when;

@RunWith(MockitoJUnitRunner.class)
public class ContentFieldInfoSearcherTest {

    @InjectMocks
    private ContentFieldInfoSearcher testObj;
    @Mock
    private ContentFieldInfo contentFieldInfo0;
    @Mock
    private ContentFieldInfo contentFieldInfo1;
    @Mock
    private ContentFieldInfo contentFieldInfo2;

    private final static String TAG_NAME_0 = "TAG_NAME_0";
    private final static String TAG_NAME_1 = "TAG_NAME_1";
    private final static String TAG_NAME_2 = "TAG_NAME_2";

    @Test
    public void testFindByTagName_resultIsPesent() {
        // given
        List<ContentFieldInfo> contentFieldInfos = createContentFieldInfos();
        // when
        Optional<ContentFieldInfo> result = testObj.findByTagName(TAG_NAME_1, contentFieldInfos);

        // then
        assertTrue(result.isPresent());
        assertSame(result.get(), contentFieldInfo1);
    }

    @Test
    public void testFindByTagName_resultIsAbsent() {
        // given
        List<ContentFieldInfo> contentFieldInfos = createContentFieldInfos();
        // when
        Optional<ContentFieldInfo> result = testObj.findByTagName("tagTest", contentFieldInfos);

        // then
        assertFalse(result.isPresent());
    }

    private List<ContentFieldInfo> createContentFieldInfos() {
        return Lists.newArrayList(initContentFieldInfo(TAG_NAME_0, contentFieldInfo0), initContentFieldInfo(TAG_NAME_1, contentFieldInfo1),
                initContentFieldInfo(TAG_NAME_2, contentFieldInfo2));
    }

    private ContentFieldInfo initContentFieldInfo(String tagName, ContentFieldInfo contentFieldInfo) {
        when(contentFieldInfo.getTag()).thenReturn(tagName);
        return contentFieldInfo;
    }
}
