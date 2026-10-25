FROM postgres:latest
ARG POSTGRES_LANG=ja_JP
RUN localedef -i $POSTGRES_LANG -c -f UTF-8 -A /usr/share/locale/locale.alias $POSTGRES_LANG.UTF-8
ENV LANG=$POSTGRES_LANG.utf8
