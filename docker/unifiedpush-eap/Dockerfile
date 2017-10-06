# Use latest rhmap/eap image as the base
FROM docker.io/rhmap/eap:latest

RUN mkdir -p $JBOSS_HOME/standalone/data && \
    mkdir -p $JBOSS_HOME/standalone/tmp/vfs/temp && \
    mkdir -p $JBOSS_HOME/standalone/log && \
    chmod -R 777 $JBOSS_HOME/standalone

USER root
ENV UPSDIST=/opt/ups

ADD artifacts/aerogear-unifiedpush-server-* $UPSDIST/

WORKDIR $UPSDIST

RUN mv aerogear-unifiedpush-server*/* .  && \
    rm -Rf aerogear-unifiedpush-server* && \
    unzip migrator/ups-migrator-*.zip -d migrator && \
    mv migrator/ups-migrator*/* migrator/ && \
    touch $UPSDIST/migrator/liquibase.properties && \
    chmod -R 777 $UPSDIST/migrator/liquibase.properties

# Run everything below as aerogear user
USER jboss

# Switch to the working dir $JBOSS_HOME/standalone/deployments
WORKDIR $JBOSS_HOME/standalone/deployments

# copy war files
RUN cp $UPSDIST/servers/unifiedpush-auth-server.war . \
    && cp $UPSDIST/servers/unifiedpush-server-as7.war .


# copy and run startup script
# migration is done inside the startup script before launching the server
COPY entrypoint.sh /opt/
ENTRYPOINT ["/opt/entrypoint.sh"]
