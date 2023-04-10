FROM grafana/grafana:9.3.6

RUN grafana-cli plugins install grafana-piechart-panel

ADD ./config.ini /etc/grafana/config.ini
ADD ./provisioning /etc/grafana/provisioning
ADD ./dashboards /var/lib/grafana/dashboards
