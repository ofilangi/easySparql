FROM inraep2m2/scala-sbt:1.0.1

LABEL author="Olivier Filangi"
LABEL mail="olivier.filangi@inrae.fr"
ENV MILL_VERSION="0.10.4"

COPY . /discovery/
WORKDIR /discovery/service-discovery-proxy
RUN curl -L https://github.com/com-lihaoyi/mill/releases/download/${MILL_VERSION}/${MILL_VERSION} > mill &&\
    chmod +x mill &&\
    ./mill app.test # first time download and build every thing about discovery and test proxy !

CMD ["./mill","-w","app.runBackground"]