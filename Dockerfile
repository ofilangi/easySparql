FROM inraep2m2/scala-sbt:latest

LABEL author="Olivier Filangi"
LABEL mail="olivier.filangi@inrae.fr"
ENV MILL_VERSION="0.10.4"

COPY . /discovery/

WORKDIR /discovery/service-discovery-proxy
RUN curl -L https://github.com/com-lihaoyi/mill/releases/download/${MILL_VERSION}/${MILL_VERSION} > mill
RUN chmod +x mill

# first time download and build every thing about discovery and test proxy !
RUN ./mill app.test

CMD ["./mill","-w","app.runBackground"]