FROM ubuntu

ADD ./postgres.pp /opt/pp/

RUN apt-get update && \
    apt-get dist-upgrade -y && \
    apt-get install -y puppet && \
    puppet module install puppetlabs/postgresql && \
    puppet apply /opt/pp/postgres.pp

USER postgres

CMD ["/usr/lib/postgresql/9.3/bin/postgres", "-D", "/var/lib/postgresql/9.3/main", "-c", "config_file=/etc/postgresql/9.3/main/postgresql.conf"]
