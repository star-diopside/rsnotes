package jp.gr.java_conf.stardiopside.rsnotes.web.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.util.StringUtils;

@Slf4j
public final class WebUtils {

    private WebUtils() {
    }

    public static MediaType parseMediaType(String mediaType) {
        return parseMediaType(mediaType, MediaType.APPLICATION_OCTET_STREAM);
    }

    public static MediaType parseMediaType(String mediaType, MediaType defaultMediaType) {
        try {
            return StringUtils.hasLength(mediaType)
                    ? MediaType.parseMediaType(mediaType)
                    : defaultMediaType;
        } catch (InvalidMediaTypeException e) {
            log.atDebug().setCause(e).log(e::getMessage);
            return defaultMediaType;
        }
    }
}
