# Use latest jboss/eap image as the base
FROM jboss-eap-6/eap64-openshift:1.8-14

# Run everything below as root user
USER root


# Install required packages. We need to enable only required repo
RUN yum install --disablerepo * --enablerepo rhel-7-server-rpms  -y unzip wget  && \
	yum -q clean all

## MYSQL
ENV mysql_module_dir=$JBOSS_HOME/modules/com/mysql/jdbc/main/ \
    mysql_connector_jar=mysql-connector-java-5.1.44.jar \
    NON_PROXY_HOSTS="" \
    HTTP_PROXY_HOST="" \
    HTTP_PROXY_PORT="" \
    HTTPS_PROXY_HOST="" \
    HTTPS_PROXY_PORT=""

RUN mkdir -p ${mysql_module_dir}
RUN wget -O ${mysql_connector_jar} http://search.maven.org/remotecontent\?filepath\=mysql/mysql-connector-java/5.1.44/mysql-connector-java-5.1.44.jar
RUN mv ${mysql_connector_jar} ${mysql_module_dir}
COPY configuration/xml/mysql-module.xml ${mysql_module_dir}/module.xml

# Rename the original configuration file
RUN mv $JBOSS_HOME/standalone/configuration/standalone.xml $JBOSS_HOME/standalone/configuration/standalone.xml.orig

# EAP configuration file ready for HTTPS
ADD configuration/xml/standalone-full-sample.xml $JBOSS_HOME/standalone/configuration/standalone.xml

# Add the certificate.sh script into $JBOSS_HOME/standalone/configuration/certs
ADD configuration/certs/ $JBOSS_HOME/standalone/configuration/certs

# Switch to $JBOSS_HOME/configuration/certs
WORKDIR $JBOSS_HOME/standalone/configuration/certs

# Execute the script to generate self signed certificates
RUN ./certificate.sh

# Switch to the working dir
WORKDIR $JBOSS_HOME

# Expose SSL default port
EXPOSE 8443

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
