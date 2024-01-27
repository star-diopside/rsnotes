package jp.gr.java_conf.stardiopside.rsnotes.web.util;

import lombok.extern.slf4j.Slf4j;
import org.springframework.context.MessageSource;
import org.springframework.context.support.MessageSourceAccessor;
import org.springframework.http.InvalidMediaTypeException;
import org.springframework.http.MediaType;
import org.springframework.lang.Nullable;
import org.springframework.util.StringUtils;
import org.springframework.web.reactive.function.server.ServerRequest;

@Slf4j
public final class WebUtils {

    private WebUtils() {
    }

    public static MediaType parseMediaType(@Nullable String mediaType) {
        return parseMediaType(mediaType, MediaType.APPLICATION_OCTET_STREAM);
    }

    public static MediaType parseMediaType(@Nullable String mediaType, MediaType defaultMediaType) {
        try {
            return StringUtils.hasLength(mediaType)
                    ? MediaType.parseMediaType(mediaType)
                    : defaultMediaType;
        } catch (InvalidMediaTypeException e) {
            log.atDebug().setCause(e).log(e::getMessage);
            return defaultMediaType;
        }
    }

    public static MessageSourceAccessor newMessageSourceAccessor(MessageSource messageSource,
                                                                 ServerRequest request) {
        var locale = request.exchange().getLocaleContext().getLocale();
        return locale == null
                ? new MessageSourceAccessor(messageSource)
                : new MessageSourceAccessor(messageSource, locale);
    }
}
